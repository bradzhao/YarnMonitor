package utils

/**
  *
  *
  * Project: YarnMonitor
  *
  * @author Derek Wang, Brad Zhao
  * @version 1.0
  * @since : 2016-01-31 19:00
  * @note
  *
  *
  */

import java.io.File
import java.util.Properties
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet._

import play.api.Logger
import utils.YarnContext._

object MailSendings {
  val host = property.getString("email.send.host")
  val username = property.getString("email.send.username")
  val password = property.getString("email.send.password")
  val sender = property.getString("email.send.sender")
  val nick = property.getString("email.send.nick")
  val protocol = property.getString("email.send.protocol")
  val auth = property.getString("email.send.auth")
  //with attachment

  def sendEmail(receipients: List[String], attachment: String, subject: String, content: String) = {
    Logger.debug("MailSendings.sendEmail------------4--------start")
    Logger.info( s"""receipients:{${receipients.toString()}}, attachment:$attachment, subject:$subject, content:$content""")
    val prop = new Properties()
    prop.setProperty("mail.host", host)
    prop.setProperty("mail.transport.protocol", protocol)
    prop.setProperty("mail.smtp.auth", auth)
    val session = Session.getInstance(prop)
    session.setDebug(true)
    val transport = session.getTransport()
    transport.connect(host, username, password)
    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(sender))

    try {
      receipients.map(x => x.toString).foreach(message.addRecipients(javax.mail.Message.RecipientType.TO, _))
    } catch {
      case ex: NullPointerException => Logger.error(s"""--------null pointer1111, NullPointerException: ${ex.getMessage}""")
      case ex: Exception => Logger.error(s"""--------sendEmail exception111, Exception: ${ex.getMessage}""")
    }
    try {
      message.setSubject(subject)
      val affix = new File(attachment)
      val multipart = new MimeMultipart()
      if (attachment != null) {
        val attachmentBodyPart = new MimeBodyPart()
        val source = new FileDataSource(attachment)
        attachmentBodyPart.setDataHandler(new DataHandler(source))
        attachmentBodyPart.setFileName(MimeUtility.encodeWord(affix.getName()))
        //this is to add the attachment
        multipart.addBodyPart(attachmentBodyPart)
      }
      val contentPart = new MimeBodyPart()
      //this is to add content
      contentPart.setText(content)
      multipart.addBodyPart(contentPart)
      message.setContent(multipart)
      message.saveChanges()
    } catch {
      case ex: NullPointerException => Logger.error(s"""--------null pointer2222, NullPointerException: ${ex.getMessage}""")
      case ex: Exception => Logger.error(s"""--------sendEmail exception2222, NullPointerException: ${ex.getMessage}""")
    }

    try {
      transport.sendMessage(message, message.getAllRecipients)
    } catch {
      case ex: NullPointerException => Logger.error(s"""null pointer3, NullPointerException:${ex.getMessage}, receipients: ${receipients.toString()}, attachment: $attachment, subject: $subject, content: $content""")
      case ex: Exception => Logger.error(s"""----------------null--------------transport.sendMessage(message, message.getAllRecipients), Exception:${ex.getMessage}""")
    }
    Logger.debug("MailSendings.sendEmail-----------4---------end")
  }

  //without attachment
  def sendEmail(receipients: List[String], subject: String, content: String) = {
    Logger.debug("MailSendings.sendEmail----------3----------start")
    Logger.info( s"""receipients:{${receipients.toString()}}, subject:$subject, content:$content""")
    val prop = new Properties()
    prop.setProperty("mail.host", host)
    prop.setProperty("mail.transport.protocol", protocol)
    prop.setProperty("mail.smtp.auth", auth)
    val session = Session.getInstance(prop)
    session.setDebug(true)
    val transport = session.getTransport()
    transport.connect(host, username, password)
    val message: MimeMessage = new MimeMessage(session)
    message.setFrom(new InternetAddress(sender))

    val emailAddress: List[String] = receipients.map(x => x.toString)
    emailAddress.foreach(println)

    try {
      emailAddress.foreach(message.addRecipients(javax.mail.Message.RecipientType.TO, _))
    } catch {
      case ex: NullPointerException => Logger.error(s"""null pointer4, NullPointerException: ${ex.getMessage}, receipients:{${receipients.toString()}}, subject:$subject, content:$content""")
      case ex: Exception => Logger.error(s"""null exception444, NullPointerException: ${ex.getMessage}, receipients:{${receipients.toString()}}, subject:$subject, content:$content""")
    }

    //This is to add content.
    message.setContent(content, "text/html;charset=UTF-8")
    message.setSubject(subject)
    message.saveChanges()

    try {
      transport.sendMessage(message, message.getAllRecipients)
    } catch {
      case ex: NullPointerException => Logger.error(s"""null pointer5, receipients: ${receipients.toString()}, subject: $subject, content: $content""")
      case ex: Exception => Logger.error(s"""null exception555, Exception: ${ex.getMessage}, receipients:{${receipients.toString()}}, subject:$subject, content:$content""")
    }
    Logger.debug("MailSendings.sendEmail----------3----------end")
  }
}
