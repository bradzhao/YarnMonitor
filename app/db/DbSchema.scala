package db

import java.sql.Timestamp
import _root_.slick.backend.DatabaseConfig
import _root_.slick.driver.JdbcProfile

import play.api.Logger

/**
  * Created by merlin on 16-2-2.
  */


object DbSchema {

  val dbConf: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("database")

  import dbConf.driver.api._

  class YmUser(tag:Tag) extends Table[(Int,  String, String, Timestamp)](tag, "YM_USER") {
    def user_id = column[Int]("USER_ID", O.PrimaryKey, O.AutoInc)
    def user_email = column[String]("USER_EMAIL")
    def user_mobile = column[String]("USER_MOBILE")
    def updated_on = column[Timestamp]("UPDATED_ON")
    def * = (user_id, user_email, user_mobile, updated_on)
  }

  lazy val ymUser = TableQuery[YmUser]


  class YmApp(tag:Tag) extends Table[(Int, String, String, String)](tag, "YM_APP") {
    def aid = column[Int]("AID", O.PrimaryKey, O.AutoInc)
    def app_id = column[String]("APP_ID")
    def app_name = column[String]("APP_NAME")
    def app_description = column[String]("DESCRIPTION")
    def * = (aid, app_id, app_name, app_description)
  }

  lazy val ymApp = TableQuery[YmApp]

  class YmUserApp(tag:Tag) extends Table[(Int, Int, Int, Timestamp)](tag, "YM_USER_APP") {
    def map_id = column[Int]("MAP_ID", O.PrimaryKey, O.AutoInc)
    def user_id = column[Int]("USER_ID")
    def aid = column[Int]("AID")
    def sub_time = column[Timestamp]("SUB_TIME")
    def * = (map_id, user_id, aid, sub_time)
  }

  lazy val ymUserApp = TableQuery[YmUserApp]

  class YmNotifyHist(tag:Tag) extends Table[(Int, String, Int, Boolean, String, Timestamp)](tag, "YM_NOTIFY_HIST") {
    def notification_id = column[Int]("NOTIFICATION_ID",O.PrimaryKey ,O.AutoInc)
    def app_id = column[String]("APP_ID")
    def aid = column[Int]("AID")
    def is_notified = column[Boolean]("IS_NOTIFIED")
    def notify_info = column[String]("NOTIFY_INFO")
    def update_on = column[Timestamp]("UPDATED_ON")
    def * = (notification_id, app_id, aid, is_notified, notify_info, update_on)
  }

  lazy val ymNotifyHist = TableQuery[YmNotifyHist]

}
