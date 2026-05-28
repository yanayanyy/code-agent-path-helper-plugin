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

## 开发

```bash
./gradlew runIde        # 启动沙箱 IDE 调试
./gradlew buildPlugin   # 构建插件
```
