error id: 7B24F233620AC68017BC6B6C3469744D
file:///C:/Users/Jayso/Projects_For_Github/pls/hello-world/src/main/scala/app/Main.scala
### scala.reflect.internal.Types$TypeError: illegal cyclic reference involving object Includes

occurred in the presentation compiler.



action parameters:
uri: file:///C:/Users/Jayso/Projects_For_Github/pls/hello-world/src/main/scala/app/Main.scala
text:
```scala
package app

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, Alert, TextField, ComboBox}
import scalafx.Includes._

import scalafx.scene.layout.VBox
import scalafx.scene.layout.HBox
import scalafx.stage.WindowEvent
import scalafx.event.EventHandler

import java.sql.DriverManager
import scalafx.scene.control._
import scalafx.scene.control.Alert
import scalafx.beans.property._

import scalafx.scene.control.TableView
import scalafx.scene.control.TableColumn
import javafx.scene.control.TableCell
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.util.Callback
import scalafx.scene.control.Button
import scalafx.scene.control.cell.TextFieldTableCell
import scalafx.collections.ObservableBuffer
import java.lang.module.ModuleDescriptor.Exports
import javafx.beans.property.ReadOnlyObjectWrapper
import scalafx.scene.input.KeyCode.V
import scalafx.scene.chart.PieChart
import javafx.scene.chart
import scalafx.stage.Stage
import javafx.scene.chart.{BarChart, CategoryAxis, NumberAxis, XYChart, LineChart}

import java.lang.ProcessBuilder
import java.io.File
import scalafx.scene.control.TextArea
import java.net.{HttpURLConnection, URL}
import java.io.{BufferedReader, InputStreamReader, OutputStream}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global



object Main extends JFXApp3 {
  
  val database = new Database()

  case class ExpenseFX(month: StringProperty, category: StringProperty, amount: StringProperty)

  def toFX(expenses: List[Expense]): ObservableBuffer[ExpenseFX] = {
    ObservableBuffer(expenses.map(e =>
      ExpenseFX(
        StringProperty(e.month),
        StringProperty(e.category),
        StringProperty(e.amount.toString())
      )
    ): _*)
  }
  
  override def start(): Unit = {
    // Start the Python server if not already running
    val pythonScript = "src/main/scala/app/ai_server.py" // Make sure this path is correct
    val processBuilder = new ProcessBuilder("python", pythonScript)
    processBuilder.directory(new File(".")) // Set working directory if needed
    processBuilder.inheritIO() // Optional: show server output in console
    processBuilder.start()
    database.init()

    val months_list = (Seq("_","January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"))
    val tables = database.grab_years()  // Convert to ObservableBuffer
    
    // Screens
    def home_screen(stage: Stage, database: Database, months: Seq[String], years: List[String]): Scene = new Scene{
      content = new VBox {
        spacing = 10
        children = Seq(new Label("Scalable Budgets"),
        new Button("View Expenses/Incomes") { onAction = _ => stage.scene = view_expenses(stage, database, months, years) },
        new Button("Add a new year") { onAction = _ => stage.scene = new_table(stage, database) }, 
        new Button("Add Expenses/Income") { onAction = _ => stage.scene = new_expense_screen(stage, database, months, years) },
        new Button("Analyze Expenses") { onAction = _ => stage.scene = analyze_screen(stage, database, months, years) },
        new Button("Assistant") { onAction = _ => stage.scene = assistantScene(stage, database, months, years) }
        )
      } 
    }

    def new_table(stage: Stage, database: Database): Scene = new Scene{
      val new_year = new Button("Add New Year Expenses")
      val user_year = new TextField() {
        promptText = "Enter Year (e.g., 2023)"
      }

      new_year.onAction = handle {
        database.addYear(user_year.getText())
        new Alert(Alert.AlertType.Information) {
          title = s"Expense table for ${user_year.getText()} created"
          headerText = "You created a Table"
        }.showAndWait()
      }

      content = new VBox { 
        spacing = 10
        children = Seq(new Label("Add a Table"), new Label("Which year did it fall under:"), new_year,  user_year, new Button("HOME") { onAction = _ => stage.scene = home_screen(stage, database, months_list, tables) })
      }
    }

    def new_expense_screen(stage: Stage, database: Database, months: Seq[String], tables: List[String]): Scene = new Scene{
        val new_expense = new Button("Add new expense")
        val selected_year = new Label()
        val selected_month = new Label()

        val user_category = new TextField(){
          promptText = "Enter Category (e.g., Food, Transport)"
        }
        val user_expense = new TextField(){
          promptText = "Enter Amount"
        }

        new_expense.onAction = handle {
          database.insertExpense(selected_year.text.value, selected_month.text.value, user_category.getText(), (user_expense.getText()).toDouble)
          new Alert(Alert.AlertType.Information) {
            title = s"Expense for ${selected_year.text.value} Added"
            headerText = s"You added a ${user_category.getText()} expense for ${selected_month.text.value} of ${user_expense.getText()} dollars." 
          }.showAndWait()
        }
        
        content = new VBox { 
        spacing = 10
        children = Seq(new Label("Add an Expense"), new Label("Which year did it fall under:"), new ComboBox[String](tables) { 
          promptText = "Select a Budget Year"
          onAction = _ => {
            selected_year.setText(this.value.value)}
            }, 
          new Label("Which month did it fall under:"), new ComboBox[String](months_list) { 
          promptText = "Select a Month"
          onAction = _ => {
            selected_month.setText(this.value.value)
          }
          }, new Label("What was the expense:"), user_category, new Label("Amount:"),user_expense,new_expense, new Button("HOME") { onAction = _ => stage.scene = home_screen(stage, database, months, tables) })
      }
    }

    def view_expenses(stage: Stage, database: Database, months: Seq[String], years: List[String]): Scene = new Scene{
    // Stuff for Table in View Expenses
    val selected_year = new Label()
    val selected_month = new Label()
        val expensesBuffer = ObservableBuffer[ExpenseFX]()

        val deleteColumn = new javafx.scene.control.TableColumn[ExpenseFX, Unit] {
          
        setText("Actions")
          setPrefWidth(100)

          setCellValueFactory(_ => new ReadOnlyObjectWrapper[Unit](()))

          setCellFactory(new Callback[javafx.scene.control.TableColumn[ExpenseFX, Unit], TableCell[ExpenseFX, Unit]] {
            override def call(param: javafx.scene.control.TableColumn[ExpenseFX, Unit]): TableCell[ExpenseFX, Unit] = {
              new TableCell[ExpenseFX, Unit] {
                val btn = new Button("Delete")

                btn.onAction = _ => {
                  val item = getTableView.getItems.get(getIndex)
                  expensesBuffer -= item
                  database.deleteExpense(selected_year.text.value, item.month.value, item.category.value)
                }

                override def updateItem(item: Unit, empty: Boolean): Unit = {
                  super.updateItem(item, empty)
                  if (empty) {
                    setGraphic(null)
                  } else {
                    setGraphic(btn)
                  }
                }
              }
            }
          })
        }

        val table = new TableView[ExpenseFX](expensesBuffer) {
          columns ++= List(
            new TableColumn[ExpenseFX, String] {
              text = "Month"
              cellValueFactory = _.value.month
            },
            new TableColumn[ExpenseFX, String] {
              text = "Category"
              cellValueFactory = _.value.category
            },
            new TableColumn[ExpenseFX, String] {
              text = "Amount"
              cellValueFactory = _.value.amount
            },
            deleteColumn
          )
        }
        content = new VBox { 
        spacing = 10
        children = Seq(new Label("View your expenses"), new Label("Which year would you like to view:"), new ComboBox[String](tables) { 
          promptText = "Select a Budget Year" 
          onAction = _ => {
            selected_year.setText(this.value.value)
            val expenses = database.viewYear(selected_year.text.value, Option(selected_month.text.value).getOrElse("."))
            table.items = toFX(expenses) 
            } 
          }, 
          new Label("Which month did it fall under:"),  new ComboBox[String](months_list) { 
            promptText = "Select a month"
            onAction = _ => {
              selected_month.setText(this.value.value)
              val expenses = database.viewYear(selected_year.text.value, Option(selected_month.text.value).getOrElse("."))
              table.items = toFX(expenses)            
            }
          }, table, new Button("HOME") { onAction = _ => stage.scene = home_screen(stage, database, months, years) })
      }
    }

    def analyze_screen(stage: Stage, database: Database, months: Seq[String], years: List[String]): scalafx.scene.Scene = new Scene{
      content = new VBox{
        spacing = 10
        children = Seq(new Label("Analyze your expenses"),
        new Button("Categories") { onAction = _ => {stage.scene = pieChartScene(stage, database, years = years, months = months)}},
        new Button("Months"){ onAction = _ => stage.scene = barChartScene(stage, database, years)},
        new Button("Years") {onAction = _ => {stage.scene = histogramChartScene(stage, database, years, months)}},
        new Button("HOME") { onAction = _ => stage.scene = home_screen(stage, database, months, years)}
        )
      }
    }

    def pieChartScene(stage:Stage, database: Database, years: List[String], months: Seq[String]): Scene = {
      val pieChart = new PieChart() {
        title = "Spending by Category"
      }

      lazy val year : ComboBox[String] = new ComboBox[String](years) {
        promptText = "Select a year" 
        onAction = _ =>{ month.setVisible(true)}}
      lazy val month = new ComboBox[String](months) {
        visible = false
        onAction = _ => {
          val data = database.getSpendingByCategory(year.value.value, this.value.value)
          val pieChartData = ObservableBuffer(
            data.map { case (category, amount) =>
              PieChart.Data(category, amount)
            }.toSeq: _*)
          pieChart.setData(pieChartData)
        }
      }

      new Scene {
        content = new VBox{
          spacing = 10
          children = Seq(year, month, pieChart, new Button("Back") {onAction =_ => {stage.scene = analyze_screen(stage, database, months, years)}}
          )
        }
      }
    }

    def barChartScene(stage: Stage, database: Database, years: List[String]): Scene = {
     
      val xAxis = new CategoryAxis()
      xAxis.setLabel("Month")

      val yAxis = new NumberAxis()
      yAxis.setLabel("Amount")

      val barChart = new BarChart[String, Number](xAxis, yAxis)
      barChart.setTitle("Spending by month")

      val monthOrder = Seq(
          "January", "February", "March", "April", "May", "June",
          "July", "August", "September", "October", "November", "December"
        )
        xAxis.setCategories(scalafx.collections.ObservableBuffer(monthOrder: _*))


      val year = new ComboBox[String](years) {
        onAction = _ => {
          val barChartData = database.getBarChartData(this.value.value)
          val sortedData = monthOrder.flatMap(m => barChartData.find(_._1 == m))
          val series = new XYChart.Series[String, Number]()
          series.setName("Total by Month")
          sortedData.foreach { case (cat, amt) =>
            series.getData.add(new XYChart.Data[String, Number](cat, amt))
          }
          barChart.getData.clear()
          barChart.getData.add(series)
        }
      }
      val backButton = new Button("Back") {
        onAction = _ => stage.scene = analyze_screen(stage, database, months_list, years)
      }

      val layout = new VBox(10, year, barChart, backButton)

      new Scene(layout, 600, 400) 
      
    }

    def histogramChartScene(stage:Stage, database: Database, years: List[String], months:Seq[String]):Scene = {
      val xAxis = new CategoryAxis()
      xAxis.setLabel("Year")

      val yAxis = new NumberAxis()
      yAxis.setLabel("Amount")

      val lineChart = new LineChart[String, Number](xAxis, yAxis)
      lineChart.setTitle("Amount by Year")

      val backButton = new Button("Back") {
        onAction = _ => stage.scene = analyze_screen(stage, database, months, years)
      }
      val month: ComboBox[String] = new ComboBox[String](months.filter(m => m != "_")) {onAction = _ =>{
        val data = database.getHistogramData(this.value.value)
        val series = new XYChart.Series[String, Number]()
        series.setName("Total by Month")          
        data.foreach { case (cat, amt) =>
          series.getData.add(new XYChart.Data[String, Number](cat, amt))
        }
        lineChart.getData.clear()
        lineChart.getData.add(series)
      }}

      val layout = new VBox(10, month, lineChart, backButton)

      new Scene(layout, 600, 400) 

    }

    def assistantScene(stage: Stage, database: Database, months: Seq[String], years: List[String]): Scene = {
      val chatArea = new TextArea {
        editable = false
        prefRowCount = 15
        wrapText = true
      }
      val inputField = new TextField {
        promptText = "Ask the assistant..."
      }
      val sendButton = new Button("Send")

      def sendMessageToAI(message: String): Unit = {
    chatArea.appendText(s"You: $message\n")
    inputField.clear()
    Future {
      val url = new URL("http://localhost:5005/chat")
      val conn = url.openConnection().asInstanceOf[HttpURLConnection]
      conn.setRequestMethod("POST")
      conn.setRequestProperty("Content-Type", "application/json")
      conn.setDoOutput(true)
      val payload = s"""{"inputs": "$message"}"""
      val os = conn.getOutputStream
      os.write(payload.getBytes("UTF-8"))
      os.close()
      val response = new BufferedReader(new InputStreamReader(conn.getInputStream))
      val responseStr = Iterator.continually(response.readLine()).takeWhile(_ != null).mkString("\n")
      response.close()
      // Extract the generated text from the JSON response
      val answer = responseStr.split("\"generated_text\":").lift(1)
        .flatMap(_.split("\"").drop(1).headOption)
        .getOrElse("Sorry, I couldn't understand.")
      scalafx.application.Platform.runLater {
        chatArea.appendText(s"Assistant: $answer\n")
      }
    }
  }

      sendButton.onAction = _ => {
        val msg = inputField.text.value.trim
        if (msg.nonEmpty) sendMessageToAI(msg)
      }
      inputField.onAction = _ => sendButton.fire()

      new Scene {
        content = new VBox {
          spacing = 10
          children = Seq(
            new Label("Chat with your AI Assistant"),
            chatArea,
            new HBox(5, inputField, sendButton),
            new Button("Home") { onAction = _ => stage.scene = home_screen(stage, database, months, years) }
          )
        }
      }
    }
    // Set the initial scene
    stage = new JFXApp3.PrimaryStage {
      title = "Budget App"
      scene = home_screen(this, database, months_list, tables)
      width = 800
    }
    
  }

  override def stopApp(): Unit = {
    database.close()
  }
}

```


presentation compiler configuration:
Scala version: 2.13.16
Classpath:
<WORKSPACE>\.bloop\hello-world\bloop-bsp-clients-classes\classes-Metals-zUhhZR6ZR2i5aB2kJtcp4Q== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.10.4\semanticdb-javac-0.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalafx\scalafx_2.13\20.0.0-R31\scalafx_2.13-20.0.0-R31.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\xerial\sqlite-jdbc\3.43.2.0\sqlite-jdbc-3.43.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-base\20\javafx-base-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-controls\20\javafx-controls-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-fxml\20\javafx-fxml-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-graphics\20\javafx-graphics-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-media\20\javafx-media-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-swing\20\javafx-swing-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-web\20\javafx-web-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\slf4j\slf4j-api\2.0.9\slf4j-api-2.0.9.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-base\20\javafx-base-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-controls\20\javafx-controls-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-fxml\20\javafx-fxml-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-graphics\20\javafx-graphics-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-media\20\javafx-media-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-swing\20\javafx-swing-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-web\20\javafx-web-20-win.jar [exists ]
Options:
-Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```

```
#### Short summary: 

scala.reflect.internal.Types$TypeError: illegal cyclic reference involving object Includes