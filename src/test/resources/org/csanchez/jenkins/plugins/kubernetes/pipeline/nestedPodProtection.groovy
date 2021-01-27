podTemplate(label: 'template1') {
    podTemplate(label: 'template2') {
        node('template1') {
            sh 'pwd'
            container('jnlp') {
                sh 'pwd'
            }
        }
        node('template2') {
            sh 'pwd'
            container('jnlp') {
                sh 'pwd'
            }
        }
    }
}
