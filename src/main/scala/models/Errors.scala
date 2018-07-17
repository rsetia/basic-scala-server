package models

object Errors {
  case class UsernameAlreadyExistsError(username: String) extends Exception
  case class UndefinedError() extends Exception
}
