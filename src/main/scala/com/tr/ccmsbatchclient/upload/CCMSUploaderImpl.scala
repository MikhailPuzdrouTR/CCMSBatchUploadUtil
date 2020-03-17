package com.tr.ccmsbatchclient.upload

import java.io.File

import akka.actor.ActorSystem
import com.tr.ccmsbatchclient.upload.archive.ArchiveProxy
import com.tr.ccmsbatchclient.upload.ccms.CCMSClient
import com.tr.ccmsbatchclient.upload.domain._
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

class CCMSUploaderImpl(proxy: ArchiveProxy, client: CCMSClient, actorSystem: ActorSystem) extends CCMSUploader with LazyLogging {

  val ccmsClient: CCMSClient = client
  val archiveProxy: ArchiveProxy = proxy

  implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

  override def uploadArchiveToCCMS(ccmsUploadInfo: CCMSUploadInfo, documentsContentType: String): Unit = {
    logger.info("Start uploading to CCMS...")
    archiveProxy.processArchive(archive => {
      implicit val ccmsAccessTokenProvider: CCMSAccessTokenProvider = ccmsClient.authorize(ccmsUploadInfo.environment)

      val uploadFuture: Future[CCMSUploadResponse] = processUpload(ccmsClient, ccmsUploadInfo, archive, documentsContentType)

      Await.result(uploadFuture, Duration.Inf) match {
        case okCCMSUploadResponse: OkCCMSUploadResponse =>
          okCCMSUploadResponse.status match {
            case "OK" => logger.info("Uploading completed successfully")
            case "ERROR" => throw CCMSUploadException(s"CCMS failed on processing doucuments: ${okCCMSUploadResponse.errors}")
            case _ => throw CCMSUploadException(s"CCMS Responded unknown status: ${okCCMSUploadResponse.status}")
          }
        case _: AcceptedCCMSUploadResponse => logger.info("CCMS got an archive and continue to process result on background...")
      }
    })
  }

  def processUpload(ccmsClient: CCMSClient, ccmsUploadInfo: CCMSUploadInfo, archive: File, documentsContentType: String)
                   (implicit ccmAccessTokenProvider: CCMSAccessTokenProvider): Future[CCMSUploadResponse] = {
    logger.info(s"Verify that group ${ccmsUploadInfo.group} exists")
    ccmsClient.getGroup(ccmsUploadInfo.group)
      .flatMap(verifyGroupExisting)
      .flatMap { _ => verifyCcmsDocumentStoreExisting(ccmsUploadInfo, documentsContentType) }
      .flatMap { _ => sentArchiveToDocumentsCcmsStore(ccmsUploadInfo, archive) }
  }

  def verifyGroupExisting(group: Option[CCMSGroup]): Future[CCMSGroup] = {
    group match {
      case Some(existingGroup) =>
        logger.info(s"Found group ${existingGroup.name}")
        Future {
          existingGroup
        }
      case None => Future.failed(CCMSGroupNotFoundException(s"Group not found. Please create a group in CCMS or change group name in input properties"))
    }
  }

  def verifyCcmsDocumentStoreExisting(ccmsUploadInfo: CCMSUploadInfo, documentsContentType: String)
                                     (implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[CCMSDocumentsStore] = {
    ccmsUploadInfo match {
      case ccmsContentSetUploadInfo: CCMSContentSetUploadInfo => createCcmsDocumentsStoreIfNotExist(() => {
        ccmsClient.getContentSet(ccmsContentSetUploadInfo.group, ccmsContentSetUploadInfo.contentSetName)
      }, () => {
        logger.info(s"Content-Set ${ccmsContentSetUploadInfo.contentSetName} doesn't exists in CCMS. Try to create new content-set...")
        ccmsClient.createContentSet(CCMSContentSetSetRequest(ccmsContentSetUploadInfo.contentSetName, ccmsContentSetUploadInfo.group, ""))
      })
      case ccmsCorpusUploadInfo: CCMSCorpusUploadInfo => createCcmsDocumentsStoreIfNotExist(
        () => ccmsClient.getCorpus(ccmsCorpusUploadInfo.group, ccmsCorpusUploadInfo.corpusName),
        () => {
          logger.info(s"Corpus ${ccmsCorpusUploadInfo.corpusName} doesn't exists in CCMS. Try to create new corpus...")
          ccmsClient.createCorpus(CCMSCorpusRequest(ccmsCorpusUploadInfo.corpusName, ccmsCorpusUploadInfo.group, "", documentsContentType))
        })
    }
  }

  def createCcmsDocumentsStoreIfNotExist(getCcmsDocumentsStore: () => Future[Option[CCMSDocumentsStore]],
                                         createCcmsDocumentsStore: () => Future[CCMSDocumentsStore]): Future[CCMSDocumentsStore] = {
    getCcmsDocumentsStore().flatMap {
      case Some(documentsStore) =>
        logger.info(s"${documentsStore.name} exists in CCMS")
        Future { documentsStore }
      case None => createCcmsDocumentsStore()
    }
  }

  def sentArchiveToDocumentsCcmsStore(ccmsUploadInfo: CCMSUploadInfo, archive: File) (implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[CCMSUploadResponse] = {
    logger.info(s"Upload archive for ${ccmsUploadInfo.name} collection")
    ccmsUploadInfo match {
      case ccmsCorpusUploadInfo: CCMSCorpusUploadInfo => ccmsClient.uploadArchiveToCorpus(archive, ccmsCorpusUploadInfo)
      case ccmsCollectionUploadInfo: CCMSContentSetUploadInfo => ccmsClient.uploadArchiveToContentSet(archive, ccmsCollectionUploadInfo)
    }
  }
}

