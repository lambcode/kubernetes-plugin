podTemplate(nodeUsageMode: 'NORMAL') {
    node(POD_LABEL) {
        containerLog 'jnlp'
    }
}
