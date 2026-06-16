<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# code-agent-path-helper-plugin Changelog

## [Unreleased]

## [1.0.0] - 2026-06-16
### Added
- 一键复制代码位置引用（`@相对路径:行号` 或绝对路径），供 AI 编程助手（Claude Code、Cursor、Copilot 等）使用
- 相对路径 / 绝对路径双模式，带 `enabled` 启用开关与可配置基准目录
- 编辑器右键菜单、项目文件树右键菜单入口
- 行号支持：单行 `#L42`、多行选区 `#L10-L20`
- 快捷键 `Ctrl + Option + Cmd + C`
- Settings > Tools > Path Helper 配置界面

### Added (English)
- One-click copy of code location reference (`@relative/path:line` or absolute path) for AI coding agents (Claude Code, Cursor, Copilot)
- Relative / absolute path dual mode with an enable toggle and a configurable base directory
- Editor right-click menu and project view right-click menu entries
- Line info: single line `#L42`, multi-line selection `#L10-L20`
- Shortcut `Ctrl + Option + Cmd + C`
- Settings UI at Settings > Tools > Path Helper
