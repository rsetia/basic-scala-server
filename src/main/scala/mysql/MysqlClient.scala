package mysql

import java.sql.Timestamp

import models.Errors.{UndefinedError, UsernameAlreadyExistsError}
import models.User
import slick.jdbc.MySQLProfile.api._
import mysql.Tables._
import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object MysqlClient {
  val users = TableQuery[Users]
}

class MysqlClient(db: Database) {
  import MysqlClient._

  def readUserQuery(userId: Int)(implicit ec: ExecutionContext): Future[Option[User]] = {
    db.run(users.filter(_.id === userId).result)
      .map(x => x.map(RowConverter.rowToModel(_)).headOption)
  }

  def writeUserQuery(user: User)(implicit ec: ExecutionContext): Future[Int] = {
    val now = new Timestamp(DateTime.now.getMillis)
    val q = users returning users.map(_.id) += UsersRow(user.id, user.username, now, now)
    db.run(q).transform({
      case s @ Success(_) => s
      case Failure(cause) => cause match {
        case _: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException =>
          Failure(UsernameAlreadyExistsError(user.username))
        case e =>
          Failure(UndefinedError())
      }
    })
  }
}
