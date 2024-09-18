package mypackage

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

  def insertPizza(): Unit = {
    val queryDescription = SlickTables.pizzaTable += quattroFormaggi
    val futureId: Future[Int] = Connection.db.run(queryDescription)

    futureId.onComplete {
      case Success(newPizzaId) => println(s"Success, new id: $newPizzaId")
      case Failure(e) => println(s"Execution failed: $e")
    }
    //Apparently the executor finishes before applying the statement, since it is a test a sleep should do
    Thread.sleep(200)
  }

  def readAllPizzas(): Unit = {
    val resultFuture: Future[Seq[Pizza]] = Connection.db.run(SlickTables.pizzaTable.result)
    resultFuture.onComplete{
      case Success(pizzas) => println(s"Fetched: ${pizzas.mkString(", ")}")
      case Failure(e) => println(s"Error: $e")
    }
    Thread.sleep(200)
  }

  def readSomePizzas(): Unit = {
    val resultFuture: Future[Seq[Pizza]] = Connection.db.run(SlickTables.pizzaTable.filter(_.name.like("%formaggi%")).result)
    resultFuture.onComplete{
      case Success(pizzas) => println(s"Fetched: ${pizzas.mkString(", ")}")
      case Failure(e) => println(s"Error: $e")
    }
    Thread.sleep(200)
  }

  def updatePizza(): Unit = {
    val queryDescriptor = SlickTables.pizzaTable.filter(_.id === 1L).update(quattroFormaggi.copy(price = 20))
    val futureId: Future[Int] = Connection.db.run(queryDescriptor)

    futureId.onComplete {
      case Success(newPizzaId) => println(s"Success, new id: $newPizzaId")
      case Failure(e) => println(s"Execution failed: $e")
    }
    Thread.sleep(200)
  }

  def deletePizzas(): Unit = {
    Connection.db.run(SlickTables.pizzaTable.filter(_.name.like("%formaggi%")).delete)
    Thread.sleep(200)
  }

  def main(args: Array[String]): Unit = {
    //insertPizza()
    //readAllPizzas()
    //readSomePizzas()
    //updatePizza()
  }
}
