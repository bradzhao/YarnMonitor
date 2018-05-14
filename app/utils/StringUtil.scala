package utils

import java.util.UUID

import play.api.Logger
/**
  *
  *
  * Project: YarnMonitor
  *
  * @author Brad Zhao
  * @version 1.0
  * @since : 2016-02-03 07:46
  * @note
  *
  *
  */
object StringUtil {

  def getUUID = {
    UUID.randomUUID().toString.replace("-", "")
  }

  def finalJobContent(jobId:String, jobName:String, startTime:Long, finishTime:Long,
                    user:String, queue:String, finalState:String): String ={
    s"""Job Name: $jobName,
       |Job Id: $jobId,
       |User: $user,
       |Queue: $queue,
       |Final State: $finalState,
       |Start-Time: ${DateUtil.getFormatTime(startTime)}
       |Finish-Time: ${DateUtil.getFormatTime(finishTime)}
     """.stripMargin
  }

  def finalJobTitle(jobName:String, finalState:String) = {
    s"""$jobName ${finalState.toLowerCase}"""
  }

  def notifyToPhoneContent(phoneList:List[String], content:String) = {
    if(phoneList == null || phoneList.isEmpty) {
      ""
    }else{
      var phones = ""
      phoneList.map{x => phones+=x + ","}
      phones = phones.substring(0, phones.length - 1)
      //"phone=13240891307&message=【YarnMonitor】Job_Id:application_13233333"
      s"""phone=$phones&message=【YarnMonitor】$content"""
    }

  }
}
