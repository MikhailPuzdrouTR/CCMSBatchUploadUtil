package com.tr.ccmsbatchclient

import java.io.File

import akka.actor.ActorSystem
import com.tr.ccmsbatchclient.domain.{Collection, ContentSet, Corpus, DataStorage, LocalDataStorage}
import com.tr.ccmsbatchclient.fs.{CollectionsRetriever, ZipPreuploadProcessor}
import com.tr.ccmsbatchclient.upload.CCMSUploader
import com.tr.ccmsbatchclient.upload.domain.{CCMSContentSetUploadInfo, CCMSCorpusUploadInfo, CCMSUploadInfo}
import com.tr.ccmsbatchclient.util.ActorSystemUtil
import com.typesafe.scalalogging.LazyLogging

object Runner extends LazyLogging {

  def main(args: Array[String]): Unit = {
    if (args.length < 1 || args.length > 2) {
      println("Expects 1 or 2 arguments. 1st - path to directory with groups and collections, 2nd - CCMS enviroment (dev, ppe (default))")
    } else {
      val pathToGroups: String = args(0) // "C:\\Bermuda\\CCMS\\Script\\data"
      val ccmsEnv: String = if (args.length == 2) args(1) else "ppe"

      val collectionsRetriever = CollectionsRetriever()

      val collections = collectionsRetriever.getCollectionsByPath(pathToGroups)
      logger.info(s"Found ${collections.length} collections")


      val preProcessor = ZipPreuploadProcessor()
      collections.foreach(collection => preProcessor.beforeUpload(collection.pathInFileSystem))

      ActorSystemUtil.withActorSystem { implicit actorSystem: ActorSystem =>
        collections
          .map(collection => convertCollectionToCCMSUploadInfo(collection, ccmsEnv))
            .foreach { case (ccmsUploadInfo, dataStorage, contentType) => CCMSUploader(dataStorage).uploadArchiveToCCMS(ccmsUploadInfo, contentType)}
      }
    }
  }

  def convertCollectionToCCMSUploadInfo(collection: Collection, env: String): (CCMSUploadInfo, DataStorage, String) = {
    val ccmsUploadInfo: CCMSUploadInfo = collection.collectionType match {
      case Corpus() => CCMSCorpusUploadInfo(env, collection.name, collection.group)
      case ContentSet() => CCMSContentSetUploadInfo(env, collection.name, collection.group)
    }

    val localDataStorage: LocalDataStorage = LocalDataStorage(new File(collection.pathInFileSystem))

    (ccmsUploadInfo, localDataStorage, collection.contentType)
  }
}
