import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
}

// changelog 插件（Release draft 步骤）依赖 project.version，否则 getChangelog 报 VersionNotSpecifiedException
// 用 .get() 强制解析为具体值，确保 `gradlew properties --property version` 返回 "1.0.0" 而非 "provider(?)"
version = providers.gradleProperty("pluginVersion").get()

dependencies {
    testImplementation("junit:junit:4.13.2")

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        intellijIdea("2025.2.6.2")
        testFramework(TestFrameworkType.Platform)
    }
}

tasks {
    // 发布到 JetBrains Marketplace 的上传令牌配置
    // 令牌放 ~/.gradle/gradle.properties 的 publishToken=perm:xxxx，或用环境变量 PUBLISH_TOKEN
    // 绝不要把令牌提交进 git
    publishPlugin {
        token = providers.gradleProperty("publishToken")
            .orElse(providers.environmentVariable("PUBLISH_TOKEN"))
        // 默认 default（稳定）channel；版本号带 -beta/-eap 后缀会自动走对应 channel
    }
}
