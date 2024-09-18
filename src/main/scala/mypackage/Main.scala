package mypackage

import slick.jdbc.GetResult

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object PrivateExecutionContext{
  val executor = Executors.newFixedThreadPool(4)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executor)
}

object Main {
  import slick.jdbc.PostgresProfile.api._
  import PrivateExecutionContext._

  val quattroFormaggi = Pizza(1L, "Quattro Formaggi", 10, 4)

  val gorgonzola = Ingredient(1L, "Gorgonzola")
  val mozzarella = Ingredient(2L, "Mozzarella")
  val brie = Ingredient(3L, "Brie")
  val feta = Ingredient(4L, "Feta")
  val salmon = Ingredient(4L, "Salmon")

  def insertPizza(): Unit = {
    val queryDescription = SlickTables.pizzaTable += quattroFormaggi
    val futureId: Future[Int] = Connection.db.run(queryDescription)

    futureId.onComplete {
      case Success(recordsAffected) => println(s"Success, records affected: $recordsAffected")
      case Failure(e) => println(s"Execution failed: $e")
    }
    //Apparently the executor finishes before applying the statement, since it is a test a sleep should do
    //Refactored to the main function
    //Thread.sleep(200)
  }

  def readAllPizzas(): Unit = {
    val resultFuture: Future[Seq[Pizza]] = Connection.db.run(SlickTables.pizzaTable.result)
    resultFuture.onComplete{
      case Success(pizzas) => println(s"Fetched: ${pizzas.mkString(", ")}")
      case Failure(e) => println(s"Error: $e")
    }
  }

  def readSomePizzas(): Unit = {
    val resultFuture: Future[Seq[Pizza]] = Connection.db.run(SlickTables.pizzaTable.filter(_.name.like("%formaggi%")).result)
    resultFuture.onComplete{
      case Success(pizzas) => println(s"Fetched: ${pizzas.mkString(", ")}")
      case Failure(e) => println(s"Error: $e")
    }
  }

  def updatePizza(): Unit = {
    val queryDescriptor = SlickTables.pizzaTable.filter(_.id === 1L).update(quattroFormaggi.copy(price = 20))
    val futureId: Future[Int] = Connection.db.run(queryDescriptor)

    futureId.onComplete {
      case Success(recordsAffected) => println(s"Success, records affected: $recordsAffected")
      case Failure(e) => println(s"Execution failed: $e")
    }
  }

  def deletePizzas(): Unit = {
    Connection.db.run(SlickTables.pizzaTable.filter(_.name.like("%formaggi%")).delete)
  }

  def insertIngredient(): Unit = {
    val queryDescription = SlickTables.ingredientTable ++= Seq(mozzarella, gorgonzola, feta, brie)
    val futureId = Connection.db.run(queryDescription)

    futureId.onComplete {
      case Success(recordsAffected) => println(s"Success, records affected: $recordsAffected")
      case Failure(e) => println(s"Execution failed: $e")
    }
    //Apparently the executor finishes before applying the statement, since it is a test a sleep should do
    //Refactored to the main function
    //Thread.sleep(200)
  }

  def executeQuery(): Future[Vector[Pizza]] = {
    implicit val getResultPizza: GetResult[Pizza] =
      GetResult(positionedResult => Pizza(
        positionedResult.<<,
        positionedResult.<<,
        positionedResult.<<,
        positionedResult.<<))
    val stmt = sql""" select * from pizzeria."Pizza"""".as[Pizza]

    Connection.db.run(stmt)
  }

  def executeTransaction(): Unit = {
    val insertPizza = SlickTables.pizzaTable += quattroFormaggi
    val insertIngredient = SlickTables.ingredientTable += salmon
    val queryStmts = DBIO.seq(insertPizza,insertIngredient)
    Connection.db.run(queryStmts.transactionally)
  }

  def findIngredientsOfPizza(pizzaId: Long): Future[Seq[Ingredient]] = {
    val joinStmt = SlickTables.pizzaIngredientTable
      .filter(_.pizzaId === pizzaId)
      .join(SlickTables.ingredientTable)
      .on(_.ingredientId === _.id)
      .map(_._2)

    Connection.db.run(joinStmt.result)
  }

  def main(args: Array[String]): Unit = {
    //insertPizza()
    //readAllPizzas()
    //readSomePizzas()
    //updatePizza()
    //insertIngredient()
    //executeTransaction()
    /*
    findIngredientsOfPizza(1L).onComplete{
      case Success(ingredients) => println(s"Success:$ingredients")
      case Failure(e) => println(s"Exception: $e")
    }
    */
    executeQuery().onComplete{
      case Success(pizzas) => println(s"Success, pizzas: ${pizzas}")
      case Failure(e) => println(s"Exception: $e")
    }
    Thread.sleep(2000)
    PrivateExecutionContext.executor.shutdown()
  }
}
