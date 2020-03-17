package com.tr.ccmsbatchclient.upload.ccms

import java.io.File

import com.thomsonreuters.tms.cognito.CognitoHelper
import com.thomsonreuters.tms.cognito.session.{RefreshTokenBasedSession, UserLoginSession}
import com.tr.ccmsbatchclient.upload.ccms.CcmsEntityProtocol._
import com.tr.ccmsbatchclient.upload.ccms.creds.CredentialsLoader
import com.tr.ccmsbatchclient.upload.domain._
import com.tr.ccmsbatchclient.upload.http.HttpClient
import spray.json.DefaultJsonProtocol._

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}

class CCMSClientImpl(client: HttpClient, executionContextExecutor: ExecutionContext, ccmsCredentialsLoader: CredentialsLoader) extends CCMSClient {

  val httpClient: HttpClient = client
  var credentialsLoader: CredentialsLoader = ccmsCredentialsLoader
  implicit val executionContext: ExecutionContext = executionContextExecutor

  override def authorize(environment: String): CCMSAccessTokenProvider = {
    val creds = credentialsLoader.loadCredentials(environment)
    val cognitoHelper: CognitoHelper = new CognitoHelper(creds.poolId, creds.clientId, creds.region)
    val userLoginSession: UserLoginSession = new RefreshTokenBasedSession(creds.refreshToken, cognitoHelper)

    CCMSAccessTokenProvider(userLoginSession.getJWT, creds.host)
  }

  val duration: Duration.Infinite = Duration.Inf

  override def getGroup(groupName: String)
                       (implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[Option[CCMSGroup]] = {
    httpClient.get[List[CCMSGroup]](
      s"${ccmsAccessTokenProvider.host}/api/v1/user/groups",
      accessToken = ccmsAccessTokenProvider.accessToken
    ).map {
      case Some(groups) => groups.find(group => group.name == groupName)
      case None => Option.empty
    }
  }

  override def getContentSet(group: String, contentSetName: String)
                            (implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[Option[CCMSContentSet]] = {
    httpClient.get[CCMSContentSetResponse](
      s"${ccmsAccessTokenProvider.host}/api/v1/content-sets/$group/$contentSetName", ccmsAccessTokenProvider.accessToken)
  }

  override def createContentSet(ccmsContentSet: CCMSContentSetSetRequest)
                               (implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[CCMSContentSet] = {
    val uploadEndpointUrl: String = s"${ccmsAccessTokenProvider.host}/api/v1/content-sets/${ccmsContentSet.group}"
    httpClient
      .post[CCMSContentSetSetRequest, CreatedCCMSContentSetSetResponse](uploadEndpointUrl, ccmsContentSet, ccmsAccessTokenProvider.accessToken)
      .map {
        case Some(createdCollection) => createdCollection
        case None => throw CCMSEntityCreationException(s"Cannot create collection with ${ccmsContentSet.name} for ${ccmsContentSet.group} group")
      }
  }

  override def uploadArchiveToContentSet(archive: File, ccmsUploadInfo: CCMSContentSetUploadInfo)
                                        (implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[CCMSUploadResponse] = {
    httpClient.multipartPost[CCMSUploadResponse](
      s"${ccmsAccessTokenProvider.host}/api/v1/content-sets/${ccmsUploadInfo.group}/${ccmsUploadInfo.contentSetName}/documents/upload",
      archive,
      ccmsAccessTokenProvider.accessToken
    ).map {
      case Some(resp) => resp
      case None => throw CCMSUploadException("Cannot upload the archive to CCMS")
    }
  }

  override def uploadArchiveToCorpus(archive: File, ccmsUploadInfo: CCMSCorpusUploadInfo)
                                    (implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[CCMSUploadResponse] = {
    httpClient.multipartPost[CCMSUploadResponse](
      s"${ccmsAccessTokenProvider.host}/api/v1/corpora/${ccmsUploadInfo.group}/${ccmsUploadInfo.corpusName}/documents/upload",
      archive,
      ccmsAccessTokenProvider.accessToken
    ).map {
      case Some(resp) => resp
      case None => throw CCMSUploadException("Cannot upload the archive to CCMS")
    }
  }

  override def createCorpus(ccmsCorpusRequest: CCMSCorpusRequest)(implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[CCMSCorpus] = {
    val uploadEndpointUrl: String = s"${ccmsAccessTokenProvider.host}/api/v1/corpora/${ccmsCorpusRequest.group}"
    httpClient
      .post[CCMSCorpusRequest, CreatedCCMSCorpusResponse](uploadEndpointUrl, ccmsCorpusRequest, ccmsAccessTokenProvider.accessToken)
      .map {
        case Some(createdCollection) => createdCollection
        case None => throw CCMSEntityCreationException(s"Cannot create corpus with ${ccmsCorpusRequest.name} for ${ccmsCorpusRequest.group} group")
      }
  }

  override def getCorpus(group: String, corpusName: String)(implicit ccmsAccessTokenProvider: CCMSAccessTokenProvider): Future[Option[CCMSCorpus]] = {
    httpClient.get[CCMSCorpusResponse](
      s"${ccmsAccessTokenProvider.host}/api/v1/corpora/$group/$corpusName", ccmsAccessTokenProvider.accessToken)
  }
}


