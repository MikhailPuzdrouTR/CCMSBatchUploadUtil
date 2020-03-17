package com.tr.ccmsbatchclient.upload.ccms

import com.tr.ccmsbatchclient.upload.domain._
import spray.json.JsValue

object CcmsEntityProtocol {

  import spray.json.DefaultJsonProtocol._
  import spray.json.RootJsonFormat

  implicit val ccmsGroupFormat: RootJsonFormat[CCMSGroup] = jsonFormat4(CCMSGroup)
  implicit val ccmsCollectionResponseFormat: RootJsonFormat[CCMSContentSetResponse] = jsonFormat6(CCMSContentSetResponse)
  implicit val ccmsCreatedCollectionResponseFormat: RootJsonFormat[CreatedCCMSContentSetSetResponse] = jsonFormat5(CreatedCCMSContentSetSetResponse)
  implicit val ccmsCollectionRequestFormat: RootJsonFormat[CCMSContentSetSetRequest] = jsonFormat3(CCMSContentSetSetRequest)

  implicit val ccmsCorpusResponseFormat: RootJsonFormat[CCMSCorpusResponse] = jsonFormat7(CCMSCorpusResponse)
  implicit val ccmsCorpusRequestFormat: RootJsonFormat[CCMSCorpusRequest] = jsonFormat4(CCMSCorpusRequest)
  implicit val ccmsCreatedCorpusFormat: RootJsonFormat[CreatedCCMSCorpusResponse] = jsonFormat6(CreatedCCMSCorpusResponse)

  implicit val ccmsErrorMetaFormat: RootJsonFormat[CCMSErrorMeta] = jsonFormat2(CCMSErrorMeta)
  implicit val ccmsErrorForamt: RootJsonFormat[CCMSError] = jsonFormat3(CCMSError)
  implicit val acceptedCCMSUploadResponseFormat: RootJsonFormat[AcceptedCCMSUploadResponse] = jsonFormat3(AcceptedCCMSUploadResponse)
  implicit val okCCMSUploadResponseFormat: RootJsonFormat[OkCCMSUploadResponse] = jsonFormat5(OkCCMSUploadResponse)
  implicit val ccmsUpploadResponseFormat: RootJsonFormat[CCMSUploadResponse] = new RootJsonFormat[CCMSUploadResponse] {
    override def read(json: JsValue): CCMSUploadResponse = {
      json.asJsObject.getFields("errors", "uploaded") match {
        case Seq() => json.convertTo[AcceptedCCMSUploadResponse]
        case _ => json.convertTo[OkCCMSUploadResponse]
      }
    }
    override def write(obj: CCMSUploadResponse): JsValue = ???
  }
}

