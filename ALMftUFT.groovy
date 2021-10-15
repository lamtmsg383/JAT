import groovy.json.JsonSlurperClassic
def callUFT(def testsetID, def almURL,def almUser, def almPass, def botToken, def chatID, def domain, def project, def UFTRunSuccess) {
	def now = ""
	def total = 0
	def passed = 0
	def failed = 0
	def norun = 0
	def tester = ""
	def testsetname = ""
	
	if (UFTRunSuccess == "1") {
	String url = almURL+"/api/authentication/sign-in"
	String response = sh(script: "curl -c qc_cookies.txt -u ${almUser}:${almPass} $url -v", returnStdout: true).trim()
	echo response
	
	String url4 = almURL+"/rest/domains/"+domain+"/projects/"+project+"/test-sets"
	String response4 = sh(script: "curl -G -b qc_cookies.txt -c qc_cookies.txt --data-urlencode \"alt=application/json\" --data-urlencode \"query={id[${testsetID}]}\" --data-urlencode \"fields=name\" -H 'Content-Type: application/json' $url4", returnStdout: true).trim()
	echo response4
	def root4 = new JsonSlurperClassic().parseText(response4)
	testsetname = root4.entities.flatten()[0].Fields[0].values[0].value
	echo testsetname
	
    String url2 = almURL+"/rest/domains/"+domain+"/projects/"+project+"/test-instances"
	String response2 = sh(script: "curl -G -b qc_cookies.txt -c qc_cookies.txt --data-urlencode \"alt=application/json\" --data-urlencode \"query={cycle-id[${testsetID}]}\" --data-urlencode \"fields=id,name,status,exec-time,exec-date,cycle-id,owner\" -H 'Content-Type: application/json' $url2", returnStdout: true).trim()
	echo response2
	def root = new JsonSlurperClassic().parseText(response2)
    def TestInst = root.entities.flatten()
    tester = TestInst[0].Fields.values.value[0][0]
    TestInst.each{ TInt, i -> 
        total++
        def test1 = TInt.Fields.values.value
        if(test1[6][0] == "Passed") {passed++}
        else if (test1[6][0] == "Failed") {failed++}
        else {norun++}
                }
	now = new Date().format("yyyy-MM-dd HH.mm.ss Z")
    
	def text = "[${testsetname} Forwarded from ${tester}]\nPerform the test on: ${now}\nTotal number of test cases performed: ${total}\nNumber of cases passed: ${passed}\nNumber of cases failed: ${failed}\nNumber of cases no run: ${norun}"
	def url3 = "https://api.telegram.org/bot${botToken}/sendMessage"
    def response3 = sh(script: "curl -X POST -H 'Content-Type: application/json' -d '{\"chat_id\": \"${chatID}\", \"text\": \"$text\", \"disable_notification\": true}' $url3", returnStdout: true).trim()
    echo response3
	
	String urlLogout = almURL+"/authentication-point/logout"
	String responseLogout = sh(script: "curl -G -b qc_cookies.txt -c qc_cookies.txt --data-urlencode \"alt=application/json\" -H 'Content-Type: application/json' $urlLogout", returnStdout: true).trim()
	echo responseLogout
	
	} else {
			String url4 = almURL+"/rest/domains/"+domain+"/projects/"+project+"/test-sets"
			String response4 = sh(script: "curl -G -b qc_cookies.txt -c qc_cookies.txt --data-urlencode \"alt=application/json\" --data-urlencode \"query={id[${testsetID}]}\" --data-urlencode \"fields=name\" -H 'Content-Type: application/json' $url4", returnStdout: true).trim()
			echo response4
			def root4 = new JsonSlurperClassic().parseText(response4)
			testsetname = root4.entities.flatten()[0].Fields[0].values[0].value
			echo testsetname
	
			now = new Date().format("yyyy-MM-dd HH.mm.ss Z")
			def text = "UFT failed: [${testsetname} Forwarded from ${tester}]\nPerform the test on: ${now}"
			def url3 = "https://api.telegram.org/bot${botToken}/sendMessage"
			def response3 = sh(script: "curl -X POST -H 'Content-Type: application/json' -d '{\"chat_id\": \"${chatID}\", \"text\": \"$text\", \"disable_notification\": true}' $url3", returnStdout: true).trim()
			echo response3
			
			String urlLogout = almURL+"/authentication-point/logout"
	String responseLogout = sh(script: "curl -G -b qc_cookies.txt -c qc_cookies.txt --data-urlencode \"alt=application/json\" -H 'Content-Type: application/json' $urlLogout", returnStdout: true).trim()
	echo responseLogout
	}
	
}
return this