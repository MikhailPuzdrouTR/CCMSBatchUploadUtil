package com.tr.ccmsbatchclient.upload.domain

case class CCMSAccessTokenProvider(accessToken: String, host: String)

case class CCMSGroup(collectionType: String, display: String, name: String, role: String)

trait CCMSDocumentsStore {
  def name: String
  def group: String
  def description: String
}

trait CCMSContentSet extends CCMSDocumentsStore {
}

trait CCMSCorpus extends CCMSDocumentsStore {
  def contentType: String
}

case class CCMSCorpusResponse(name: String, group: String, description: String, contentType: String, createdBy: String, modified: String, documentsCount: Int) extends CCMSCorpus
case class CreatedCCMSCorpusResponse(name: String, group: String, description: String, contentType: String, createdBy: String, modified: String) extends CCMSCorpus
case class CCMSCorpusRequest(name: String, group: String, description: String, contentType: String) extends CCMSCorpus

case class CCMSContentSetResponse(name: String, group: String, description: String, createdBy: String, modified: String, documentsCount: Int) extends CCMSContentSet
case class CreatedCCMSContentSetSetResponse(name: String, group: String, description: String, createdBy: String, modified: String) extends CCMSContentSet
case class CCMSContentSetSetRequest(name: String, group: String, description: String) extends CCMSContentSet

case class CCMSErrorMeta(`_type`: String, documentName: String)
case class CCMSError(message: String, meta: CCMSErrorMeta, `type`: String)

trait CCMSUploadResponse {
  def group: String
  def name: String
  def status: String
}

case class OkCCMSUploadResponse(errors: List[CCMSError], group: String, name: String, status: String, uploaded: List[String]) extends CCMSUploadResponse {}
case class AcceptedCCMSUploadResponse(group: String, name: String, status: String) extends CCMSUploadResponse
