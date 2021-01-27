import org.csanchez.jenkins.plugins.kubernetes.pipeline.WithAllowedServiceAccountsStepExecutionTest
import java.util.concurrent.TimeUnit

podTemplate(label: 'uniquelabel') {
    WithAllowedServiceAccountsStepExecutionTest.groovyCommunicationLatches.get('$TEST_METHOD_NAME').await(1, TimeUnit.MINUTES)
}
