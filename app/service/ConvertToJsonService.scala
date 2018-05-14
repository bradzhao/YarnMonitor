package services

/**
  *
  *
  * Project: YarnMonitor
  *
  * @author Collins Xiao, Brad Zhao
  * @version 1.0
  * @since : 2016-01-27 10:17
  * @note
  *
  *
  */

import service.YarnMonitorService
import service.YarnMonitorService._
import spray.json._
import DefaultJsonProtocol._
import db._
import models.AppModels._
import utils.{Message}
import play.api.Logger

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val appListFormat = jsonFormat2(AppList)
}

object ConvertToJsonService {

  def getAppList = {
    Logger.info("ConvertToJsonService.getAppList.start")
    var appListMap: Map[String, JsValue] = Map()
    var appListData: Array[AppList] = Array()
    //database things
    //get the "-list" data from database
    //select appname, appid from YM_APP
    val appListInfo: Seq[(String, String)] = DbOperation.appGetAll
    appListInfo.map {
      x => appListData ++= Array(AppList(x._1, x._2))
    }
    import spray.json._
    import MyJsonProtocol._
    appListMap += ("AppList" -> appListData.toJson)
    appListMap += ("errCode" -> Message.Success.toString.toJson)
    appListMap.toJson
  }

  def yarnAppList = {
    Logger.info("ConvertToJsonService.yarnAppList.start")
    var appListInfo: Array[AppList] = Array()
    val applist = YarnMonitorService.getAppLists.split("Tracking-URL")
    if (applist.length >= 2) {
      val applist1 = applist(1).trim
      val regex = "\n|\t"
      val res: Array[String] = applist1.split(regex).map(_.trim)
      var i = 1
      while (i <= res.length) {
        if (i % 9 == 0) {
          if (!res(i - 8).equals("Spark shell")) {
            if (!res(i - 9).contains("(Stage-")) {
              appListInfo ++= Array(AppList(res(i - 9), res(i - 8)))
            }
          }
        }
        i += 1
      }
    }
    appListInfo
  }

  def storeAppList(appListInfo: Array[AppList]) = {
    Logger.info("ConvertToJsonService.storeAppList.start")
    if (appListInfo != null && !appListInfo.isEmpty) {
      appListInfo.foreach(x => DbOperation.appInsert(x.app_id, x.app_name))
    }
  }

  def getAppStatus(app_id: Option[String], app_name: Option[String]) = {
    Logger.info("ConvertToJsonService.getAppStatus.start")
    var MessageMap: Map[String, JsValue] = Map()
    var appStatusMap: Map[String, JsValue] = Map()
    var appStatusInfo: Map[String, String] = Map()

    def getStatus(appId: String): JsValue = {
      val appStatus = YarnMonitorService.getSingleAppStatu(appId).split("Application Report : ")
      val appStatus1 = appStatus(1).trim
      val regex = "\n| : "
      val res = appStatus1.split(regex).map(_.trim)
      var i = 1
      while (i <= res.length) {
        if (i % 2 == 0) {
          appStatusInfo += (res(i - 2) -> (res(i - 1)))
        }
        i += 1
      }
      appStatusMap += ("AppStatus" -> appStatusInfo.toJson)
      appStatusMap.toJson
    }

    if (app_id.isEmpty && app_name.isEmpty) {
      val errCode = Message.Error400.toString
      MessageMap += ("Result" -> "None".toJson)
      MessageMap += ("ErrorCode" -> errCode.toJson)
    }
    else if (app_id.isEmpty) {
      //database things
      val errCode = Message.Success.toString
      val appId = DbOperation.appGetID(app_name.getOrElse(""))
      val appStatus = getStatus(appId)
      MessageMap += ("Result" -> appStatus.toJson)
      MessageMap += ("ErrorCode" -> errCode.toJson)
    }
    else {
      val errCode = Message.Success.toString
      val appId = app_id.getOrElse("")
      val appStatus = getStatus(appId)
      MessageMap += ("Result" -> appStatus.toJson)
      MessageMap += ("ErrorCode" -> errCode.toJson)
    }
    MessageMap.toJson
  }

  def subscribeApp(email: Option[String], phone: Option[String], app_id: Option[String], app_name: Option[String]) = {
    Logger.info("ConvertToJsonService.subscribeApp.start")
    var MessageMap: Map[String, String] = Map()
    if ((email.isEmpty && phone.isEmpty) || ((app_id.isEmpty && app_name.isEmpty))) {
      MessageMap += ("Result" -> "Failed")
      MessageMap += ("ErrorCode" -> Message.Error400.toString)
    }
    else if ((email.nonEmpty || phone.nonEmpty) && (app_name.nonEmpty || app_id.nonEmpty)) {
      MessageMap += ("Result" -> "Success")
      MessageMap += ("ErrorCode" -> Message.Success.toString)
      //some databases things
      DbOperation.userInsert(email.getOrElse(""), phone.getOrElse(""))
      val aID = DbOperation.appGetAID(app_id.getOrElse(""), app_name.getOrElse(""))
      val uID = DbOperation.userGetUserID(email.getOrElse(""), phone.getOrElse(""))
      DbOperation.appUserInsert(uID, aID)
    }
    MessageMap.toJson
  }

  def unSubscribeApp(email: Option[String], phone: Option[String], app_id: Option[String], app_name: Option[String]) = {
    Logger.info("ConvertToJsonService.unSubscribeApp.start")
    var MessageMap: Map[String, String] = Map()
    if ((email.isEmpty && phone.isEmpty) || ((app_id.isEmpty && app_name.isEmpty))) {
      MessageMap += ("Result" -> "Failed")
      MessageMap += ("ErrorCode" -> Message.Error400.toString)
    }
    else if ((email.nonEmpty || phone.nonEmpty) && (app_name.nonEmpty || app_id.nonEmpty)) {
      MessageMap += ("Result" -> "Success")
      MessageMap += ("ErrorCode" -> Message.Success.toString)
    }
    //some databases things
    val userId = DbOperation.userGetUserID(email.getOrElse(""), phone.getOrElse(""))
    val aId = DbOperation.appGetAID(app_id.getOrElse(""), app_name.getOrElse(""))
    DbOperation.appUserDelete(userId, aId)
    MessageMap.toJson
  }

  def addDescription(app_name: String, description: String): String = {
    Logger.info("ConvertToJsonService.addDescription.start")
    DbOperation.appAddDescription(app_name, description)
    "Description added successfully"
  }

  def getFinalState(appId: String): Map[String, String] = {
    Logger.info("ConvertToJsonService.getFinalState.start")
    var appStatusInfo: Map[String, String] = Map()
    val appStatus = YarnMonitorService.getSingleAppStatu(appId).split("Application Report : ")
    val appStatus1 = appStatus(1).trim
    val regex = "\n| : "
    val res = appStatus1.split(regex).map(_.trim)
    var i = 1
    while (i <= res.length) {
      if (i % 2 == 0) {
        appStatusInfo += (res(i - 2) -> (res(i - 1)))
      }
      i += 1
    }
    appStatusInfo
  }

}
