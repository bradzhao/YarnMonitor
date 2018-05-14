package utils

import java.text.SimpleDateFormat
import java.util.Date
import play.api.Logger

/**
  *
  *
  * Project: YarnMonitor
  *
  * @author Brad Zhao
  * @version 1.0
  * @since : 2016-02-03 11:39
  * @note
  *
  *
  */
object DateUtil {

  val sdf:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def getFormatTime(time:Long):String = {
    sdf.format(new Date(time))
  }

  def getFormatTime(time:Long, format:String):String = {
    new SimpleDateFormat(format).format(new Date(time))
  }
}
