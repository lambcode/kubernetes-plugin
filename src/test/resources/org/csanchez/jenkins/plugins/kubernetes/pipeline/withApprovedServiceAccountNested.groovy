import org.csanchez.jenkins.plugins.kubernetes.pipeline.ServiceAccountSelectorFromLabels
withAllowedServiceAccounts(new ServiceAccountSelectorFromLabels([environment: 'prod'])) { //test that this permission is included in nested scope
    withAllowedServiceAccounts(new ServiceAccountSelectorFromLabels([environment: 'dev'])) {
        podTemplate(serviceAccount: 'prod-$TEST_METHOD_NAME') {
            node(POD_LABEL) {
                containerLog 'jnlp'
            }
        }
    }
}
