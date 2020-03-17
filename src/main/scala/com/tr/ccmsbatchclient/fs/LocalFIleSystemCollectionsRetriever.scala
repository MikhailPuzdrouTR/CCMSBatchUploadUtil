package com.tr.ccmsbatchclient.fs
import java.io.File

import com.tr.ccmsbatchclient.domain.{Collection, ContentSet, Corpus}

class LocalFIleSystemCollectionsRetriever(contentTypeResolver: ContentTypeResolver) extends CollectionsRetriever {

  override def getCollectionsByPath(pathToStructuredFolder: String): List[Collection] = {
    val rootDirectory = new File(pathToStructuredFolder)
    if (!rootDirectory.exists() || !rootDirectory.isDirectory) {
      throw new RuntimeException(s"Root archives directory ($pathToStructuredFolder) doesn't exist or is not a directory. Please check the path")
    }

    if (rootDirectory.list().isEmpty) {
      throw new RuntimeException("Input directory is empty")
    }

    rootDirectory.listFiles()
      .map(groupFolder => (groupFolder.getName, groupFolder))
      .flatMap { case (group, groupDirectory) => buildListOfCollectionsForGroup(group, groupDirectory) }
      .toList
  }

  def buildListOfCollectionsForGroup(group: String, groupDirectory: File): List[Collection] = {
    val corpusPath = groupDirectory.getPath + File.separator + "corpus"
    val contentSetPath = groupDirectory.getPath + File.separator + "content-set"

    val corpuses = getSubFoldersNames(new File(corpusPath))
      .map(corpusFileName => Collection(Corpus(), corpusFileName.replace(".zip", ""), group, s"$corpusPath${File.separator}$corpusFileName", null))
      .map(corpus => corpus.copy(contentType = contentTypeResolver.resolveContentTypeOfCorpus(corpus.pathInFileSystem)))
    val contentSets = getSubFoldersNames(new File(contentSetPath))
      .map(contentSetFileName => Collection(ContentSet(), contentSetFileName.replace(".zip", ""), group, s"$contentSetPath${File.separator}$contentSetFileName", null))

    corpuses ++ contentSets
  }

  def getSubFoldersNames(directory: File): List[String] = if (directory.exists() && directory.isDirectory) directory.listFiles().filter(_.isFile).map(_.getName).toList else List()
}
