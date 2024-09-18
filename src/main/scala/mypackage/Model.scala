package mypackage
import slick.lifted.ProvenShape

case class Pizza(id: Long, name: String, price: Int, nIngredients: Int)

object SlickTables {
  import slick.jdbc.PostgresProfile.api._

  class PizzaTable(tag: Tag) extends Table[Pizza](tag, Some("pizzeria"), "Pizza"){
    def id = column[Long]("pizza_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def price = column[Int]("price")
    def nIngredients = column[Int]("n_ingredients")

    override def *  = (id, name, price, nIngredients) <> (Pizza.tupled, Pizza.unapply)
  }

  lazy val pizzaTable = TableQuery[PizzaTable]
}
