package com.yanayanyy.pathhelper.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel

class PathHelperConfigurable : SearchableConfigurable {

    private val project: Project
        get() = ProjectManager.getInstance().openProjects.first()

    private val customPathField = TextFieldWithBrowseButton()

    override fun getId() = "PathHelperConfigurable"

    override fun getDisplayName() = "Path Helper"

    override fun createComponent(): JComponent {
        customPathField.addBrowseFolderListener(
            "Select Base Directory",
            "Choose the directory used as the base for relative paths",
            project,
            FileChooserDescriptorFactory.createSingleFolderDescriptor()
        )
        customPathField.textField.columns = 50
        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.empty(10, 8)
            val gc = GridBagConstraints().apply {
                anchor = GridBagConstraints.WEST
                fill = GridBagConstraints.HORIZONTAL
            }
            gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.0
            add(JBLabel("Base directory: "), gc)
            gc.gridx = 1; gc.gridy = 0; gc.weightx = 1.0; gc.gridwidth = 2
            add(customPathField, gc)
            gc.gridx = 0; gc.gridy = 1; gc.gridwidth = 3; gc.weightx = 1.0; gc.insets = JBUI.insetsTop(8)
            add(JBLabel("Relative paths will be calculated from this directory. Leave empty to use project root."), gc)
        }
    }

    override fun isModified(): Boolean {
        val settings = project.getService(PathHelperSettings::class.java)
        return customPathField.text != settings.customPath
    }

    override fun apply() {
        val settings = project.getService(PathHelperSettings::class.java)
        settings.state.customPath = customPathField.text.trim()
    }

    override fun reset() {
        val settings = project.getService(PathHelperSettings::class.java)
        customPathField.text = settings.customPath
    }
}
