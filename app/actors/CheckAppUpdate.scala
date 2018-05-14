package actors

import akka.actor.{ActorLogging, Actor}
import db.DbOperation
import service.WriteLogs
import services.ConvertToJsonService
import play.api.Logger
import utils.{MailSendings, HttpClient, StringUtil}

/**
  *
  *
  * Project: YarnMonitor
  *
  * @author Brad Zhao
  * @version 1.0
  * @since : 2016-02-02 16:34
  * @note
  *
  *
  */
class CheckAppUpdate extends Actor {

  def receive: Receive = {
    case _ => {
      Logger.info("CheckAppUpdate.receive.actor")
      val appListInfo = ConvertToJsonService.yarnAppList
      if ((appListInfo != null) && (!appListInfo.isEmpty)) {
        ConvertToJsonService.storeAppList(appListInfo)
        appListInfo.foreach(x => DbOperation.notifyHistInsert(x.app_id))
        Logger.info("CheckAppUpdate.receive.actor.appListInfo.notempty.end")
      }
      Logger.info("NotifyActor.receive.notempty")
      val monitoredList = DbOperation.getMonitoringApp
      Logger.info( s"""NotifyActor.receive.getMonitoringApp, monitoredList:$monitoredList""")
      if (monitoredList.nonEmpty) {
        //SUCCEEDED, FAILED, KILLED
        //monitoredList.map { m => ConvertToJsonService.getFinalState(m).foreach(println) }
        monitoredList.map { m =>
          var ft = ConvertToJsonService.getFinalState(m)
          ft.get("Final-State").getOrElse("").toString match {
            case "SUCCEEDED" => {
              Logger.info("NotifyActor.receive.SUCCEEDED----start")
              val users = DbOperation.getMonitoringUserList(ft.get("Application-Id").get)
              val emailList = users.map(u => u._1).toList
              val phoneList = users.map(u => u._2).toList
              val subject = StringUtil.finalJobTitle(ft.get("Application-Name").get, "SUCCEEDED")
              val content = StringUtil.finalJobContent(ft.get("Application-Id").get, ft.get("Application-Name").get,
                ft.get("Start-Time").get.toLong, ft.get("Finish-Time").get.toLong,
                ft.get("User").get, ft.get("Queue").get, "SUCCEEDED")

              var i = 0
              Logger.info("NotifyActor.receive.SUCCEEDED----sendSMSPost")
              if (phoneList != null && !phoneList.isEmpty) {
                HttpClient.sendSMSPost(StringUtil.notifyToPhoneContent(phoneList, content))
                Logger.info("NotifyActor.receive.SUCCEEDED----sendSMSPost")
                i += 1
              }
              //Logger.info("NotifyActor.receive.SUCCEEDED----sendEmail")
              if (emailList != null && emailList.nonEmpty) {
                MailSendings.sendEmail(emailList, subject, content)
                i += 1
              }
              if (i > 0) {
                DbOperation.sendNotify(ft.get("Application-Id").get, content)
                Logger.info("NotifyActor.receive.SUCCEEDED----end")
              }
            }
            case "FAILED" | "KILLED" => {
              Logger.info("NotifyActor.receive.FAILEDorKILLED-----start")
              val users = DbOperation.getMonitoringUserList(ft.get("Application-Id").get)
              val emailList = users.map(u => u._1).toList
              val phoneList = users.map(u => u._2).toList
              val yarnLog = WriteLogs.writeLogsToFile(ft.get("Application-Id").get)
              val subject = StringUtil.finalJobTitle(ft.get("Application-Name").get, ft.get("Final-State").get)
              val content = StringUtil.finalJobContent(ft.get("Application-Id").get, ft.get("Application-Name").get,
                ft.get("Start-Time").get.toLong, ft.get("Finish-Time").get.toLong,
                ft.get("User").get, ft.get("Queue").get, ft.get("Final-State").get)

              var i = 0
              if (phoneList != null && phoneList.nonEmpty) {
                HttpClient.sendSMSPost(StringUtil.notifyToPhoneContent(phoneList, content))
                Logger.info("NotifyActor.receive.SUCCEEDED----sendSMSPost")
                i += 1
              }
              Logger.info("NotifyActor.receive.FAILEDorKILLED-----sendEmail")
              if (emailList != null && emailList.nonEmpty) {
                MailSendings.sendEmail(emailList, yarnLog, subject, content)
                Logger.info("NotifyActor.receive.FAILEDorKILLED-----DbOperation.sendNotify")
                i += 1
              }
              if (i > 0) {
                DbOperation.sendNotify(ft.get("Application-Id").get, content)
                Logger.info("NotifyActor.receive.FAILEDorKILLED-----end")
              }
            }
            case _ => Logger.info( s"""NotifyActor nothing to do, app_id: ${ft.get("Application-Id")} is UNDEFINED""")
          }
        }
      }
    }
  }
}
