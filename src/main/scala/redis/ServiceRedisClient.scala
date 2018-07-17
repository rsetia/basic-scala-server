package redis

import akka.actor.ActorSystem
import akka.util.Timeout
import com.redis.RedisClient
import models.User

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import com.redis.serialization.SprayJsonSupport._
import json.JsonSupport

object ServiceRedisClient {
  implicit val system: ActorSystem = ActorSystem("redis-client")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val timeout: Timeout = Timeout(5.seconds)

  def userKey(userId: Int): String = {
    s"u:$userId"
  }
}

class ServiceRedisClient(client: RedisClient) extends JsonSupport {
  import ServiceRedisClient._

  def addUser(user: User): Future[Boolean] = {
    client.set(userKey(user.id), user)
  }

  def getUser(userId: Int): Future[Option[User]] = {
    client.get[User](userKey(userId))
  }
}
