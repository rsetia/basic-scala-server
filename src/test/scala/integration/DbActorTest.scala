package integration

import actors.DbActor
import actors.DbActor.Messages.{ReadUserResponse, WriteUserResponse}
import akka.pattern.ask
import akka.util.Timeout
import helpers.TestModels
import models.User
import mysql.MysqlClient
import org.scalatest.FlatSpec

import scala.concurrent.Await
import scala.concurrent.duration._
import slick.jdbc.MySQLProfile.api._

class DbActorTest extends FlatSpec with TestModels {
  implicit val timeout: Timeout = 5.seconds

  def truncate() = {
    val f = db.run(MysqlClient.users.schema.truncate)
    Await.result(f, 5.seconds)
  }

  def flushall() = {
    val f = client.flushall()
    Await.result(f, 5.seconds)
  }

  def writeUser(user: User): WriteUserResponse = {
    val dbActor = system.actorOf(DbActor.props(mysqlClient, redisClient))
    val result = Await.result(dbActor ? DbActor.Messages.WriteUserRequest(user), 5.seconds)
    result.asInstanceOf[WriteUserResponse]
  }

  def readUser(userId: Int): ReadUserResponse = {
    val dbActor = system.actorOf(DbActor.props(mysqlClient, redisClient))
    val result = Await.result(dbActor ? DbActor.Messages.ReadUserRequest(userId), 5.seconds)
    result.asInstanceOf[ReadUserResponse]
  }

  "A DbActor" should "add a user to the database and cache" in {
    truncate()
    val user = User(0, "test_user")
    val writtenUserMsg = writeUser(user)

    assert(writtenUserMsg.user.username == user.username)

    assert( Await.result(mysqlClient.readUserQuery(writtenUserMsg.user.id), 5.seconds).get == writtenUserMsg.user)
    assert( Await.result(redisClient.getUser(writtenUserMsg.user.id), 5.seconds).get == writtenUserMsg.user)
  }

  it should "fill the cache after a cache miss" in {
    truncate()
    val user = User(0, "test_user")
    val writtenUserMsg = writeUser(user)
    flushall()

    assert( Await.result(mysqlClient.readUserQuery(writtenUserMsg.user.id), 5.seconds).get == writtenUserMsg.user)
    assert( Await.result(redisClient.getUser(writtenUserMsg.user.id), 5.seconds).isEmpty)

    val readWithDbActor = readUser(writtenUserMsg.user.id)
    assert(readWithDbActor.user.get == writtenUserMsg.user)
    assert( Await.result(redisClient.getUser(writtenUserMsg.user.id), 5.seconds).get == writtenUserMsg.user)
  }
}
