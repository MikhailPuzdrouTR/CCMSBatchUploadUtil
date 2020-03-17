package com.tr.ccmsbatchclient.domain

import java.io.File

sealed trait DataStorage

case class LocalDataStorage(path: File = null) extends DataStorage
