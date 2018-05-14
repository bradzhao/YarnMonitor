package utils

/**
  *
  *
  * Project: YarnMonitor
  *
  * @author Brad Zhao
  * @version 1.0
  * @since : 2016-01-31 23:04
  * @note
  *
  *
  */

import java.io.{BufferedReader, InputStream, InputStreamReader}

//Excuting an order
object ExcutingCommand {

  def setYarnCommand(yarnCommands: List[String]): String = {
    val pb = new ProcessBuilder()
    if (yarnCommands.contains("-list")) {
      pb.command(yarnCommands(0), yarnCommands(1), yarnCommands(2))
    }
    else if (yarnCommands.contains("-status")) {
      pb.command(yarnCommands(0), yarnCommands(1), yarnCommands(2), yarnCommands(3))
    }
    else if (yarnCommands.contains("-applicationId")) {
      pb.command(yarnCommands(0), yarnCommands(1), yarnCommands(2), yarnCommands(3))
    }
    val process = pb.start()
    getOutPut(process.getInputStream)
  }

  def getOutPut(inputStream: InputStream) = {
    val sb: StringBuilder = new StringBuilder()
    val is = new InputStreamReader(inputStream)
    val br = new BufferedReader(is)
    var line = br.readLine()
    try {
      while (line != null) {
        sb.append(line + System.getProperty("line.separator"))
        line = br.readLine()
      }
    } finally {
      br.close()
    }
    sb.toString()
  }
}
