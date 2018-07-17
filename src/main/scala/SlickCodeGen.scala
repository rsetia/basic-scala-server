import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._

object SlickCodeGen extends App {
  slick.codegen.SourceCodeGenerator.main(
    Array("slick.jdbc.MySQLProfile", "com.mysql.jdbc.Driver", "jdbc:mysql://localhost/main", "./src/main/scala", "mysql", "root", "root")
  )
}
