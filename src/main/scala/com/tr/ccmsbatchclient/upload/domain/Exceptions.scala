package com.tr.ccmsbatchclient.upload.domain

abstract class CCMSUploadingException(message: String) extends RuntimeException(message)

case class CCMSCredentialsNotRightException() extends CCMSUploadingException("CCMS Credentials not right")

case class CCMSGroupNotFoundException(message: String) extends CCMSUploadingException(message)

case class CCMSEntityCreationException(message: String) extends CCMSUploadingException(message)

case class CCMSUploadException(message: String) extends CCMSUploadingException(message)
