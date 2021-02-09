package org.jetbrains.intellij

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VerifyTaskSpec : IntelliJPluginSpecBase() {

    @Test
    fun `skip verifying empty directory`() {
        buildFile.groovy("""
            verifyPlugin {
                pluginDirectory = null
            }
        """)

        val result = build(IntelliJPlugin.VERIFY_PLUGIN_TASK_NAME)

        assertTrue(result.output.contains("verifyPlugin NO-SOURCE"))
    }

    @Test
    fun `do not fail on warning by default`() {
        buildFile.groovy("""
            version '1.0'
        """)

        pluginXml.xml("""
            <idea-plugin>
                <name>PluginName</name>
                <description>Lorem ipsum.</description>
                <vendor>JetBrains</vendor>
            </idea-plugin>
        """)

        val result = build(IntelliJPlugin.VERIFY_PLUGIN_TASK_NAME)

        assertTrue(result.output.contains("Description is too short"))
    }

    @Test
    fun `fail on warning if option is disabled`() {
        buildFile.groovy("""
            version '1.0'

            verifyPlugin {
                ignoreWarnings = false
            }
        """)

        pluginXml.xml("""
            <idea-plugin version="2">
                <name>PluginName</name>
                <description>Привет, Мир!</description>
                <vendor>Zolotov</vendor>
            </idea-plugin>
        """)

        val result = buildAndFail(IntelliJPlugin.VERIFY_PLUGIN_TASK_NAME)

        assertTrue(result.output.contains("Description is too short"))
    }


    @Test
    fun `fail on errors by default`() {
        val result = buildAndFail(IntelliJPlugin.VERIFY_PLUGIN_TASK_NAME)

        result.output.contains("Plugin descriptor 'plugin.xml' is not found")
    }

    @Test
    fun `do not fail on errors if option is enabled`() {
        buildFile.groovy("""
            verifyPlugin {
                ignoreFailures = true
            }
        """)

        val result = build(IntelliJPlugin.VERIFY_PLUGIN_TASK_NAME)

        result.output.contains("Plugin descriptor 'plugin.xml' is not found")
    }

    @Test
    fun `do not fail if there are no errors and warnings`() {
        buildFile.groovy("""
            version '1.0'

            verifyPlugin { 
                ignoreWarnings = false 
            }
        """)

        pluginXml.xml("""
            <idea-plugin>
                <name>PluginName</name>
                <description>Lorem ipsum dolor sit amet, consectetur adipisicing elit.</description>
                <vendor>JetBrains</vendor>
                <depends>com.intellij.modules.lang</depends>
            </idea-plugin>
        """)

        val result = build(IntelliJPlugin.VERIFY_PLUGIN_TASK_NAME)

        assertFalse(result.output.contains("Plugin verification"))
    }
}
