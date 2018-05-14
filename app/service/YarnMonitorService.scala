package service

/**
  *
  *
  * Project: YarnMonitor 
  * @author Collins Xiao
  * @version 1.0
  * @since : 01 02 2016
  * @note
  *
  *
  */

import utils.ExcutingCommand._

object YarnMonitorService {
  def getAppLists = {
    val allAppsList = List("yarn", "application", "-list")
    setYarnCommand(allAppsList)
  }

  def getSingleAppStatu(app_id: String) = {
    val allAppsList = List("yarn", "application", "-status", s"$app_id")
    setYarnCommand(allAppsList)
  }

  def getAppLogs(app_id: String) = {
    val allAppsList = List("yarn", "logs", "-applicationId", s"$app_id")
    setYarnCommand(allAppsList)
  }
}
