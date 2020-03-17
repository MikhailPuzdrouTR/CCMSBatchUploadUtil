package com.tr.ccmsbatchclient.fs

trait ContentTypeResolver {

  def resolveContentTypeOfCorpus(pathToCorpus: String): String
}

object ContentTypeResolver {
  def apply(): ContentTypeResolver = new ContentTypeResolverImpl()
}