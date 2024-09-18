package mypackage
import slick.lifted.ProvenShape

case class Pizza(id: Long, name: String, price: Int, nIngredients: Int)
case class Ingredient(id: Long, name: String)
case class PizzaIngredient(id: Long, pizzaid: Long, ingredientid: Long)

object SlickTables {
  import slick.jdbc.PostgresProfile.api._

  //Entity = Pizza
  class PizzaTable(tag: Tag) extends Table[Pizza](tag, Some("pizzeria"), "Pizza"){
    def id = column[Long]("pizza_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def price = column[Int]("price")
    def nIngredients = column[Int]("n_ingredients")

    override def *  = (id, name, price, nIngredients) <> (Pizza.tupled, Pizza.unapply)
  }
  lazy val pizzaTable = TableQuery[PizzaTable]

  //Entity = Ingredient
  class IngredientTable(tag: Tag) extends Table[Ingredient](tag, Some("pizzeria"), "Ingredient"){
    def id = column[Long]("ingredient_id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    override def *  = (id, name) <> (Ingredient.tupled, Ingredient.unapply)
  }
  lazy val ingredientTable = TableQuery[IngredientTable]

  class PizzaIngredientTable(tag: Tag) extends Table[PizzaIngredient](tag, Some("pizzeria"), "PizzaIngredientMapping"){
    def id = column[Long]("pizza_ingredient_id", O.PrimaryKey, O.AutoInc)
    def pizzaId = column[Long]("pizza_id")
    def ingredientId = column[Long]("ingredient_id")

    override def *  = (id, pizzaId, ingredientId) <> (PizzaIngredient.tupled, PizzaIngredient.unapply)
  }
  lazy val pizzaIngredientTable = TableQuery[PizzaIngredientTable]
}
