# Code Agent Path Helper

![Build](https://github.com/yanayanyy/code-agent-path-helper-plugin/workflows/Build/badge.svg)

一个 JetBrains IDE 插件，为 AI 编程助手（Claude Code、Cursor、Copilot 等）一键复制代码位置引用。

## 功能

在编辑器或项目文件树中右键，选择 **Copy Path for AI Agent**，自动将当前代码位置复制到剪贴板，可直接粘贴给 AI 作为上下文引用。

复制内容 = **路径** + **行号**，两部分独立决定：

### 路径（两种模式）

| 模式 | 触发条件 | 输出 | `@` 前缀 |
|------|----------|------|---------|
| **相对路径** | 启用开关 + 配置基准目录 + 文件位于该目录下 | `@src/main/kotlin/Main.kt` | ✅ 加 |
| **绝对路径**（默认） | 其余所有情况 | `/Users/.../src/main/kotlin/Main.kt` | ❌ 不加 |

> 相对路径模式契合 AI 助手的 `@` 引用语法（如 Claude Code）；绝对路径模式则给出完整磁盘路径。

### 行号（取决于操作位置）

| 操作位置 | 行号格式 |
|----------|----------|
| 项目文件树右键 | （无行号） |
| 编辑器内光标定位 | `#L42` |
| 编辑器内选中多行 | `#L10-L20` |

## 配置

**Settings > Tools > Path Helper**：

- **Enable relative path** — 总开关。勾选启用相对路径模式；关闭则一律复制绝对路径。
- **Base directory** — 相对路径的基准目录。勾选开关后此项可用（未勾选时自动置灰）。可填项目内相对路径（如 `src/main`），也可填绝对路径。

> **计算规则**：只有「勾选开关 **且** 文件位于基准目录下」时才输出相对路径（加 `@`）；否则一律输出绝对路径（不加 `@`）。

## 快捷键

- macOS: `Ctrl + Option + Cmd + C`（可在 IDE 的 Keymap 中自定义）

## 兼容性

- IntelliJ Platform **2025.2** 及以上
- 适用于所有基于 IntelliJ 平台的 IDE（IntelliJ IDEA、Android Studio、PyCharm、WebStorm、GoLand 等）

## 安装

从 [GitHub Releases](https://github.com/yanayanyy/code-agent-path-helper-plugin/releases/latest) 下载最新版本，在 IDE 中：

<kbd>Settings</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Hammerspoon 联动（可选）

配合 [Hammerspoon](https://www.hammerspoon.org/) 可实现：在 IDEA 中按一个快捷键，自动复制路径并输入到终端，无需手动粘贴。

### 配置步骤

1. 安装 Hammerspoon：`brew install hammerspoon`
2. 编辑 `~/.hammerspoon/init.lua`，添加以下内容：

```lua
-- 定义你的超键（hyper），此处用 Ctrl+Option+Cmd（想要四键则加上 "shift"）
local hyper = {"ctrl", "alt", "cmd"}
-- 修改为你使用的终端：Zed / Warp / Terminal / iTerm2 / Alacritty 等
local terminalApp = "Zed"

hs.hotkey.bind(hyper, "1", function()
    local frontApp = hs.application.frontmostApplication()

    if frontApp:name() == "IntelliJ IDEA" then
        -- 触发插件快捷键（Ctrl+Alt+Cmd+C），复制路径到剪贴板
        hs.eventtap.keyStroke({"ctrl", "alt", "cmd"}, "c")

        hs.timer.doAfter(0.15, function()
            local contextPath = hs.pasteboard.getContents()

            -- 校验：相对引用（@ 开头）或绝对路径（/ 开头）均接受
            if contextPath and (string.sub(contextPath, 1, 1) == "@" or string.sub(contextPath, 1, 1) == "/") then
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

3. 将 `terminalApp` 修改为你使用的终端（`Zed`、`Warp`、`Terminal`、`iTerm2`、`Alacritty` 等）
4. 在 Hammerspoon 菜单栏点击 **Reload Config**
5. 在 IDEA 中按 `Hyper + 1`（上例为 `Ctrl+Option+Cmd+1`），路径会自动出现在终端中

> **提示**：若希望联动 AI 助手识别为 `@` 引用，请在插件设置里启用相对路径并将基准目录指向项目根，这样复制出来的是 `@相对路径`。
>
> 如果你的 IDEA 窗口标题不叫 "IntelliJ IDEA"，可在 Hammerspoon 控制台输入 `hs.application.frontmostApplication():name()` 查看实际名称并修改。

## 开发

```bash
./gradlew runIde                                # 启动沙箱 IDE 调试
./gradlew buildPlugin -x buildSearchableOptions # 构建插件（headless 环境跳过 searchable options）
./gradlew verifyPlugin                          # 运行 Plugin Verifier
```

- Java 21、Kotlin 2.4.0、Gradle 9.5.1、IntelliJ Platform Gradle Plugin 2.x
- 目标 IntelliJ 版本：2025.2+
