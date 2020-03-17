package com.tr.ccmsbatchclient.upload

import akka.actor.ActorSystem
import com.tr.ccmsbatchclient.domain.DataStorage
import com.tr.ccmsbatchclient.upload.archive.ArchiveProxy
import com.tr.ccmsbatchclient.upload.ccms.CCMSClient
import com.tr.ccmsbatchclient.upload.domain.CCMSUploadInfo

trait CCMSUploader {
  def uploadArchiveToCCMS(ccmsUploadInfo: CCMSUploadInfo, documentsContentType: String): Unit
}

object CCMSUploader {

  def apply(dataStorage: DataStorage)(implicit actorSystem: ActorSystem): CCMSUploader = {
    val archiveProxy: ArchiveProxy = ArchiveProxy(dataStorage)
    new CCMSUploaderImpl(archiveProxy, CCMSClient(actorSystem), actorSystem)
  }
}
