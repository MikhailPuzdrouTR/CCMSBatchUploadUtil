package com.tr.ccmsbatchclient.upload.ccms.creds

import com.tr.ccmsbatchclient.upload.domain.CCMSCredentials

trait CredentialsLoader {
  def loadCredentials(environment: String): CCMSCredentials
}

object CredentialsLoader {
  def apply(): CredentialsLoader = new CredentialsLoaderImpl("ccms.conf")
}
