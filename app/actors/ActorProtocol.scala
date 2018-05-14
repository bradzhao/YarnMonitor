package actors

/**
  *
  *
  * Project: YarnMonitor 
  * @author Collins Xiao, Brad Zhao
  * @version 1.0
  * @since : 2016-01-25 15:17
  * @note
  *
  *
  */
object ActorProtocol {

  object GetAllAppList

  case class GetSingleAppStatus(app_id: Option[String], app_name: Option[String])

  case class SubscribeApp(email: Option[String], phone: Option[String], app_id: Option[String], app_name: Option[String])

  case class UnsubscribeApp(email: Option[String], phone: Option[String], app_id: Option[String], app_name: Option[String])

  case class AddDescription(app_name: String, description: String)
  
}
