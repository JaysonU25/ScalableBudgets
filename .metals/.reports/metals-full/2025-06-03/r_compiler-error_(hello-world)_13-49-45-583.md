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
    database.init()
    val user_year = new TextField() // Add Combo box of years based on tables
    val user_category = new TextField()
    val user_expense = new TextField()

    // Usable Year/Month for buttons
    val selected_year = new Label()
    val months_list = (Seq("_","January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"))
    val selected_month = new Label()

    val tables = database.grab_years()  // Convert to ObservableBuffer
    
    // Stuff for Table in View Expenses
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

    // Buttons
    val new_year = new Button("Add New Year Expenses")
    val new_expense = new Button("Add new expense")

    new_year.onAction = handle {
      database.addYear(user_year.getText())
      new Alert(Alert.AlertType.Information) {
        title = s"Expense table for ${user_year.getText()} created"
        headerText = "You created a Table"
      }.showAndWait()
    }


    new_expense.onAction = handle {
      database.insertExpense(selected_year.text.value, selected_month.text.value, user_category.getText(), (user_expense.getText()).toDouble)
      new Alert(Alert.AlertType.Information) {
        title = s"Expense for ${selected_year.text.value} Added"
        headerText = s"You Added a ${user_category.getText()} expense for ${selected_month.text.value} of ${user_expense.getText()} dollars." 
      }.showAndWait()
    }


    // Screens
    lazy val home_screen: Scene = new Scene{
      content = new VBox {
        spacing = 10
        children = Seq(new Label("Scalable Budgets"),
        new Button("View Expenses") { onAction = _ => stage.scene = view_expenses},
        new Button("AddTable") { onAction = _ => stage.scene = new_table}, 
        new Button("AddExpenses") { onAction = _ => stage.scene = new_expense_screen},
        new Button("Analyze Expenses") { onAction = _ => stage.scene = analyze_screen})
      } 
    }

    lazy val new_table: Scene = new Scene{
      content = new VBox { 
        spacing = 10
        children = Seq(new Label("Add a Table"), new Label("Which year did it fall under:"), new_year,  user_year, new Button("HOME") { onAction = _ => stage.scene = home_screen})
      }
    }

    lazy val new_expense_screen: Scene = new Scene{
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
          }, new Label("What was the expense:"), user_category, new Label("Amount:"),user_expense,new_expense, new Button("HOME") { onAction = _ => stage.scene = home_screen})
      }
    }

    lazy val view_expenses: Scene = new Scene{
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
          }, table, new Button("HOME") { onAction = _ => stage.scene = home_screen})
      }
    }

    lazy val analyze_screen: Scene = new Scene{

      val data = database.getPieChartData("2026") // returns Map[String, Double] = Map("Food" -> 120.0, "Rent" -> 400.0)

      val chartData = ObservableBuffer(
        data.map { case (category, amount) =>
          PieChart.Data(category, amount)
        }.toSeq: _*
      )

      val pieChart = new PieChart(chartData) {
        title = "Spending by Category"
        visible = fal
      }

      content = new VBox{
        spacing = 10
        children = Seq(new Label("Charts"), new ComboBox[String](tables) { 
          promptText = "Select a Budget Year"
          onAction = _ => {
            selected_year.setText(this.value.value)}
            }, pieChart , new Button("HOME") { onAction = _ => stage.scene = home_screen})
      }
    }

    
    stage = new JFXApp3.PrimaryStage {
      title = "Budget App"
      scene = home_screen
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
<WORKSPACE>\.bloop\hello-world\bloop-bsp-clients-classes\classes-Metals-ObcvgGahSTSP3VZGSpPQ-A== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.10.4\semanticdb-javac-0.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalafx\scalafx_2.13\20.0.0-R31\scalafx_2.13-20.0.0-R31.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\xerial\sqlite-jdbc\3.43.2.0\sqlite-jdbc-3.43.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-base\20\javafx-base-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-controls\20\javafx-controls-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-fxml\20\javafx-fxml-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-graphics\20\javafx-graphics-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-media\20\javafx-media-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-swing\20\javafx-swing-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-web\20\javafx-web-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\slf4j\slf4j-api\2.0.9\slf4j-api-2.0.9.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-base\20\javafx-base-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-controls\20\javafx-controls-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-fxml\20\javafx-fxml-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-graphics\20\javafx-graphics-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-media\20\javafx-media-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-swing\20\javafx-swing-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-web\20\javafx-web-20-win.jar [exists ]
Options:
-Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```

```
#### Short summary: 

scala.reflect.internal.Types$TypeError: illegal cyclic reference involving object Includes