package com.yanayanyy.pathhelper.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil

@Service(Service.Level.PROJECT)
@State(name = "PathHelperSettings", storages = [Storage("path-helper.xml")])
class PathHelperSettings : PersistentStateComponent<PathHelperSettings.State> {

    private var state = State()

    val enabled: Boolean get() = state.enabled
    val customPath: String get() = state.customPath

    override fun getState() = state

    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, this.state)
    }

    class State {
        var enabled: Boolean = true
        var customPath: String = ""
    }
}
