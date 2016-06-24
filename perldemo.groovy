def projectName = 'perldemo';
def gitRemote = "ssh://jenkins@localhost:29418/${projectName}";

def env = System.getenv()
job("${projectName}-gerrit-master") {
	triggers {
        gerrit {
			buildFailed(-1, null)
			buildSuccessful(1, null)
			buildUnstable(-1, null)
            
            events {
				patchsetCreated()
            }

            project(projectName, 'master')
        }
    }

    scm {
    	git {

            branch(env['GERRIT_REFSPEC'])

            remote {
                url(gitRemote)
                refspec("refs/changes/*:refs/changes/*")
            }

            extensions {
                choosingStrategy {
                    gerritTrigger()
                }
            }
        }
    }
    
    steps {
        managedScript("perl_check_syntax") {
        }

        managedScript("perl_critic") {
            arguments('4')
        }

        managedScript("perl_test") {
        }
 	}
}

job("${projectName}-git-master") {
  triggers {
        gerrit {
            buildFailed(-1, null)
            buildSuccessful(1, null)
            buildUnstable(-1, null)
            
            events {
                changeMerged()
            }

            project(projectName, 'master')
        }
    }
    
    steps {
        shell("env")
        managedScript("perl_check_syntax") {
        }

        managedScript("perl_critic") {
            arguments('4')
        }

        managedScript("perl_test") {
        }
    }

    scm {
        git {

            branch('master')
            remote {
                url(gitRemote)
                credentials('jenkins')
            }
        }
    }

    publishers {
        git {
            pushOnlyIfSuccess()
            tag('origin', "release_\${BUILD_ID}") {
                message('Build #\${BUILD_ID}')
                create()
            }
        }
    }

    properties {
        promotions {
            promotion {
                name("DevQA")
                icon("star-red")
                conditions {
                    manual('')
                }
                actions {
                    downstreamParameterized {
                        trigger("prepare-perldemo") {
                            parameters {
                                predefinedProp("ENVIRONMENT","STAGE")
                                predefinedProp("APPLICATION_NAME", "\${PROMOTED_JOB_FULL_NAME}")
                                predefinedProp("BUILD_ID","\${PROMOTED_NUMBER}")
                                predefinedProp("GIT_COMMIT","\${GIT_COMMIT}")
                            }
                        }
                    }
                }
            }
  
            promotion {
                name("Live")
                icon("star-green")
                conditions {
                    manual('')
                    upstream("DevQA")
                }
                actions {
                    downstreamParameterized {
                        trigger("deploy-perldemo") {
                            parameters {
                                predefinedProp("ENVIRONMENT","LIVE")
                                predefinedProp("APPLICATION_NAME", "\${PROMOTED_JOB_FULL_NAME}")
                                predefinedProp("BUILD_ID","\${PROMOTED_NUMBER}")
                            }
                        }
                    }
                }                           
            }
        }
    }
}

job("prepare-perldemo") {
    scm {
        git {
            branch("tags/release_\${BUILD_ID}")
            remote {
                url(gitRemote)
                refspec("refs/tags/release_\${BUILD_ID}")
                credentials('jenkins')
            }
        }
    }

    steps {
        shell("env")
        managedScript("pack_distribution") {
            arguments("perldemo")
            arguments("\$BUILD_ID")
        }

        downstreamParameterized {
            trigger("test-perldemo") {
                parameters {
                    predefinedProp("ENVIRONMENT","\${ENVIRONMENT}")
                    predefinedProp("BUILD_ID","\${BUILD_ID}")
                }
            }
        }
    }
}

job("test-perldemo") {
    scm {
        git {
            branch("tags/release_\${BUILD_ID}")
            remote {
                url(gitRemote)
                refspec("refs/tags/release_\${BUILD_ID}")
                credentials('jenkins')
            }
        }
    }
    
    steps {
        managedScript("tarball_install") {
            arguments("perldemo")
            arguments("\${BUILD_ID}")
        }
        managedScript("starman_restart")
        managedScript("perl_test_integration") {
            arguments("http://localhost:5000")
        }
       
        downstreamParameterized {
            trigger("deploy-perldemo") {
                parameters {
                    predefinedProp("ENVIRONMENT","\${ENVIRONMENT}")
                    predefinedProp("BUILD_ID","\${BUILD_ID}")
                }
            }
        }
    }
}

job("deploy-perldemo") {
    steps {
        managedScript("deploy_remote") {
            arguments("\${ENVIRONMENT}")
        }
    }
}
