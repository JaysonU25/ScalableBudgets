error id: file:///C:/Users/Jayso/Projects_For_Github/pls/hello-world/src/main/scala/app/Database.scala:
file:///C:/Users/Jayso/Projects_For_Github/pls/hello-world/src/main/scala/app/Database.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 2732
uri: file:///C:/Users/Jayso/Projects_For_Github/pls/hello-world/src/main/scala/app/Database.scala
text:
```scala
package app

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import javax.naming.spi.DirStateFactory.Result
import scala.collection.mutable.ListBuffer

class Database {
  private var conn: Connection = _
  
  def Database(){
  }

  def init(): Unit = {
    this.conn = DriverManager.getConnection("jdbc:sqlite:budget.db")
  }

  def grab_years(): List[String] = {
    val sql = s"SELECT name FROM sqlite_master WHERE type='table';"
    val stmt = this.conn.createStatement()
    val result = stmt.executeQuery(sql)
    val tables = scala.collection.mutable.ListBuffer[String]()
    while (result.next()) {
      tables += result.getString("name")
    }
    result.close()
    tables.toList
  }

  def addYear(year: String): Unit = {
    // Safe table name creation (very basic sanitization)
    if (!year.matches("[a-zA-Z0-9_]+"))
      throw new IllegalArgumentException("Invalid table name")

    val sql = s"CREATE TABLE IF NOT EXISTS \'$year\' (id INTEGER PRIMARY KEY, month TEXT, category TEXT, amount REAL)"
    println(sql)
    this.conn.createStatement().execute(sql)

  }

  def insertExpense(year: String, month: String, category: String, expense: Double): Unit = {
    val sql = s"INSERT INTO `$year` (month, category, amount) VALUES (?, ?, ?)"
    val stmt: PreparedStatement = conn.prepareStatement(sql)
    stmt.setString(1, month)
    stmt.setString(2, category)
    stmt.setDouble(3, expense)
    println(stmt.toString())
    stmt.executeUpdate()
    stmt.close()
  }

  def deleteExpense(year: String, month: String, category: String): Unit = {
    val sql = s"DELETE FROM `$year` WHERE month = ? AND category = ?"
    val stmt: PreparedStatement = conn.prepareStatement(sql)
    stmt.setString(1, month)
    stmt.setString(2, category)
    stmt.executeUpdate()
    stmt.close()
  }

  def viewYear(year: String, month: String): List[Expense] = {
    val sql = s"SELECT month, category, amount FROM \'$year\' WHERE month like \'%${month}%\'"
    val stmt = conn.createStatement()
    val result = stmt.executeQuery(sql)  // Caller should consume and close ResultSet
    val rows = ListBuffer[Expense]()
    while (result.next()) {
      rows += Expense(
        result.getString("month"),
        result.getString("category"),
        result.getDouble("amount")
      )
    }
    result.close()
    rows.toList
  }

  


  def viewMonth(year: String, month: String): ResultSet = {
    val sql = s"SELECT month, category, amount FROM $year WHERE month = ?"
    val stmt = conn.prepareStatement(sql)
    stmt.setString(1, month)
    stmt.executeQuery()  // Caller should consume and close ResultSet
  }

  @@def getPieChartData(year: String): Map[String, Double] = {
    val result = scala.collection.mutable.Map[String, Double]()
    val sql = s"SELECT category, SUM(amount) FROM `$year` GROUP BY category"
    val rs = conn.prepareStatement(sql).executeQuery()
    while (rs.next()) {
      result += rs.getString(1) -> rs.getDouble(2)
    }
    result.toMap
  }



  def getBarChartData(year: String) = ???
  def getHistogramData() = ???

  def close(): Unit = {
    if (this.conn != null && !this.conn.isClosed) conn.close()
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 