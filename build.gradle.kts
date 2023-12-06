import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.papermc.paperweight.tasks.RemapJar
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import java.util.*

plugins {
    idea
    alias(libs.plugins.kotlin)
    alias(libs.plugins.paperweight)
    alias(libs.plugins.shadow)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    paperweight.paperDevBundle(libs.versions.paper)

    compileOnly("io.github.monun:kommand-api:3.1.7")
    compileOnly("io.github.monun:tap-api:4.9.8")
    implementation("io.github.monun:invfx-api:3.3.2")
}

extra.apply {
    set("pluginName", project.name.split('-').joinToString("") {
        it.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
        }
    })
    set("packageName", project.name.replace("-", ""))
    set("kotlinVersion", libs.versions.kotlin)
    set("paperVersion", libs.versions.paper.get().split('.').take(2).joinToString("."))

    val pluginLibraries = LinkedHashSet<String>()

    configurations.findByName("implementation")?.allDependencies?.forEach { dependency ->
        val group = dependency.group ?: error("group is null")
        var name = dependency.name ?: error("name is null")
        var version = dependency.version

        if (group == "org.jetbrains.kotlin" && version == null) {
            version = getKotlinPluginVersion()
        } else if (group == "io.github.monun.tap" && name.endsWith("-api")) {
            name = name.removeSuffix("api") + "core"
        }

        requireNotNull(version) { "version is null" }
        require(version != "latest.release") { "version is latest.release" }

        pluginLibraries += "$group:$name:$version"
        set("pluginLibraries", pluginLibraries.joinToString("\n  ") { "- $it" })
    }
}

tasks {
    // generate plugin.yml
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
            expand(extra.properties)
        }
    }

    fun registerJar(
        classifier: String,
        source: Any
    ) = register<Copy>("test${classifier.capitalized()}Jar") {
        from(source)

        val prefix = project.name
        val plugins = rootProject.file(".server/plugins-$classifier")
        val update = File(plugins, "update")
        val regex = Regex("($prefix).*(.jar)")

        from(source)
        into(if (plugins.listFiles { _, it -> it.matches(regex) }?.isNotEmpty() == true) update else plugins)

        doLast {
            update.mkdirs()
            File(update, "RELOAD").delete()
        }
    }

    registerJar("dev", jar)
    registerJar("reobf", jar)
}

idea {
    module {
        excludeDirs.add(file(".server"))
    }
}