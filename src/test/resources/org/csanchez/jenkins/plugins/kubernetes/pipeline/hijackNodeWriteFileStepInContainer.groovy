import org.csanchez.jenkins.plugins.kubernetes.pipeline.WithAllowedServiceAccountsStepExecutionTest

node('uniquelabel') {
    container('jnlp') {
        WithAllowedServiceAccountsStepExecutionTest.groovyCommunicationLatches.get('$TEST_METHOD_NAME').countDown()
        writeFile(file: 'somefile', text: 'somevalue')
    }
}
