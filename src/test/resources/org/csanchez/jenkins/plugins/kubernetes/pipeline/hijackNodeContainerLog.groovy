import org.csanchez.jenkins.plugins.kubernetes.pipeline.WithAllowedServiceAccountsStepExecutionTest

node('uniquelabel') {
    WithAllowedServiceAccountsStepExecutionTest.groovyCommunicationLatches.get('$TEST_METHOD_NAME').countDown()
    containerLog('jnlp')
}
