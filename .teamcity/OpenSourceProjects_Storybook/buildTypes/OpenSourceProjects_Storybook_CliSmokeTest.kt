package OpenSourceProjects_Storybook.buildTypes

import jetbrains.buildServer.configs.kotlin.v2017_2.*
import jetbrains.buildServer.configs.kotlin.v2017_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2017_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2017_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2017_2.triggers.retryBuild
import jetbrains.buildServer.configs.kotlin.v2017_2.triggers.VcsTrigger

object OpenSourceProjects_Storybook_CliTest : BuildType({
    uuid = "b1db1a3a-a4cf-46ea-8f55-98b86611f92f"
    id = "OpenSourceProjects_Storybook_CliTest"
    name = "CLI smoke tests"

    vcs {
        root(OpenSourceProjects_Storybook.vcsRoots.OpenSourceProjects_Storybook_HttpsGithubComStorybooksStorybookRefsHeadsMaster)

    }

    steps {
        script {
            name = "Bootstrap"
            scriptContent = """
                yarn
                yarn bootstrap --core
            """.trimIndent()
            dockerImage = "andthensome/docker-node-rsync"
        }
        script {
            name = "Test"
            scriptContent = "yarn --cwd lib/cli test -s"
            dockerImage = "andthensome/docker-node-rsync"
        }
    }

    triggers {
        vcs {
            quietPeriodMode = VcsTrigger.QuietPeriodMode.USE_DEFAULT
            triggerRules = "-:comment=^TeamCity change:**"
            branchFilter = """
                +:pull/*
                +:release/*
                +:master
                +:dependencies.io-*
            """.trimIndent()
        }
        retryBuild {}
    }

    features {
        commitStatusPublisher {
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:5ffe2d7e-531e-4f6f-b1fc-a41bfea26eaa"
                }
            }
            param("github_oauth_user", "Hypnosphi")
        }
    }

    requirements {
        doesNotContain("env.OS", "Windows")
    }

    cleanup {
        artifacts(days = 1)
    }
})
