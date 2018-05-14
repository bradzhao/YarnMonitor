package db


import java.sql.Timestamp
import _root_.slick.jdbc.meta.MTable
import db.DbSchema._
import play.api.Logger
import scala.collection.Seq
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.MySQLDriver.api._

/**
  * Created by merlin on 16-2-2.
  */
object DbOperation {

  def tableCreate = {
    Logger.info("create new tables. tableCreate")
    try {
      //println(dbConf.db.createSession().conn.getCatalog)
      val checkTable = Await.result(dbConf.db.run(MTable.getTables), Duration.Inf).toList

      if (checkTable.isEmpty) {
        val createAction = (ymUser.schema ++ ymApp.schema ++ ymUserApp.schema ++ ymNotifyHist.schema).create
        Await.result(dbConf.db.run(createAction), Duration.Inf)
      }
    }
    catch {
      case e: Exception => print("Exception caught :" + e)
    }
  }

  def userInsert(user_email: String, user_mobile: String) = {
    Logger.debug("method:userInsert")

    implicit def date2timestamp(date: java.util.Date): Timestamp = new Timestamp(date.getTime)
    val ts: Timestamp = new java.util.Date
    val userCollect = ymUser.map(x => (x.user_email, x.user_mobile))
    val userList = Await.result(dbConf.db.run(userCollect.result), Duration.Inf)
    val userEmail = userList.map(x => x._1)
    val userMobile = userList.map(x => x._2)
    if (userEmail.contains(user_email)) {
      val userUpdate = ymUser.filter(_.user_email === user_email).map(x => (x.user_mobile,x.updated_on))
      val updateAction = userUpdate.update(user_mobile,ts)
      Await.result(dbConf.db.run(updateAction), Duration.Inf)
    }
    else if (userMobile.contains(user_mobile)) {
      val userUpdate = ymUser.filter(_.user_mobile === user_mobile).map(x => (x.user_email,x.updated_on))
      val updateAction = userUpdate.update(user_email,ts)
      Await.result(dbConf.db.run(updateAction), Duration.Inf)
    }
    else {
      val insertAction = DBIO.seq(
        ymUser +=(0, user_email, user_mobile, ts)
      )
      Await.result(dbConf.db.run(insertAction), Duration.Inf)
    }
  }

  def userDelete(user_id: Int) = {
    Logger.debug("method:userDelete")
    val userDel = ymUser.filter(_.user_id === user_id)
    Await.result(dbConf.db.run(userDel.delete), Duration.Inf)
  }


  def userGetEmail(user_id: Int) = {
    Logger.debug("method:userGetEmail")
    val userEmail = ymUser.filter(_.user_id === user_id).map(_.user_email)
    Await.result(dbConf.db.run(userEmail.result), Duration.Inf).head
  }


  def userGetMobile(user_id: Int) = {
    Logger.debug("method:userGetMobile")
    val userMobile = ymUser.filter(_.user_id === user_id).map(_.user_mobile)
    Await.result(dbConf.db.run(userMobile.result), Duration.Inf).head
  }


  def userGetUserID(user_email: String, user_mobile: String) = {
    Logger.debug("method:userGetUserID")
    if (!user_email.isEmpty) {
      val userId = ymUser.filter(_.user_email === user_email).map(_.user_id)
      Await.result(dbConf.db.run(userId.result), Duration.Inf).head
    } else {
      val userId = ymUser.filter(_.user_mobile === user_mobile).map(_.user_id)
      Await.result(dbConf.db.run(userId.result), Duration.Inf).head
    }
  }


  def appGetAID(app_id: String, app_name: String) = {
    Logger.debug("method:appGetAID")
    if (!app_id.isEmpty) {
      val aid = ymApp.filter(_.app_id === app_id).map(_.aid)
      Await.result(dbConf.db.run(aid.result), Duration.Inf).head
    } else {
      val aid = ymApp.filter(_.app_name === app_name).map(_.aid)
      Await.result(dbConf.db.run(aid.result), Duration.Inf).head
    }
  }


  def appInsert(app_id: String, app_name: String) = {
    Logger.debug("method:appInsert")

    val appCollect = ymApp.map(_.app_name)
    val appList = Await.result(dbConf.db.run(appCollect.result), Duration.Inf)

    if (!appList.contains(app_name)) {
      val insertAction = DBIO.seq(
        ymApp +=(0, app_id, app_name, "")
      )
      Await.result(dbConf.db.run(insertAction), Duration.Inf)
    } else {
      val q = ymApp.filter(_.app_name === app_name).map(x => x.app_id)
      val updateAction = q.update(app_id)
      Await.result(dbConf.db.run(updateAction), Duration.Inf)
    }
  }


