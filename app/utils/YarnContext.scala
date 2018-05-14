package utils

import com.typesafe.config.ConfigFactory

/**
  *
  *
  * Project: YarnMonitor
  *
  * @author Brad Zhao
  * @version 1.0
  * @since : 2016-01-31 18:40
  * @note
  *
  *
  */
object YarnContext {

  lazy val property = ConfigFactory.load()
}
