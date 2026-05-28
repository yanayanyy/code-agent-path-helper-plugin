package com.yanayanyy.pathhelper.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.yanayanyy.pathhelper.settings.PathHelperSettings
import java.awt.datatransfer.StringSelection
import java.io.File

class CopyAgentPathAction : AnAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.EDT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val customPath = project.service<PathHelperSettings>().customPath

        val baseDir = if (customPath.isNotBlank()) {
            project.guessProjectDir()?.findFileByRelativePath(customPath)
                ?: VfsUtil.findFile(File(customPath).toPath(), true)
        } else {
            project.guessProjectDir()
        }

        val relativePath = if (baseDir != null) {
            VfsUtilCore.getRelativePath(virtualFile, baseDir, '/') ?: virtualFile.path
        } else {
            virtualFile.path
        }

        val editor = e.getData(CommonDataKeys.EDITOR)
        val result = if (editor != null) {
            val selectionModel = editor.selectionModel
            val startLine = editor.document.getLineNumber(selectionModel.selectionStart) + 1
            val endLine = editor.document.getLineNumber(selectionModel.selectionEnd) + 1
            val lineSuffix = if (selectionModel.hasSelection() && startLine != endLine) {
                "#L$startLine-L$endLine"
            } else {
                "#L${editor.caretModel.logicalPosition.line + 1}"
            }
            "@$relativePath$lineSuffix"
        } else {
            "@$relativePath"
        }

        CopyPasteManager.getInstance().setContents(StringSelection(result))
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
