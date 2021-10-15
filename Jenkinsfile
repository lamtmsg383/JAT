def modules = [:]
				String domain = 'ITKV2'
				String project = 'iQMS'
				String testsetID = '1'
				String almURL = 'https://alm.vnpt.vn/qcbin'
				String botToken = '2045297152:AAEZxpEDMKxlUl6YX4lmcMjdmfXkAUVlerg'
				String chatID = '-445345246'
				String UFTRunSuccess = '1'
pipeline {
    agent { label "CLGSP.Window" }
    environment {
      PATH = 'C:\\Program Files\\Git\\usr\\bin;C:\\windows\\system32;C:\\windows\\;C:\\Program Files\\Git\\bin;C:\\curl\\bin'
    }
    stages {
        stage("UFT Testing") {
            steps {
				script {
					try {
						sseBuildAndPublish almDomain: 'ITKV2', almEntityId: '1', almProject: 'iQMS', almServerName: 'ITKV2_ALM', archiveTestResultsMode: 'ONLY_ARCHIVE_FAILED_TESTS_REPORT', clientType: '', credentialsId: 'ITKV2_ALM', description: '', environmentConfigurationId: '', runType: 'TEST_SET', timeslotDuration: '30'
                    } catch (e) {
						echo e
						UFTRunSuccess = '0'
					}
				}
			}
        }
		stage("Send notification to telegram") {
			steps {
				withCredentials(bindings: [usernamePassword(credentialsId: 'ITKV2_ALM', usernameVariable: 'ITKV2_ALM_USERNAME', passwordVariable: 'ITKV2_ALM_PASSWORD')]) {
					script {
						echo UFTRunSuccess
						modules.first = load "ALMftUFT.groovy"
						modules.first.callUFT(testsetID, almURL, "${ITKV2_ALM_USERNAME}", "${ITKV2_ALM_PASSWORD}", botToken, chatID, domain, project, UFTRunSuccess)
					}
				}
			}
        } 
	} 
}
