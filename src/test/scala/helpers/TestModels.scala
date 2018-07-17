package helpers

import akka.actor.ActorSystem
import akka.util.Timeout
import com.redis.RedisClient
import mysql.MysqlClient
import redis.ServiceRedisClient
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext

trait TestModels {
  implicit val system: ActorSystem = ActorSystem("system")
  implicit val ec: ExecutionContext = system.dispatcher

  val db = Database.forConfig("mysql.main")
  val client = RedisClient("localhost", 6379)

  val mysqlClient = new MysqlClient(db)
  val redisClient = new ServiceRedisClient(client)
}
