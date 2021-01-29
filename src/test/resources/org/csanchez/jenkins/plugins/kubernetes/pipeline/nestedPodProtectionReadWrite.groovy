podTemplate(label: 'template1') {
    podTemplate(label: 'template2') {
        node('template1') {
            writeFile(file: 'somefile', text: 'somevalue')
            readFile('somefile')
            container('jnlp') {
                writeFile(file: 'somefile', text: 'somevalue')
                readFile('somefile')
            }
        }
        node('template2') {
            writeFile(file: 'somefile', text: 'somevalue')
            readFile('somefile')
            container('jnlp') {
                writeFile(file: 'somefile', text: 'somevalue')
                readFile('somefile')
            }
        }
    }
}
