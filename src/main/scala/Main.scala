import actors.DbActor
import akka.actor.{ActorSystem, Props}
import json.JsonSupport
import models.User
import mysql.{MysqlClient, RowConverter}
import mysql.Tables._
import redis.ServiceRedisClient
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration._
import slick.jdbc.MySQLProfile.api._
import akka.pattern.ask
import akka.util.Timeout
import com.redis.RedisClient

object Main extends App with JsonSupport {
  implicit val system = ActorSystem("system")
  import system.dispatcher

  val user = User(1, "ravpreet")
  println(user.toJson)

  val client = RedisClient("localhost", 6379)
  val redisClient = new ServiceRedisClient(client)

  val f = redisClient.addUser(user)
  Await.result(f, 5.seconds)

  val g = redisClient.getUser(user.id)
  Await.result(g, 5.seconds)

  val db = Database.forConfig("mysql.main")

  val users = TableQuery[Users]
  val q = users.filter(_.id === user.id)
  val action = q.result
  val dbF = db.run(action)
  val result = Await.result(dbF, 5.seconds)

  val mysqlClient = new MysqlClient(db)
  implicit val timeout: Timeout = 5.seconds

  var dbActor = system.actorOf(DbActor.props(mysqlClient, redisClient))
  val x = Await.result(dbActor ? DbActor.Messages.ReadUserRequest(2), 5.seconds)
  println(x)

  dbActor = system.actorOf(DbActor.props(mysqlClient, redisClient))
  val y = Await.result(dbActor ? DbActor.Messages.WriteUserRequest(User(0,"b")), 5.seconds)
  println(y)

  dbActor = system.actorOf(DbActor.props(mysqlClient, redisClient))
  val z = Await.result(dbActor ? DbActor.Messages.ReadUserRequest(3), 5.seconds)
  println(z)
}