  def appGetID(app_name: String) = {
    Logger.debug("method:appGetID")
    val appId = ymApp.filter(_.app_name === app_name).map(_.app_id)
    Await.result(dbConf.db.run(appId.result), Duration.Inf).head
  }

  def appGetAll = {
    Logger.debug("method:appGetAll")
    val appInfo = ymApp.map(x => (x.app_id, x.app_name))
    Await.result(dbConf.db.run(appInfo.result), Duration.Inf)
  }


  def appAddDescription(app_name: String, app_description: String) = {
    Logger.debug("method:appAddDescription")

    val appCollect = ymApp.map(_.app_name)
    val appList = Await.result(dbConf.db.run(appCollect.result), Duration.Inf)
    if (appList.contains(app_name)) {
      val q = ymApp.filter(_.app_name === app_name).map(x => x.app_description)
      val updateAction = q.update(app_description)
      Await.result(dbConf.db.run(updateAction), Duration.Inf)
    }
  }


  def appDelete(app_id: String) = {
    Logger.debug("method:appDelete")
    val appDel = ymApp.filter(_.app_id === app_id)
    Await.result(dbConf.db.run(appDel.delete), Duration.Inf)
  }


  def appUserInsert(user_id: Int, aid: Int) = {
    Logger.debug("method:appUserInsert")

    implicit def date2timestamp(date: java.util.Date): Timestamp = new Timestamp(date.getTime)
    val ts: Timestamp = new java.util.Date
    val mapCollect = ymUserApp.map(x => (x.user_id, x.aid))
    val mapList = Await.result(dbConf.db.run(mapCollect.result), Duration.Inf)
    val x1 = mapList.map(x => x._1)
    val x2 = mapList.map(x => x._2)
    if (!((x1.contains(user_id)) && (x2.contains(aid)))) {
      val insertAction = DBIO.seq(
        ymUserApp +=(0, user_id, aid, ts)
      )
      Await.result(dbConf.db.run(insertAction), Duration.Inf)
    }
  }


  def appUserDelete(user_id: Int, aid: Int) = {
    Logger.debug("method:appUserDelete")
    val appUserDel = ymUserApp.filter(x => (x.user_id === user_id && x.aid === aid))
    Await.result(dbConf.db.run(appUserDel.delete), Duration.Inf)
  }

  def notifyHistInsert(app_id: String) = {

    implicit def date2timestamp(date: java.util.Date): Timestamp = new Timestamp(date.getTime)

    val aid: Int = appGetAID(app_id, null)

    val ts: Timestamp = new java.util.Date
    val aidCollect = ymNotifyHist.map(_.aid)
    val aidList = Await.result(dbConf.db.run(aidCollect.result), Duration.Inf)

    if (!aidList.contains(aid)) {
      val insertAction = DBIO.seq(ymNotifyHist +=(0, app_id, aid, false, "", ts))
      Await.result(dbConf.db.run(insertAction), Duration.Inf)
    } else {
      val q = ymNotifyHist.filter(_.aid === aid).map(x => (x.app_id, x.is_notified, x.notify_info))
      val updateAction = q.update(app_id, false, "")
      Await.result(dbConf.db.run(updateAction), Duration.Inf)
    }
  }


  def sendNotify(app_id: String, notify_info: String) = {
    Logger.debug("method:sendNotify")

    implicit def date2timestamp(date: java.util.Date): Timestamp = new Timestamp(date.getTime)
    val ts: Timestamp = new java.util.Date
    val q = ymNotifyHist.filter(_.app_id === app_id).map(x => (x.is_notified, x.notify_info, x.update_on))
    val updateAction = q.update((true, notify_info, ts))
    Await.result(dbConf.db.run(updateAction), Duration.Inf)
  }

  def getMonitoringApp(): Seq[String] = {
    val notifyHist = ymNotifyHist.filter(_.is_notified === false).map(x => x.app_id)
    Await.result(dbConf.db.run(notifyHist.result), Duration.Inf)
  }

  def getMonitoringUserList(appId: String) = {
    val x = ymNotifyHist.filter(_.app_id === appId).map(_.aid)
    val notifyId = Await.result(dbConf.db.run(x.result), Duration.Inf).head

    val y = ymUserApp.filter(_.aid === notifyId).map(_.user_id)
    val userList = Await.result(dbConf.db.run(y.result), Duration.Inf)

    var finalres: Map[String, String] = Map()

    for (x <- userList) {
      val z = ymUser.filter(_.user_id === x).map(a => (a.user_email, a.user_mobile))
      val email = Await.result(dbConf.db.run(z.result), Duration.Inf).map(_._1).head
      val mobile = Await.result(dbConf.db.run(z.result), Duration.Inf).map(_._2).head
      finalres += (email -> mobile)
    }
    finalres
  }

}
