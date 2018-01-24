package com.wijayaprinting.experimental

class PdfWriter {

    /*fun asd() {
        try {
            val pdfData = createLogDocument(items)
            if (pdfData.size != 0) {
                val pdfInFormat = DocFlavor.BYTE_ARRAY.PDF
                val myDoc = SimpleDoc(pdfData, pdfInFormat, null)
                val set = HashPrintRequestAttributeSet()
                val services = PrintServiceLookup.lookupPrintServices(pdfInFormat, set)
                if (services.size > 0) {
                    val service = ServiceUI.printDialog(null, 50, 50, services, services[0], null, set)
                    if (service != null) {
                        val job = service.createPrintJob()
                        job.print(myDoc, set)
                    }
                }
            }
        } catch (e: DocumentException) {
            e.printStackTrace()
        } catch (e: PrintException) {
            e.printStackTrace()
        }
    }

    @Throws(DocumentException::class)
    private fun createLogDocument(logs: List<Log>): ByteArray {
        val document = Document()
        val byteArrayOutputStream = ByteArrayOutputStream()
        PdfWriter.getInstance(document, byteArrayOutputStream)
        document.open()
        //Fill the document with info
        document.close()
        return byteArrayOutputStream.toByteArray()
    }*/
}