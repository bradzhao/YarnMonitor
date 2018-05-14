package service

import java.io._
import play.api.Logger
import utils.YarnContext._

/**
  *
  *
  * Project: YarnMonitor
  *
  * @author Collins Xiao
  * @version 1.0
  * @since : 03 02 2016
  * @note
  *
  *
  */
object WriteLogs {

  def logsWriter(logName: String, logs: String) = {
    val writer = new PrintWriter(new File(logName))
    writer.write(logs)
    writer.close()
  }

  def writeLogsToFile(appId: String) = {
    val logName = property.getString("logFilePath") + "/" + appId
    val logs = service.YarnMonitorService.getAppLogs(appId)
    logsWriter(logName, logs)
    logName
  }
}
