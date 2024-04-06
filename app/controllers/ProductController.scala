package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import models.Product
import play.api.http.Writeable

@Singleton
class ProductController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  implicit val productWrites: Writes[Product] = Json.writes[Product]
  implicit val productReads: Reads[Product] = Json.reads[Product]

  var products: Seq[Product] = Seq(
    Product(1, "Book", 10.99),
    Product(2, "Shoes", 105.99),
    Product(3, "Pen", 2.99),
    Product(4, "Rubber", 2.99),
    Product(5, "T-shirt", 33.99),
  )

  def createProduct() = Action(parse.json) { implicit request: Request[JsValue] =>
    request.body.validate[Product].map { product =>
      products = products :+ product
      Created("Product created successfully")
    }.getOrElse {
      BadRequest("Invalid JSON format")
    }
  }

  def getProducts() = Action { implicit request: Request[AnyContent] =>
    Ok(Json.toJson(products))
  }

  def getProduct(id: Long) = Action { implicit request: Request[AnyContent] =>
    products.find(_.id == id).map { product =>
      Ok(Json.toJson(product))
    }.getOrElse {
      NotFound("Product not found")
    }
  }

  def updateProduct(id: Long) = Action(parse.json) { implicit request: Request[JsValue] =>
    request.body.validate[Product].map { updatedProduct =>
      products.find(_.id == id) match {
        case Some(_) =>
          products = products.map(p => if (p.id == id) updatedProduct else p)
          Ok("Product updated successfully")
        case None =>
          NotFound("Product not found")
      }
    }.getOrElse {
      BadRequest("Invalid JSON format")
    }
  }

  def deleteProduct(id: Long) = Action { implicit request: Request[AnyContent] =>
    products.find(_.id == id) match {
      case Some(_) =>
        products = products.filterNot(_.id == id)
        Ok("Product deleted successfully")
      case None =>
        NotFound("Product not found")
    }
  }
}
