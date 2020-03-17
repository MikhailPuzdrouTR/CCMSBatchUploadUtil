package com.tr.ccmsbatchclient.domain

sealed trait CollectionType

case class Corpus() extends CollectionType

case class ContentSet() extends CollectionType

case class Collection(collectionType: CollectionType, name: String, group: String, pathInFileSystem: String, contentType: String)
