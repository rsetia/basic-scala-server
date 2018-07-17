package json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import models.User
import spray.json._


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat = jsonFormat2(User)
}
