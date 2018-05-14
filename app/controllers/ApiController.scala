package controllers

import javax.inject.Inject

import actors.ActorProtocol._
import actors._
import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import com.google.inject.{Singleton}
import db.DbOperation
import services.ConvertToJsonService
import spray.json._
import scala.concurrent.duration._
import play.api.mvc._
import akka.pattern.{ask}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger

@Singleton
class ApiController @Inject()(system: ActorSystem) extends Controller {

  DbOperation.tableCreate

  val yarnCommandActor = system.actorOf(Props[YarnActor], name = "yarn_command_actor")
  val checkAppUpdate = system.actorOf(Props[CheckAppUpdate], name = "CheckAppUpdate")
  //val quartzActorForCheck = system.actorOf(Props[QuartzActor], name = "QuartzActorForCheck")

  //quartzActorForCheck ! AddCronSchedule(checkAppUpdate, "0/5 * * * * ?", "update")
  system.scheduler.schedule(0 milliseconds, 5000 milliseconds, checkAppUpdate, "update")

  implicit val timeout = Timeout(5.seconds)

  def index = Action {
    Logger.debug("Action index method. Your new application is ready.")
    Ok(views.html.index ("Your new application is ready."))
  }

  def getAppList = Action.async {
    ConvertToJsonService.storeAppList(ConvertToJsonService.yarnAppList)
    (yarnCommandActor ? GetAllAppList).mapTo[JsValue] map {
      appList => Ok(appList.toString)
    }
  }

  def getAppStatus(app_id: Option[String], app_name: Option[String]) = Action.async {
    (yarnCommandActor ? GetSingleAppStatus(app_id, app_name)).mapTo[JsValue] map {
      singleAppStatu => Ok(singleAppStatu.toString)
    }
  }

  def subscribeApp(email: Option[String], phone: Option[String],
                   app_id: Option[String], app_name: Option[String]) =
    Action.async {
      (yarnCommandActor ? SubscribeApp(email, phone, app_id, app_name)).mapTo[JsValue] map {
        subscribeApp => Ok(subscribeApp.toString)
      }
    }

  def unsubscribeApp(email: Option[String], phone: Option[String],
                     app_id: Option[String], app_name: Option[String]) = Action.async {
    (yarnCommandActor ? UnsubscribeApp(email, phone, app_id, app_name)).mapTo[JsValue] map {
      unSubscribeApp => Ok(unSubscribeApp.toString)
    }
  }

  def addDescription(app_name: String, description: String) = Action.async {
    (yarnCommandActor ? AddDescription(app_name, description)).mapTo[String] map {
      description => Ok(description)
    }
  }

}