
def createManagedScript = { String scriptName, String... args -> managedScriptConfigFile(scriptName) {
    	def scriptPath = "${JENKINS_HOME}/workspace/jks-jobs/scripts/";
    	content(new File("${scriptPath}${scriptName}.sh").text)
    	if ( args ) {
    		args.each({
    			arguments(it)
    		})
    	}
	}
}

createManagedScript("perl_check_syntax")
createManagedScript("perl_critic", "SEVERITY_LEVEL")
createManagedScript("perl_test")
createManagedScript("perl_check_syntax")
createManagedScript("pack_distribution", "PROJECT_ID", "BUILD_ID")
createManagedScript("perl_test_integration", "APP_URL")
createManagedScript("tarball_install", "PROJECT_ID", "BUILD_ID")
createManagedScript("starman_restart")
createManagedScript("deploy_remote", "ENVIRONMENT")

