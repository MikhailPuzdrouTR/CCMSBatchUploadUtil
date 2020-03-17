package com.tr.ccmsbatchclient.upload.ccms.creds

import com.tr.ccmsbatchclient.upload.domain.CCMSCredentials
import com.typesafe.config.{Config, ConfigFactory}

class CredentialsLoaderImpl(configPath: String) extends CredentialsLoader {

  val ccmsConfig: Config = ConfigFactory.load(configPath)

  override def loadCredentials(environment: String): CCMSCredentials = {
    CCMSCredentials(
      environment,
      ccmsConfig.getString(s"ccms.$environment.host"),
      ccmsConfig.getString(s"ccms.$environment.poolId"),
      ccmsConfig.getString(s"ccms.$environment.clientId"),
      ccmsConfig.getString(s"ccms.$environment.region"),
      ccmsConfig.getString(s"ccms.$environment.refreshToken")
    )
  }
}

