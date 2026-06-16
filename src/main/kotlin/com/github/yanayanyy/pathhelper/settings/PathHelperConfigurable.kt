package com.yanayanyy.pathhelper.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.ConfigurableProvider
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBUI
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel

class PathHelperConfigurable(private val project: Project) : SearchableConfigurable {

    private val enabledCheckBox = JBCheckBox("Enable relative path (otherwise copy absolute path)")
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
        // 开关联动：未启用时灰掉目录选择框
        enabledCheckBox.addActionListener { customPathField.isEnabled = enabledCheckBox.isSelected }
        return JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.empty(10, 8)
            val gc = GridBagConstraints().apply {
                anchor = GridBagConstraints.WEST
                fill = GridBagConstraints.HORIZONTAL
            }
            // row 0: 启用开关
            gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 3; gc.weightx = 1.0
            gc.insets = JBUI.emptyInsets()
            add(enabledCheckBox, gc)
            // row 1: base directory
            gc.gridwidth = 1
            gc.gridx = 0; gc.gridy = 1; gc.weightx = 0.0
            gc.insets = JBUI.insetsTop(8)
            add(JBLabel("Base directory: "), gc)
            gc.gridx = 1; gc.gridy = 1; gc.weightx = 1.0; gc.gridwidth = 2
            gc.insets = JBUI.emptyInsets()
            add(customPathField, gc)
            // row 2: hint
            gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 3; gc.weightx = 1.0
            gc.insets = JBUI.insetsTop(8)
            add(JBLabel("When enabled and set, relative paths are calculated from this directory (file must be under it). Otherwise the absolute path is copied."), gc)
        }
    }

    override fun isModified(): Boolean {
        val settings = project.getService(PathHelperSettings::class.java)
        return enabledCheckBox.isSelected != settings.enabled || customPathField.text != settings.customPath
    }

    override fun apply() {
        val settings = project.getService(PathHelperSettings::class.java)
        settings.state.enabled = enabledCheckBox.isSelected
        settings.state.customPath = customPathField.text.trim()
    }

    override fun reset() {
        val settings = project.getService(PathHelperSettings::class.java)
        enabledCheckBox.isSelected = settings.enabled
        customPathField.text = settings.customPath
        customPathField.isEnabled = settings.enabled
    }

    class Provider(private val project: Project) : ConfigurableProvider() {
        override fun createConfigurable() = PathHelperConfigurable(project)
        override fun canCreateConfigurable() = !project.isDefault
    }
}
