# Code Agent Path Helper

![Build](https://github.com/yanayanyy/code-agent-path-helper-plugin/workflows/Build/badge.svg)

一个 JetBrains IDE 插件，为 AI 编程助手（Claude Code、Cursor、Copilot 等）一键复制代码位置引用。

## 功能

在编辑器或项目文件树中右键，选择 **Copy Path for AI Agent**，自动将当前代码位置复制到剪贴板：

| 场景 | 输出格式 |
|------|----------|
| 文件树右键 | `@src/main/kotlin/Main.kt` |
| 编辑器内光标定位 | `@src/main/kotlin/Main.kt#L42` |
| 编辑器内选中多行 | `@src/main/kotlin/Main.kt#L10-L20` |

### 可配置路径基准

在 **Settings > Tools > Path Helper** 中设置自定义基准目录，相对路径将基于该目录计算。留空则默认使用项目根目录。支持手动输入或点击浏览按钮选择文件夹。

## 快捷键

- macOS: `Ctrl + Option + Cmd + C`

## 安装

从 [GitHub Releases](https://github.com/yanayanyy/code-agent-path-helper-plugin/releases/latest) 下载最新版本，在 IDE 中：

<kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Hammerspoon 联动（可选）

配合 [Hammerspoon](https://www.hammerspoon.org/) 可实现：在 IDEA 中按一个快捷键，自动复制路径并输入到 Warp 终端，无需手动粘贴。

### 配置步骤

1. 安装 Hammerspoon：`brew install hammerspoon`
2. 编辑 `~/.hammerspoon/init.lua`，添加以下内容：

```lua
-- 定义你的超键（hyper），例如 Ctrl+Option+Cmd
local hyper = {"ctrl", "alt", "cmd"}
-- 修改为你使用的终端名称：Warp / Terminal / iTerm2 / Alacritty 等
local terminalApp = "Warp"

hs.hotkey.bind(hyper, "1", function()
    local frontApp = hs.application.frontmostApplication()

    if frontApp:name() == "IntelliJ IDEA" then
        -- 触发插件快捷键，复制路径到剪贴板
        hs.eventtap.keyStroke({"ctrl", "alt", "cmd"}, "c")

        hs.timer.doAfter(0.15, function()
            local contextPath = hs.pasteboard.getContents()

            if contextPath and string.sub(contextPath, 1, 1) == "@" then
                -- 切换到终端
                hs.application.launchOrFocus(terminalApp)

                hs.timer.doAfter(0.2, function()
                    hs.eventtap.keyStrokes(contextPath .. " ")
                end)
            else
                hs.alert.show("未能在剪贴板中获取有效路径，请重试！")
            end
        end)
    else
        hs.alert.show("请在 IntelliJ IDEA 中使用此快捷键！")
    end
end)
```

3. 将 `terminalApp` 修改为你使用的终端（`Warp`、`Terminal`、`iTerm2`、`Alacritty` 等）
4. 在 Hammerspoon 菜单栏点击 **Reload Config**
5. 在 IDEA 中按 `Ctrl+Option+Cmd+1`，路径会自动出现在终端中

> 如果你的 IDEA 窗口标题不叫 "IntelliJ IDEA"，可在 Hammerspoon 控制台输入 `hs.application.frontmostApplication():name()` 查看实际名称并修改。

## 开发

```bash
./gradlew runIde        # 启动沙箱 IDE 调试
./gradlew buildPlugin   # 构建插件
```
