package actors

import akka.actor.{ActorLogging, Actor}
import actors.ActorProtocol._
import play.api.Logger

/**
  *
  *
  * Project: YarnMonitor
  *
  * @author Collins Xiao
  * @version 1.0
  * @since : 25 01 2016
  * @note
  *
  *
  */
class YarnActor extends Actor {
  def receive: Receive = {
    case GetAllAppList =>
      sender() ! services.ConvertToJsonService.getAppList
    case GetSingleAppStatus(app_id, app_name) =>
      sender() ! services.ConvertToJsonService.getAppStatus(app_id, app_name)
    case SubscribeApp(email, phone, app_id, app_name) =>
      sender() ! services.ConvertToJsonService.subscribeApp(email, phone, app_id, app_name)
    case UnsubscribeApp(email, phone, app_id, app_name) =>
      sender() ! services.ConvertToJsonService.unSubscribeApp(email, phone, app_id, app_name)
    case AddDescription(app_name, description) =>
      sender() ! services.ConvertToJsonService.addDescription(app_name, description)
  }
}
