error id: 9BCA7FECE87CC6066FBED77CA99AF950
file:///C:/Users/Jayso/Projects_For_Github/pls/hello-world/src/main/scala/app/Main.scala
### java.lang.IndexOutOfBoundsException: -1 is out of bounds (min 0, max 2)

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
import javafx.scene.chart.{BarChart, CategoryAxis, NumberAxis, XYChart}



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

    lazy val analyze_screen: scalafx.scene.Scene{} = new Scene{

      val categorydata = database.getPieChartData("2026") // returns Map[String, Double] = Map("Food" -> 120.0, "Rent" -> 400.0)
      val pieChartData = ObservableBuffer(
        categorydata.map { case (category, amount) =>
          PieChart.Data(category, amount)
        }.toSeq: _*
      )


      val pieChart = new PieChart(pieChartData) {
        title = "Spending by Category"
        visible = false
      }

      val charts = Seq(pieChart)

      content = new VBox{
        spacing = 10
        children = Seq(new Label("Charts"), new ComboBox[String](tables) { 
          promptText = "Select a Budget Year"
          onAction = _ => {
            selected_year.setText(this.value.value)}
            }, new ComboBox[String](Seq("PieChart", "BarChart")) { promptText = "Choose Yor Chart" 
            onAction = _ =>{
                charts.map( y => {y.setVisible(false)}  )
                pieChart.setVisible(true)
            }}, pieChart, new Button("Months"){ onAction = _ => barChartScene(stage, database)},new Button("HOME") { onAction = _ => stage.scene = home_screen})
      }
    }


    def barChartScene(stage: Stage, database: Database): Scene = {
      val years = database.grab_years()
      val year = new ComboBox[String](years)
      try {
        val barChartData = database.getBarChartData(years.get)
        val xAxis = new CategoryAxis()
        xAxis.setLabel("Category")

        val yAxis = new NumberAxis()
        yAxis.setLabel("Amount")

        val series = new XYChart.Series[String, Number]()
        series.setName("Total by Category")

        barChartData.foreach { case (cat, amt) =>
          series.getData.add(new XYChart.Data[String, Number](cat, amt))
        }

        val barChart = new BarChart[String, Number](xAxis, yAxis)
        barChart.setTitle("Spending by month")
        barChart.getData.add(series)

        val backButton = new Button("Back") {
          onAction = _ => stage.scene = home_screen
        }

        val layout = new VBox(10, barChart, backButton)

        new Scene(layout, 600, 400) 
      } catch {
        case _ : Throwable =>
          new Alert(Alert.AlertType.Error) {
            title = s"Error fetching data"
            headerText = "You must create add a budget year to anaylyze data!"
          }.showAndWait()
          home_screen
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
<WORKSPACE>\.bloop\hello-world\bloop-bsp-clients-classes\classes-Metals-zUhhZR6ZR2i5aB2kJtcp4Q== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.10.4\semanticdb-javac-0.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalafx\scalafx_2.13\20.0.0-R31\scalafx_2.13-20.0.0-R31.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\xerial\sqlite-jdbc\3.43.2.0\sqlite-jdbc-3.43.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-base\20\javafx-base-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-controls\20\javafx-controls-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-fxml\20\javafx-fxml-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-graphics\20\javafx-graphics-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-media\20\javafx-media-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-swing\20\javafx-swing-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-web\20\javafx-web-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\slf4j\slf4j-api\2.0.9\slf4j-api-2.0.9.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-base\20\javafx-base-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-controls\20\javafx-controls-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-fxml\20\javafx-fxml-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-graphics\20\javafx-graphics-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-media\20\javafx-media-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-swing\20\javafx-swing-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-web\20\javafx-web-20-win.jar [exists ]
Options:
-Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```
scala.collection.generic.CommonErrors$.indexOutOfBounds(CommonErrors.scala:23)
	scala.collection.mutable.ArrayBuffer.apply(ArrayBuffer.scala:102)
	scala.reflect.internal.Types$Type.findMemberInternal$1(Types.scala:1030)
	scala.reflect.internal.Types$Type.findMember(Types.scala:1035)
	scala.reflect.internal.Types$Type.memberBasedOnName(Types.scala:661)
	scala.reflect.internal.Types$Type.nonLocalMember(Types.scala:652)
	scala.tools.nsc.typechecker.Namers$Namer.lookup$1(Namers.scala:551)
	scala.tools.nsc.typechecker.Namers$Namer.isValid$1(Namers.scala:552)
	scala.tools.nsc.typechecker.Namers$Namer.checkSelector$1(Namers.scala:556)
	scala.tools.nsc.typechecker.Namers$Namer.$anonfun$checkSelectors$4(Namers.scala:576)
	scala.tools.nsc.typechecker.Namers$Namer.checkSelectors(Namers.scala:576)
	scala.tools.nsc.typechecker.Namers$Namer.scala$tools$nsc$typechecker$Namers$Namer$$importSig(Namers.scala:1836)
	scala.tools.nsc.typechecker.Namers$Namer$ImportTypeCompleter.completeImpl(Namers.scala:864)
	scala.tools.nsc.typechecker.Namers$LockingTypeCompleter.complete(Namers.scala:2077)
	scala.tools.nsc.typechecker.Namers$LockingTypeCompleter.complete$(Namers.scala:2075)
	scala.tools.nsc.typechecker.Namers$TypeCompleterBase.complete(Namers.scala:2070)
	scala.reflect.internal.Symbols$Symbol.completeInfo(Symbols.scala:1583)
	scala.reflect.internal.Symbols$Symbol.info(Symbols.scala:1548)
	scala.reflect.internal.Symbols$Symbol.initialize(Symbols.scala:1747)
	scala.tools.nsc.typechecker.Typers$Typer.typedStat$1(Typers.scala:3375)
	scala.tools.nsc.typechecker.Typers$Typer.$anonfun$typedStats$10(Typers.scala:3547)
	scala.tools.nsc.typechecker.Typers$Typer.typedStats(Typers.scala:3547)
	scala.tools.nsc.typechecker.Typers$Typer.typedPackageDef$1(Typers.scala:5925)
	scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:6254)
	scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:6344)
	scala.tools.nsc.typechecker.Analyzer$typerFactory$TyperPhase.apply(Analyzer.scala:126)
	scala.tools.nsc.Global$GlobalPhase.applyPhase(Global.scala:483)
	scala.tools.nsc.interactive.Global$TyperRun.applyPhase(Global.scala:1370)
	scala.tools.nsc.interactive.Global$TyperRun.typeCheck(Global.scala:1363)
	scala.tools.nsc.interactive.Global.typeCheck(Global.scala:681)
	scala.meta.internal.pc.Compat.$anonfun$runOutline$1(Compat.scala:74)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:619)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:617)
	scala.collection.AbstractIterable.foreach(Iterable.scala:935)
	scala.meta.internal.pc.Compat.runOutline(Compat.scala:66)
	scala.meta.internal.pc.Compat.runOutline(Compat.scala:35)
	scala.meta.internal.pc.Compat.runOutline$(Compat.scala:33)
	scala.meta.internal.pc.MetalsGlobal.runOutline(MetalsGlobal.scala:36)
	scala.meta.internal.pc.ScalaCompilerWrapper.compiler(ScalaCompilerAccess.scala:18)
	scala.meta.internal.pc.ScalaCompilerWrapper.compiler(ScalaCompilerAccess.scala:13)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$semanticTokens$1(ScalaPresentationCompiler.scala:195)
```
#### Short summary: 

java.lang.IndexOutOfBoundsException: -1 is out of bounds (min 0, max 2)