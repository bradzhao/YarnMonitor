package utils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.URL
import java.net.URLConnection

import utils.YarnContext._
import play.api.Logger

/**
  *
  *
  * Project: YarnMonitor
  *
  * @author Brad Zhao
  * @version 1.0
  * @since : 2016-02-03 15:31
  * @note
  *
  *
  */
object HttpClient {

  def sendSMSPost(param: String): Unit = {
    Logger.info(s"""HttpClient.sendSMSPost, param:$param-------------------start""")
    val url = property.getString("smsapi")
    var out: PrintWriter = null
    var in: BufferedReader = null
    var result: String = ""
    try {
      val realUrl: URL = new URL(url)
      val conn: URLConnection = realUrl.openConnection
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
      conn.setDoOutput(true)
      conn.setDoInput(true)
      out = new PrintWriter(conn.getOutputStream)
      out.print(param)
      System.out.println(param)
      out.flush
      in = new BufferedReader(new InputStreamReader(conn.getInputStream))
      var line: String = null
      do {
        line = in.readLine
        if(line != null){
          result += line
        }
      }while(line != null)
    }
    catch {
      case e: Exception => {
        Logger.error("SendSMSPost Exception! error message:" + e.getMessage)
        e.printStackTrace
      }
    } finally {
      try {
        if(out != null) out.close
        if(in != null) in.close
        Logger.info(s"""HttpClient.sendSMSPost, result:$result-------------------end""")
        result
      }
      catch {
        case ex: IOException => {
          Logger.error("SendSMSPost Exception! error message:" + ex.getMessage)
          ex.printStackTrace
        }
      }
    }
  }

}
