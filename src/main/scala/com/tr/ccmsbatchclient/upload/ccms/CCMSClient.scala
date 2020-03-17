package com.tr.ccmsbatchclient.upload.ccms

import java.io.File

import akka.actor.ActorSystem
import com.tr.ccmsbatchclient.upload.ccms.creds.CredentialsLoader
import com.tr.ccmsbatchclient.upload.domain._
import com.tr.ccmsbatchclient.upload.http.HttpClient

import scala.concurrent.Future

trait CCMSClient {

  def authorize(environment: String): CCMSAccessTokenProvider

  def getGroup(groupName: String)(implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[Option[CCMSGroup]]

  def getContentSet(group: String, contentSetName: String)(implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[Option[CCMSContentSet]]

  def createContentSet(ccmsCollection: CCMSContentSetSetRequest)(implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[CCMSContentSet]

  def createCorpus(ccmsCorpusRequest: CCMSCorpusRequest)(implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[CCMSCorpus]

  def getCorpus(group: String, corpusName: String)(implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[Option[CCMSCorpus]]

  def uploadArchiveToCorpus(archive: File, ccmsUploadInfo: CCMSCorpusUploadInfo)(implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[CCMSUploadResponse]

  def uploadArchiveToContentSet(archive: File, ccmsUploadInfo: CCMSContentSetUploadInfo)(implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[CCMSUploadResponse]
}

object CCMSClient {

  def apply(actorSystem: ActorSystem): CCMSClient = new CCMSClientImpl(HttpClient(actorSystem), actorSystem.dispatcher, CredentialsLoader())
}
