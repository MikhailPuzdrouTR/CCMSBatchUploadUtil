package com.tr.ccmsbatchclient.fs

trait ZipPreuploadProcessor {

  def beforeUpload(pathToZip: String): Unit
}

object ZipPreuploadProcessor {
  def apply(): ZipPreuploadProcessor = new ZipPreuploadProcessorImpl()
}