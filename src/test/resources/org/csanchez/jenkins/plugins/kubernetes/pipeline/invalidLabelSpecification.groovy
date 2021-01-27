import org.csanchez.jenkins.plugins.kubernetes.pipeline.WithAllowedServiceAccountsStepExecutionTest
podTemplate(label: 'someLabel') {
    node('someLabel') {
        containerLog 'jnlp'
    }
}
