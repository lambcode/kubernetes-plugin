podTemplate(label: 'template1') {
    podTemplate(label: 'template2') {
        node('template1') {
            containerLog('jnlp')
            container('jnlp') {
                containerLog('jnlp')
            }
        }
        node('template2') {
            containerLog('jnlp')
            container('jnlp') {
                containerLog('jnlp')
            }
        }
    }
}
