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
        val settings = project.service<PathHelperSettings>()
        val customPath = settings.customPath

        // 未启用或未配置 customPath → 一律绝对路径（不加 @）
        val baseDir = if (settings.enabled && customPath.isNotBlank()) {
            project.guessProjectDir()?.findFileByRelativePath(customPath)
                ?: VfsUtil.findFile(File(customPath).toPath(), true)
        } else {
            null
        }

        // 计算相对路径；文件不在 baseDir 子树下时为 null（此时回退为绝对路径）
        val relativePath = baseDir?.let { VfsUtilCore.getRelativePath(virtualFile, it, '/') }
        val finalPath = relativePath ?: virtualFile.path
        // 相对路径加 @ 前缀，绝对路径不加
        val prefix = if (relativePath != null) "@" else ""

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
            "$prefix$finalPath$lineSuffix"
        } else {
            "$prefix$finalPath"
        }

        CopyPasteManager.getInstance().setContents(StringSelection(result))
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
