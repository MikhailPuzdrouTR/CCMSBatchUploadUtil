package com.tr.ccmsbatchclient.util

import spray.json.{JsArray, JsFalse, JsNumber, JsObject, JsString, JsTrue, JsValue, RootJsonReader}

object MapJsonProtocols {
  implicit def anyJsonFormat = new RootJsonReader[Any] {
    override def read(json: JsValue): Any = json match {
      case JsNumber(n) => n.intValue
      case JsString(s) => s
      case JsTrue => true
      case JsFalse => false
      case JsArray(elements) => elements.map(_.convertTo[Any](this))
      case JsObject(x) => x.map { case (key, value) => (key, value.convertTo[Any](this)) }
      case _ => null
    }
  }

  implicit def mapJsonFormat = new RootJsonReader[Map[String, Any]] {
    override def read(value: JsValue): Map[String, Any] = value match {
      case JsObject(x) => x.map { case (key, value) => (key, value.convertTo[Any]) }
    }
  }

}
