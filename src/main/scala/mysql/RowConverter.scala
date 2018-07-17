package mysql

import models.User
import Tables._

object RowConverter {
  def rowToModel(row: UsersRow): User = {
    User(row.id, row.username)
  }
}
