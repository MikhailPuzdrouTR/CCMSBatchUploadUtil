package com.tr.ccmsbatchclient.fs

import com.tr.ccmsbatchclient.domain.Collection

trait CollectionsRetriever {

  def getCollectionsByPath(pathToStructuredFolder: String): List[Collection]
}

object CollectionsRetriever {
  def apply(): CollectionsRetriever = new LocalFIleSystemCollectionsRetriever(ContentTypeResolver())
}