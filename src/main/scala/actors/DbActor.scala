package actors

import actors.DbActor.Messages.{ReadUserRequest, ReadUserResponse, WriteUserRequest, WriteUserResponse}
import akka.actor.Status.Failure
import akka.actor.{Actor, ActorRef, Props}
import models.User
import mysql.MysqlClient
import akka.pattern.pipe
import models.Errors.UndefinedError
import redis.ServiceRedisClient

import scala.concurrent.ExecutionContext

object DbActor {
  def props(mysqlClient: MysqlClient, redisClient: ServiceRedisClient)(implicit ec: ExecutionContext): Props =
    Props(new DbActor(mysqlClient, redisClient))

  object Messages {
    case class ReadUserRequest(userId: Int)
    case class ReadUserResponse(user: Option[User])

    case class WriteUserRequest(user: User)
    case class WriteUserResponse(user: User)
  }
}

class DbActor(mysqlClient: MysqlClient, redisClient: ServiceRedisClient)(implicit ec: ExecutionContext) extends Actor {
  def receive: Receive = {
    case req: ReadUserRequest =>
      pipe(redisClient.getUser(req.userId)) to self
      context become handleReadUserResponsesFromCache(sender(), req.userId)
    case req: WriteUserRequest =>
      pipe(mysqlClient.writeUserQuery(req.user)) to self
      context become handleWriteUserResponses(sender())
  }

  def handleReadUserResponsesFromCache(replyTo: ActorRef, userId: Int): Receive = {
    case res: Option[User] =>
      res match {
        case Some(_) =>
          replyTo ! ReadUserResponse(res)
          context stop self
        case None =>
          pipe(mysqlClient.readUserQuery(userId)) to self
          context become handleReadUserResponsesFromSql(replyTo)
      }
    case Failure(e) =>
      replyTo ! e
      context stop self
    case _ =>
      replyTo ! UndefinedError()
      context stop self
  }

  def handleReadUserResponsesFromSql(replyTo: ActorRef): Receive = {
    case res: Option[User] =>
      res.foreach(u => redisClient.addUser(u))
      replyTo ! ReadUserResponse(res)
      context stop self
    case Failure(e) =>
      replyTo ! e
      context stop self
    case _ =>
      replyTo ! UndefinedError()
      context stop self
  }

  def handleWriteUserResponses(replyTo: ActorRef, writtenUser: Option[User] = None): Receive = {
    case userId: Int =>
      pipe(mysqlClient.readUserQuery(userId)) to self
    case res: Option[User] =>
      pipe(redisClient.addUser(res.get)) to self
      context become handleWriteUserResponses(replyTo, res)
    case res: Boolean =>
      replyTo ! WriteUserResponse(writtenUser.get)
      context stop self
    case Failure(e) =>
      replyTo ! e
      context stop self
    case _ =>
      replyTo ! UndefinedError()
      context stop self
  }
}
