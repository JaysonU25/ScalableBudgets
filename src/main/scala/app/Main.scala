package app

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, Alert, TextField, ComboBox}
import scalafx.Includes._

import scalafx.scene.layout.{VBox, Region, Priority, HBox}
import scalafx.stage.WindowEvent
import scalafx.event.EventHandler

import java.sql.DriverManager
import scalafx.scene.control._
import scalafx.scene.control.Alert
import scalafx.beans.property._
import scalafx.scene.image.{Image, ImageView}

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
import java.io.OutputStreamWriter
import scalafx.application.Platform



object Main extends JFXApp3 {
  
  val database = new Database()
// yuh
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
    val tables = database.grab_years().map(y => y.toInt).sorted.map(y => String.valueOf(y))  // Convert to ObservableBuffer
    val screenstyle = "-fx-background-color: #141627; -fx-padding: 19px; -fx-font-size: 16px;"
    val titleStyle = "-fx-font-size: 32px; -fx-text-fill: #DE3423; -fx-font-weight: bold;"
    val textStyle = "-fx-font-size: 24px; -fx-text-fill: white; -fx-border-color: transparent;"

    // Screens
    def home_screen(stage: Stage, database: Database, months: Seq[String]): Scene = new Scene{
    val homeButtonStyle = "-fx-font-size: 24px; -fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: transparent;"
    val homeButtonStyleHover = "-fx-font-size: 24px; -fx-background-color: transparent; -fx-text-fill: #DE3423; -fx-border-color: transparent;"
      root = new HBox { 
          minWidth = 800
          minHeight = 600
          alignment = scalafx.geometry.Pos.CenterLeft
          spacing = 10
          style = screenstyle
          children = Seq(
          new VBox {
            hgrow = scalafx.scene.layout.Priority.Always
            style = screenstyle
            spacing = 30
            alignment = scalafx.geometry.Pos.TopLeft
            padding = scalafx.geometry.Insets(20)
            style = "-fx-background-color: #141627; -fx-padding: 20px; -fx-font-size: 16px;"
            val titleLabel = new Label("Scalable Budgets")
            titleLabel.setStyle(titleStyle)
            children = Seq(
              titleLabel,
              new Button("View Expenses/Incomes") { style = homeButtonStyle
                onAction = _ => stage.scene = view_expenses(stage, database, months)
                hover.onChange { (_, _, isHovered) =>
                  if (isHovered) this.setStyle(homeButtonStyleHover)
                  else this.setStyle(homeButtonStyle)
                }
              },
              new Button("Add a new year") { style = homeButtonStyle
                onAction = _ => {
                  if (stage.isFullScreen()){
                    stage.setFullScreen(true)
                  } else {
                    stage.setFullScreen(false)
                  }
                stage.scene = new_table(stage, database)
                } 
                hover.onChange { (_, _, isHovered) =>
                  if (isHovered) this.setStyle(homeButtonStyleHover)
                  else this.setStyle(homeButtonStyle)
                }},
              new Button("Add Expenses/Income") { style = homeButtonStyle
                onAction = _ => stage.scene = new_expense_screen(stage, database, months) 
                hover.onChange { (_, _, isHovered) =>
                  if (isHovered) this.setStyle(homeButtonStyleHover)
                  else this.setStyle(homeButtonStyle)
                }},
              new Button("Analyze Expenses") { style = homeButtonStyle
                onAction = _ => stage.scene = analyze_screen(stage, database, months) 
                hover.onChange { (_, _, isHovered) =>
                  if (isHovered) this.setStyle(homeButtonStyleHover)
                  else this.setStyle(homeButtonStyle)
                }},
              new Button("Budgeting Tips") { style = homeButtonStyle
                onAction = _ => stage.scene = budgetTips(stage, database, months) 
                hover.onChange { (_, _, isHovered) =>
                  if (isHovered) this.setStyle(homeButtonStyleHover)
                  else this.setStyle(homeButtonStyle)
                }},
              new Button("Help") { style = homeButtonStyle
                onAction = _ => stage.scene = helpPage(stage, database, months) 
                hover.onChange { (_, _, isHovered) =>
                  if (isHovered) this.setStyle(homeButtonStyleHover)
                  else this.setStyle(homeButtonStyle)
                }}
          )}, new scalafx.scene.image.ImageView(new scalafx.scene.image.Image("file:src/main/resources/images/ScalableBudgetLogo.png")) {
            fitWidth = 400
            fitHeight = 400
            preserveRatio = true
            smooth = true
            cache = true
          }
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
      val add_table_text = new Label("Add a new year to your budget")
      add_table_text.setStyle(titleStyle)
      val which_year = new Label("Which year would you like to add?")
      which_year.setStyle(textStyle)

      root = new VBox { 
        minWidth = 800
        minHeight = 600
        hgrow = scalafx.scene.layout.Priority.Always
        style = screenstyle
      
        spacing = 10
        children = Seq(add_table_text, which_year, user_year, new_year, new Button("HOME") { onAction = _ => stage.scene = home_screen(stage, database, months_list) })
      }
    }

    def new_expense_screen(stage: Stage, database: Database, months: Seq[String]): Scene = new Scene{
        val new_expense = new Button("Add new expense")
        val selected_year = new Label()
        val selected_month = new Label()
        val tables = database.grab_years().map(y => y.toInt).sorted.map(y => String.valueOf(y))  // Convert to ObservableBuffer

        val user_category = new TextField(){
          promptText = "Enter Category (e.g., Food, Transport)"
        }
        val user_expense = new TextField(){
          promptText = "Enter Amount"
        }
        val ex_or_in = new Label("Expense or Income?")
        ex_or_in.setStyle(textStyle)
        val expense_or_income = new ComboBox[String](Seq("Expense", "Income")) {
          promptText = "Select Expense or Income"
          onAction = _ => {
            if (this.value.value == "Income") {
              user_category.setPromptText("Enter Income Source")
            } else {
              user_category.setPromptText("Enter Expense Category")
            }
          }
        }

        new_expense.onAction = handle {
          if (expense_or_income.value.value == "Expense") {
            database.insertExpense(selected_year.text.value, selected_month.text.value, user_category.getText(), (-1 * (user_expense.getText()).toDouble))
          } else {
            database.insertExpense(selected_year.text.value, selected_month.text.value, user_category.getText(), (user_expense.getText()).toDouble)
          }
          new Alert(Alert.AlertType.Information) {
            title = s"${expense_or_income.value.value}: ${selected_year.text.value} Added"
            headerText = s"You added a ${user_category.getText()} ${expense_or_income.value.value} for ${selected_month.text.value} of ${user_expense.getText()} dollars." 
          }.showAndWait()
        }
        val add_expense_text = new Label("Add a new expense to your budget")
        add_expense_text.setStyle(titleStyle)
        val which_year = new Label("Which year would you like to add?")
        which_year.setStyle(textStyle)
        val which_month = new Label("Which month would you like to add?")
        which_month.setStyle(textStyle)
        val which_expense = new Label("What was the category/source?")
        which_expense.setStyle(textStyle)
        val amount = new Label("What was the amount?")
        amount.setStyle(textStyle)
        root = new VBox { 
          minWidth = 800
          minHeight = 600
          style = screenstyle  
          
          spacing = 30
          children = Seq(add_expense_text, expense_or_income, which_year, new ComboBox[String](tables) { 
            promptText = "Select a Budget Year"
            onAction = _ => {
              selected_year.setText(this.value.value)
            }
          }, which_month, new ComboBox[String](months_list) { 
            promptText = "Select a Month"
            onAction = _ => {
              selected_month.setText(this.value.value)
            }
          }, which_expense,user_category, amount, user_expense, new_expense, new Button("HOME") { onAction = _ => stage.scene = home_screen(stage, database, months) })
      }
    }

    def view_expenses(stage: Stage, database: Database, months: Seq[String]): Scene = new Scene{
    // Stuff for Table in View Expenses
        def change_table(table : TableView[ExpenseFX], expenses : ObservableBuffer[ExpenseFX]): Unit = {
          table.items = expenses
        }
        
        val selected_year = new Label()
        val selected_month = new Label()
        val expensesBuffer = ObservableBuffer[ExpenseFX]()
        val years = database.grab_years().map(y => y.toInt).sorted.map(y => String.valueOf(y))  // Convert to ObservableBuffer

        val deleteColumn = new javafx.scene.control.TableColumn[ExpenseFX, Unit] {
          
        setText("Actions")
          setPrefWidth(100)

          setCellValueFactory(_ => new ReadOnlyObjectWrapper[Unit](()))

          setCellFactory(new Callback[javafx.scene.control.TableColumn[ExpenseFX, Unit], TableCell[ExpenseFX, Unit]] {
            override def call(param: javafx.scene.control.TableColumn[ExpenseFX, Unit]): TableCell[ExpenseFX, Unit] = {
              new TableCell[ExpenseFX, Unit] {
                lazy val btn : Button = new Button("Delete")

                btn.onAction = _ => {
                  val item = getTableView.getItems.get(getIndex)
                  expensesBuffer -= item
                  database.deleteExpense(selected_year.text.value, item.month.value, item.category.value)
                  val expenses = database.viewYear(selected_year.text.value, Option(selected_month.text.value).getOrElse("."))
                  change_table(table, toFX(expenses))
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

        lazy val table: TableView[ExpenseFX] = new TableView[ExpenseFX](expensesBuffer) {
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

        val years_comboBox = new ComboBox[String](years) {
          promptText = "Select a Budget Year"
          onAction = _ => {
            selected_year.setText(this.value.value)
            val expenses = database.viewYear(selected_year.text.value, Option(selected_month.text.value).getOrElse("."))
            change_table(table, toFX(expenses))
          }
        }
        val view_expenses_text = new Label("View your expenses")
        view_expenses_text.setStyle(titleStyle)
        val which_year = new Label("Which year would you like to view?")
        which_year.setStyle(textStyle)
        val which_month = new Label("Which month did it fall under?")
        which_month.setStyle(textStyle)
        // Spacer region
        val spacer = new Region()
        HBox.setHgrow(spacer, Priority.Always)

        root = new VBox { 
          minWidth = 800
          minHeight = 600
          style = screenstyle
          
          table.setPlaceholder(new Label("No expenses found for the selected year and month."))
          spacing = 10
          children = Seq(view_expenses_text, which_year, years_comboBox, 
            which_month,  new ComboBox[String](months_list) { 
              promptText = "Select a month"
              onAction = _ => {
                selected_month.setText(this.value.value)
                val expenses = database.viewYear(selected_year.text.value, Option(selected_month.text.value).getOrElse("."))
                change_table(table, toFX(expenses))            
              }
            }, table, new HBox{
              spacing = 10
              maxWidth = Double.MaxValue
              children = Seq(new Button("HOME") { onAction = _ => stage.scene = home_screen(stage, database, months) }, spacer, new Button("Delete Year") { onAction = _ => 
              if (selected_year.text.value.nonEmpty) {
                val confirmation = new Alert(Alert.AlertType.Confirmation) {
                  title = s"Delete Year ${selected_year.text.value}?"
                  headerText = s"Are you sure you want to delete the year ${selected_year.text.value} and all its expenses?"
                  contentText = "This action cannot be undone."
                }.showAndWait()

                confirmation match {
                  case Some(ButtonType.OK) =>
                    database.deleteYear(selected_year.text.value)
                    new Alert(Alert.AlertType.Information) {
                      title = s"Year ${selected_year.text.value} Deleted"
                      headerText = "You deleted a year"
                    }.showAndWait()
                    selected_year.setText("")
                    selected_month.setText("")
                    change_table(table, ObservableBuffer())
                    years_comboBox.items = ObservableBuffer(database.grab_years().map(y => y.toInt).sorted.map(y => String.valueOf(y)): _*)
                  case _ => // User cancelled, do nothing
                }
              } else {
                new Alert(Alert.AlertType.Warning) {
                  title = "No Year Selected"
                  headerText = "Please select a year to delete."
                }.showAndWait()
              }
            })})
        }
    }

    def analyze_screen(stage: Stage, database: Database, months: Seq[String]): scalafx.scene.Scene = new Scene{
      val years = database.grab_years().map(y => y.toInt).sorted.map(y => String.valueOf(y))  // Convert to ObservableBuffer

      root = new VBox{
        minWidth = 800
        minHeight = 600
        style = screenstyle
        
        spacing = 50
        val analyzeLabel = new Label("Analyze your expenses")
        analyzeLabel.setStyle(titleStyle)
        children = Seq(
          analyzeLabel,
          new Button("Categories") { onAction = _ => {stage.scene = pieChartScene(stage, database, years = years, months = months)}},
          new Button("Months"){ onAction = _ => stage.scene = barChartScene(stage, database, years)},
          new Button("Years") {onAction = _ => {stage.scene = histogramChartScene(stage, database, years, months)}},
          new Button("HOME") { onAction = _ => stage.scene = home_screen(stage, database, months)}
        )
      }
    }

    def pieChartScene(stage:Stage, database: Database, years: List[String], months: Seq[String]): Scene = {
      val pieChart = new PieChart() {
        title = "Spending by Category"
      }
      pieChart.setStyle("-fx-pie-label-visible: true; -fx-label-fill: white; -fx-background-color: white; -fx-label-fill: white;")
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
          minWidth = 800
          minHeight = 600
          
          style = screenstyle
          spacing = 10
          children = Seq(year, month, pieChart, new Button("Back") {onAction =_ => {stage.scene = analyze_screen(stage, database, months)}}
          )
        }
      }
    }

    def barChartScene(stage: Stage, database: Database, years: List[String]): Scene = {
     
      val xAxis = new CategoryAxis()
      xAxis.setLabel("Month")
      xAxis.setStyle("-fx-text-fill: white; -fx-background-color: transparent;")


      val yAxis = new NumberAxis()
      yAxis.setLabel("Amount")
      yAxis.setStyle("-fx-label-fill: white; -fx-border-color: transparent;")
      yAxis.setTickLabelFormatter(new javafx.scene.chart.NumberAxis.DefaultFormatter(yAxis, "$", ""))

      val barChart = new BarChart[String, Number](xAxis, yAxis)
      barChart.setTitle("Leftovers by month")
      barChart.setStyle("-fx-label-fill: white; -fx-background-color: white;")

      val monthOrder = Seq(
          "Jan", "Feb", "Mar", "Apr", "May", "Jun",
          "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        xAxis.setCategories(scalafx.collections.ObservableBuffer(monthOrder: _*))


      val year = new ComboBox[String](years) {
        onAction = _ => {
          val barChartData = database.getBarChartData(this.value.value)
          val sortedData = monthOrder.flatMap(m => barChartData.find(_._1.contains(m)))
          val series = new XYChart.Series[String, Number]()
          series.setName("Total by Month")
          sortedData.foreach { case (cat, amt) =>
            series.getData.add(new XYChart.Data[String, Number](cat.substring(0, 3), amt))
          }
          barChart.getData.clear()
          barChart.getData.add(series)
        }
      }
      val backButton = new Button("Back") {
        onAction = _ => stage.scene = analyze_screen(stage, database, months_list)
      }

      val layout = new VBox(10, year, barChart, backButton)
      layout.style = screenstyle
      layout.setMinHeight(600)
      layout.setMinWidth(800)
      new Scene(layout)

    }

    def histogramChartScene(stage:Stage, database: Database, years: List[String], months:Seq[String]):Scene = {
      val xAxis = new CategoryAxis()
      xAxis.setLabel("Year")

      val yAxis = new NumberAxis()
      yAxis.setLabel("Amount")
      
      val lineChart = new LineChart[String, Number](xAxis, yAxis)
      lineChart.setTitle("Amount by Year")
      lineChart.setStyle("-fx-text-fill: white; -fx-background-color: white;")
      val yearOrder = years.sorted
      xAxis.setCategories(scalafx.collections.ObservableBuffer(yearOrder: _*))
      val backButton = new Button("Back") {
        onAction = _ => stage.scene = analyze_screen(stage, database, months)
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
      layout.setMinHeight(600)
      layout.setMinWidth(800)
      new Scene(layout) 

    }

    def helpPage(stage: Stage, database: Database, months : Seq[String]): Scene = {
      val helpLabel = new Label("Help Page")
      helpLabel.setStyle(titleStyle)
      val first_option = new Label("1. To add a new year, go to 'Add a new year' and enter the year.")
      first_option.setStyle("-fx-text-fill: white;")
      val second_option = new Label("2. To add expenses, go to 'Add Expenses/Income' and fill in the details.")
      second_option.setStyle("-fx-text-fill: white;")
      val third_option = new Label("3. To view expenses, go to 'View Expenses/Incomes' and select the year and month.")
      third_option.setStyle("-fx-text-fill: white;")
      new Scene {
        root = new VBox {
          style = screenstyle
          minWidth = 800
          minHeight = 600
          spacing = 10
          children = Seq(
            helpLabel,
            first_option,
            second_option,
            third_option,
            new Button("Back") { onAction = _ => stage.scene = home_screen(stage, database, months) }
          )
        }
      }
    }

    def budgetTips(stage: Stage, database: Database, months : Seq[String]): Scene = {
      

      lazy val savingsCalculator: HBox = new HBox {
        val income = new TextField { promptText = "Net Check?" }
        val savingsGoal = new TextField { promptText = "% to save?" }
        val goal_label = new Label("Savings Goal:")
        goal_label.setStyle(textStyle)
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
        val incomeField = new TextField { promptText = "Check Amount?" }
        val savingsField = new TextField { promptText = "Savings Goal?" }
        val timeFrame = new TextField { promptText = "Timeframe (in months)?" }
        val goal_label = new Label("Savings Goal:")
        goal_label.setStyle(textStyle)
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
        minWidth = 800
        minHeight = 600
        spacing = 10
        visible = false
        children = Seq(
          goal_label,
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
      budgetingTipsLabel.setStyle(titleStyle)
      new Scene {
        root = new VBox {
          minWidth = 800
          minHeight = 600
          style = screenstyle
          
          spacing = 10
          children = Seq(
            budgetingTipsLabel,
            budgetingTips,
            savingsCalculator,
            goals,
            new Button("Back") { onAction = _ => stage.scene = home_screen(stage, database, months) }
          )
        }
      }
    }

    
    // Set the initial scene
    stage = new JFXApp3.PrimaryStage {
      title = "Budget App"
      scene = home_screen(this, database, months_list)
      resizable = false
    }
    stage.fullScreen = false
    stage.fullScreenExitKey = null
    stage.fullScreenExitHint = ""
    

  }

  override def stopApp(): Unit = {
    database.close()
  }
}
