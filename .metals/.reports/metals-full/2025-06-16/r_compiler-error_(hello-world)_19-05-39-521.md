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
    database.init()

    val months_list = (Seq("_","January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"))
    val tables = database.grab_years()  // Convert to ObservableBuffer
    val screenstyle = "-fx-background-color:rgb(81, 119, 78); -fx-padding: 19px; -fx-font-size: 16px;"
    // Screens
    def home_screen(stage: Stage, database: Database, months: Seq[String], years: List[String]): Scene = {
      val homeButtonStyle = "-fx-font-size: 24px; -fx-background-color: transparent; -fx-text-fill: black; -fx-border-color: transparent;"
      val homeButtonStyleHover = "-fx-font-size: 24px; -fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: transparent;"

      val rootbox = new VBox {
        maxWidth = Double.MaxValue
        maxHeight = Double.MaxValue
        hgrow = scalafx.scene.layout.Priority.Always
        style = screenstyle
        spacing = 30
        alignment = scalafx.geometry.Pos.TopLeft
        padding = scalafx.geometry.Insets(20)
        style = "-fx-background-color:rgb(81, 119, 78); -fx-padding: 20px; -fx-font-size: 16px;"
        val titleLabel = new Label("Scalable Budgets")
        titleLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: white;")
        children = Seq(
          titleLabel,
          new Button("View Expenses/Incomes") { style = homeButtonStyle
            onAction = _ => stage.scene = view_expenses(stage, database, months, years)
            hover.onChange { (_, _, isHovered) =>
              if (isHovered) this.setStyle(homeButtonStyleHover)
              else this.setStyle(homeButtonStyle)
            }
          },
          new Button("Add a new year") { style = homeButtonStyle
            onAction = _ => stage.scene = new_table(stage, database) 
            hover.onChange { (_, _, isHovered) =>
              if (isHovered) this.setStyle(homeButtonStyleHover)
              else this.setStyle(homeButtonStyle)
            }},
          new Button("Add Expenses/Income") { style = homeButtonStyle
            onAction = _ => stage.scene = new_expense_screen(stage, database, months, years) 
            hover.onChange { (_, _, isHovered) =>
              if (isHovered) this.setStyle(homeButtonStyleHover)
              else this.setStyle(homeButtonStyle)
            }},
          new Button("Analyze Expenses") { style = homeButtonStyle
            onAction = _ => stage.scene = analyze_screen(stage, database, months, years) 
            hover.onChange { (_, _, isHovered) =>
              if (isHovered) this.setStyle(homeButtonStyleHover)
              else this.setStyle(homeButtonStyle)
            }},
          new Button("Budgeting Tips") { style = homeButtonStyle
            onAction = _ => stage.scene = budgetTips(stage, database, months, years) 
            hover.onChange { (_, _, isHovered) =>
              if (isHovered) this.setStyle(homeButtonStyleHover)
              else this.setStyle(homeButtonStyle)
            }},
          new Button("Help") { style = homeButtonStyle
            onAction = _ => stage.scene = helpPage(stage, database, months, years) 
            hover.onChange { (_, _, isHovered) =>
              if (isHovered) this.setStyle(homeButtonStyleHover)
              else this.setStyle(homeButtonStyle)
            }},
          new Button("Assistant") { style = homeButtonStyle
            onAction = _ => new Alert(Alert.AlertType.Information) {
              title = "AI Assistant"
              headerText = "Chat with your AI Assistant, Development in Progress"
              contentText = "This feature is under development. Please check back later."
            }.showAndWait()
            hover.onChange { (_, _, isHovered) =>
              if (isHovered) this.setStyle(homeButtonStyleHover)
              else this.setStyle(homeButtonStyle)
            }
          }
        )
      } 
      val scene = new Scene {
        root = rootbox
      }
    

      rootbox.prefWidthProperty <== scene.width
      rootbox.prefHeightProperty <== scene.height
      scene
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
      val add_table_text = new Label("Add a new year to your budget")
      add_table_text.setStyle("-fx-font-size: 24px; -fx-text-fill: white;")
      val which_year = new Label("Which year would you like to add?")
      which_year.setStyle(" -fx-text-fill: black;")

      root = new VBox { 
        maxWidth = Double.MaxValue
        maxHeight = Double.MaxValue
        hgrow = scalafx.scene.layout.Priority.Always
        style = screenstyle
      
        spacing = 10
        children = Seq(add_table_text, which_year, new_year,  user_year, new Button("HOME") { onAction = _ => stage.scene = home_screen(stage, database, months_list, tables) })
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
        val add_expense_text = new Label("Add a new expense to your budget")
        add_expense_text.setStyle("-fx-font-size: 24px; -fx-text-fill: white;")
        val which_year = new Label("Which year would you like to add?")
        which_year.setStyle(" -fx-text-fill: black;")
        val which_month = new Label("Which month would you like to add?")
        which_month.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-border-color: transparent;")
        val which_expense = new Label("What was the expense?")
        which_expense.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-border-color: transparent;")
        val amount = new Label("What was the amount?")
        amount.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-border-color: transparent;")
        root = new VBox { 
          maxWidth = Double.MaxValue
          maxHeight = Double.MaxValue
          style = screenstyle  
          
          spacing = 30
          children = Seq(add_expense_text, which_year, new ComboBox[String](tables) { 
            promptText = "Select a Budget Year"
            onAction = _ => {
              selected_year.setText(this.value.value)
            }
          }, which_month, new ComboBox[String](months_list) { 
            promptText = "Select a Month"
            onAction = _ => {
              selected_month.setText(this.value.value)
            }
          }, which_expense, user_category, amount, user_expense, new_expense, new Button("HOME") { onAction = _ => stage.scene = home_screen(stage, database, months, tables) })
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
          this.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY)
          
        }
        val view_expenses_text = new Label("View your expenses")
        view_expenses_text.setStyle("-fx-font-size: 24px; -fx-text-fill: white;")
        val which_year = new Label("Which year would you like to view?")
        which_year.setStyle(" -fx-text-fill: black;")
        val which_month = new Label("Which month did it fall under?")
        which_month.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-border-color: transparent;")

        root = new VBox { 
          maxWidth = Double.MaxValue
          maxHeight = Double.MaxValue
          style = screenstyle
          
          table.setPlaceholder(new Label("No expenses found for the selected year and month."))
          spacing = 10
          children = Seq(view_expenses_text, which_year, new ComboBox[String](tables) { 
            promptText = "Select a Budget Year" 
            onAction = _ => {
              selected_year.setText(this.value.value)
              val expenses = database.viewYear(selected_year.text.value, Option(selected_month.text.value).getOrElse("."))
              table.items = toFX(expenses) 
              } 
            }, 
            which_month,  new ComboBox[String](months_list) { 
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
      root = new VBox{
        maxWidth = Double.MaxValue
        maxHeight = Double.MaxValue
        style = screenstyle
        
        spacing = 50
        val analyzeLabel = new Label("Analyze your expenses")
        analyzeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;")
        children = Seq(
          analyzeLabel,
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
      pieChart.setStyle("-fx-pie-label-visible: true; -fx-label-fill: black; -fx-background-color: white; -fx-label-fill: black;")
      lazy val year : ComboBox[String] = new ComboBox[String](years) {
        promptText = "Select a year" 
        onAction = _ =>{ 
          if(month.isVisible) {
            val data = database.getSpendingByCategory(this.value.value, month.value.value)
            val pieChartData = ObservableBuffer(
              data.map { case (category, amount) =>
              PieChart.Data(category, amount)
            }.toSeq: _*)
          pieChart.setData(pieChartData)
          } else {
            month.setVisible(true)
          }
        }
      } 
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
        root = new VBox{
          maxWidth = Double.MaxValue
          maxHeight = Double.MaxValue
          
          style = screenstyle
          spacing = 10
          children = Seq(year, month, pieChart, new Button("Back") {onAction =_ => {stage.scene = analyze_screen(stage, database, months, years)}}
          )
        }
      }
    }

    def barChartScene(stage: Stage, database: Database, years: List[String]): Scene = {
     
      val xAxis = new CategoryAxis()
      xAxis.setLabel("Month")
      xAxis.setStyle("-fx-text-fill: black; -fx-background-color: transparent;")


      val yAxis = new NumberAxis()
      yAxis.setLabel("Amount")
      yAxis.setStyle("-fx-label-fill: black; -fx-border-color: transparent;")
      yAxis.setTickLabelFormatter(new javafx.scene.chart.NumberAxis.DefaultFormatter(yAxis, "$", ""))

      val barChart = new BarChart[String, Number](xAxis, yAxis)
      barChart.setTitle("Spending by month")
      barChart.setStyle("-fx-label-fill: black; -fx-background-color: white;")

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
      layout.style = screenstyle
      layout.setMaxHeight(Double.MaxValue)
      layout.setMaxWidth(Double.MaxValue)
      new Scene(layout)

    }

    def histogramChartScene(stage:Stage, database: Database, years: List[String], months:Seq[String]):Scene = {
      val xAxis = new CategoryAxis()
      xAxis.setLabel("Year")

      val yAxis = new NumberAxis()
      yAxis.setLabel("Amount")
      
      val lineChart = new LineChart[String, Number](xAxis, yAxis)
      lineChart.setTitle("Amount by Year")
      lineChart.setStyle("-fx-text-fill: black; -fx-background-color: white;")
      val yearOrder = years.sorted
      xAxis.setCategories(scalafx.collections.ObservableBuffer(yearOrder: _*))
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
      layout.style = screenstyle
      layout.setMaxHeight(Double.MaxValue)
      layout.setMaxWidth(Double.MaxValue)
      new Scene(layout) 

    }

    def helpPage(stage: Stage, database: Database, months: Seq[String], years: List[String]): Scene = {
      val helpLabel = new Label("Help Page")
      helpLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;")
      val first_option = new Label("1. To add a new year, go to 'Add a new year' and enter the year.")
      first_option.setStyle("-fx-text-fill: white;")
      val second_option = new Label("2. To add expenses, go to 'Add Expenses/Income' and fill in the details.")
      second_option.setStyle("-fx-text-fill: white;")
      val third_option = new Label("3. To view expenses, go to 'View Expenses/Incomes' and select the year and month.")
      third_option.setStyle("-fx-text-fill: white;")
      new Scene {
        root = new VBox {
          style = screenstyle
          maxWidth = Double.MaxValue
          maxHeight = Double.MaxValue
          
          spacing = 10
          children = Seq(
            helpLabel,
            first_option,
            second_option,
            third_option,
            new Button("Back") { onAction = _ => stage.scene = home_screen(stage, database, months, years) }
          )
        }
      }
    }

    def budgetTips(stage: Stage, database: Database, months: Seq[String], years: List[String]): Scene = {
      

      lazy val savingsCalculator: HBox = new HBox {
        val income = new TextField { promptText = "Enter your last net check amount:" }
        val savingsGoal = new TextField { promptText = "How much of this would you like to save? i.e 20%" }
        val goal_label = new Label("Savings Goal:")
        goal_label.setStyle("-fx-text-fill: black;")
        spacing = 10
        visible = false
        children = Seq(
          goal_label,
          income,
          savingsGoal,
          new Button("Calculate") {
            onAction = _ => {
              goals.visible = false
              if (income.text.value.nonEmpty && savingsGoal.text.value.nonEmpty) {
                val incomeAmount = income.text.value.toDouble
                val savingsPercentage = savingsGoal.text.value.toDouble / 100
                val savingsAmount = incomeAmount * savingsPercentage
                new Alert(Alert.AlertType.Information) {
                  title = "Savings Calculation"
                  headerText = "Your savings goal has been calculated."
                  contentText = s"You should save $$${savingsAmount} from this check!."
                }.showAndWait()
              }
            }
          }
        )
      }

      lazy val goals : HBox = new HBox { 
        val incomeField = new TextField { promptText = "About how much do you come home with a check?" }
        val savingsField = new TextField { promptText = "How much are you trying to save?" }
        val timeFrame = new TextField { promptText = "By when would you like to reach this goal?"}
        val calculateButton = new Button("Calculate") {
          onAction = _ => {
            savingsCalculator.visible = false
            if (incomeField.text.value.isEmpty || savingsField.text.value.isEmpty || timeFrame.text.value.isEmpty) {
              new Alert(Alert.AlertType.Warning) {
                title = "Input Error"
                headerText = "Please fill in all fields."
                contentText = "All fields are required to calculate your savings goal."
              }.showAndWait()
            }
            if (incomeField.text.value.nonEmpty && savingsField.text.value.nonEmpty && timeFrame.text.value.nonEmpty) {
              val incomeAmount = incomeField.text.value.toDouble
              val savingsAmount = savingsField.text.value.toDouble
              val timeFrameMonths = timeFrame.text.value.toInt
              val monthlySavings = (savingsAmount / timeFrameMonths) / incomeAmount * 100
              if (monthlySavings < 0 || monthlySavings > 100) {
                new Alert(Alert.AlertType.Warning) {
                  title = "Invalid Savings Goal"
                  headerText = "Your savings goal is unrealistic."
                  contentText = "Please adjust your savings goal or timeframe."
                }.showAndWait()
              } else if (monthlySavings > 50) {
                incomeField.clear()
                savingsField.clear()
                timeFrame.clear()
                val savingsAmount = (incomeAmount * monthlySavings / 100).formatted("%.2f")
                new Alert(Alert.AlertType.Information) {
                  title = "Savings Calculation"
                  headerText = "Your savings goal is a little optimistic."
                  contentText = s"You would have to save more than half of your income each month to reach your goal. However if you can, you would save $$${savingsAmount} from this check!. If possible try to extend your time frame or lower your goal."
                }.showAndWait()
              } else {
                incomeField.clear()
                savingsField.clear()
                timeFrame.clear()
                val savingsAmount = (incomeAmount * monthlySavings / 100).formatted("%.2f")
                new Alert(Alert.AlertType.Information) {
                  title = "Savings Calculation"
                  headerText = "Your savings goal is achievable."
                  contentText = s"You should save $$${savingsAmount} from this check!."
                }.showAndWait()
              }
            }
          }
            
          
        }
        maxWidth = Double.MaxValue
        maxHeight = Double.MaxValue
        spacing = 10
        visible = false
        children = Seq(
          new Label("Savings Goal:"),
          incomeField, savingsField, timeFrame, calculateButton
        )
      }

      val budgetingTips = new ComboBox[String](Seq("1. How much should I save?", "2. Set realistic budget goals.")) {
        promptText = "Select a budgeting tip"
        onAction = _ => {
          if(this.value.value == "1. How much should I save?"){
            savingsCalculator.visible = true
            goals.visible = false
          } else {
            savingsCalculator.visible = false
            goals.visible = true
          }
        }
      }
      val budgetingTipsLabel = new Label("Budgeting Tips")
      budgetingTipsLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;")
      new Scene {
        root = new VBox {
          maxWidth = Double.MaxValue
          maxHeight = Double.MaxValue
          style = screenstyle
          
          spacing = 10
          children = Seq(
            budgetingTipsLabel,
            budgetingTips,
            savingsCalculator,
            goals,
            new Button("Back") { onAction = _ => stage.scene = home_screen(stage, database, months, years) }
          )
        }
      }
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
          val apiToken = "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" // Your Hugging Face token
          val model = "mistralai/Mixtral-8x7B-Instruct-v0.1"
          val url = new URL(s"https://api-inference.huggingface.co/models/$model")
          val conn = url.openConnection().asInstanceOf[HttpURLConnection]
          conn.setRequestMethod("POST")
          conn.setRequestProperty("Authorization", s"Bearer $apiToken")
          conn.setRequestProperty("Content-Type", "application/json")
          conn.setDoOutput(true)
          val prompt = s"### Human: $message\n### Assistant:"
          val payload = s"""{"inputs": "$prompt"}"""
          val os = conn.getOutputStream
          os.write(payload.getBytes("UTF-8"))
          os.close()
          val response = new BufferedReader(new InputStreamReader(conn.getInputStream))
          val responseStr = Iterator.continually(response.readLine()).takeWhile(_ != null).mkString("\n")
          response.close()
          val answer = responseStr.split("\"generated_text\":\"").lift(1).flatMap(_.split("\"").headOption).getOrElse("Sorry, I couldn't understand.")
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
        root = new VBox {
          maxWidth = Double.MaxValue
          maxHeight = Double.MaxValue
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
<WORKSPACE>\.bloop\hello-world\bloop-bsp-clients-classes\classes-Metals-pGIzfKxQQaeqzd087klmuQ== [exists ], <HOME>\AppData\Local\bloop\cache\semanticdb\com.sourcegraph.semanticdb-javac.0.10.4\semanticdb-javac-0.10.4.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.16\scala-library-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scalafx\scalafx_2.13\20.0.0-R31\scalafx_2.13-20.0.0-R31.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\xerial\sqlite-jdbc\3.43.2.0\sqlite-jdbc-3.43.2.0.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-reflect\2.13.16\scala-reflect-2.13.16.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-base\20\javafx-base-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-controls\20\javafx-controls-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-fxml\20\javafx-fxml-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-graphics\20\javafx-graphics-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-media\20\javafx-media-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-swing\20\javafx-swing-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-web\20\javafx-web-20.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\slf4j\slf4j-api\2.0.9\slf4j-api-2.0.9.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-base\20\javafx-base-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-controls\20\javafx-controls-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-fxml\20\javafx-fxml-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-graphics\20\javafx-graphics-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-media\20\javafx-media-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-swing\20\javafx-swing-20-win.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\openjfx\javafx-web\20\javafx-web-20-win.jar [exists ]
Options:
-Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```

```
#### Short summary: 

scala.reflect.internal.Types$TypeError: illegal cyclic reference involving object Includes