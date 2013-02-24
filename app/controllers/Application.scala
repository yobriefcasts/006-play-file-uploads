package controllers

import play.api.mvc._
import play.api.data.Forms._
import play.api.data._

case class ApplicationDetails(firstname: String, lastname: String, position: String)

object Application extends Controller {

  val details = Form(
    mapping(
      "firstname" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "position" -> nonEmptyText
    )(ApplicationDetails.apply)
     (ApplicationDetails.unapply)
  )

  def index = Action {
    Ok(views.html.index())
  }

  def apply = Action {
    Ok(views.html.apply())
  }

  def submit = Action(parse.multipartFormData)  { implicit request =>
    details.bindFromRequest.fold(
      errors => BadRequest(views.html.apply()),
      details => {
        // print the details
        println(details)

        request.body.file("coverletter").map { coverletter =>
          val target = new java.io.File(s"./uploads/${coverletter.filename}")
          coverletter.ref.moveTo(target, true)
        }

        request.body.file("cv").map { cv =>
          val target = new java.io.File(s"./uploads/${cv.filename}")
          cv.ref.moveTo(target, true)
        }

        Redirect(routes.Application.index())
      }
    )
  }

}