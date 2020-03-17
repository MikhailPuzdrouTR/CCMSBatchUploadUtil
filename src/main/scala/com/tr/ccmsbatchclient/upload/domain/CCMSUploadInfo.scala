package com.tr.ccmsbatchclient.upload.domain

sealed trait CCMSUploadInfo {
  def environment: String

  def group: String

  def name: String
}

case class CCMSContentSetUploadInfo(environment: String,
                                    contentSetName: String,
                                    group: String) extends CCMSUploadInfo {
  override def name: String = contentSetName
}

case class CCMSCorpusUploadInfo(environment: String, corpusName: String, group: String) extends CCMSUploadInfo {
  override def name: String = corpusName
}
