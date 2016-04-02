package com.amathew.kidtimer.counter

import java.util.UUID

import android.speech.tts.{UtteranceProgressListener, TextToSpeech}
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.mutable
import scala.concurrent.Future
import android.content.Context
import scala.collection.immutable.Set

class Counter(context: Context) {

  val LOG_TAG = "MainActivity"
//  var tts : TextToSpeech = null

  import com.amathew.kidtimer.counter.CountingState._
  @volatile var countState : CountingState = Initializing

  def ttsInitListener = new OnInitListener {
    override def onInit(i: Int): Unit = {
      countState = Ready
    }
  }

  var tts : TextToSpeech = null

  val countingInProgressMsg: mutable.Set[String] = mutable.Set("Counting already in progress...")

  def startCount(countVal: String, timeVal: String) : ConvertResult = {
    if(countState == Counting) {
      Log.i(LOG_TAG, "Counting already in progress... returning.");
      return ConvertResult(false, countingInProgressMsg, None, None);
    }
    if (tts == null) tts = new TextToSpeech(context, ttsInitListener)

    val convertResult: ConvertResult = convert(countVal, timeVal)
    if(convertResult.valid == true) {
      val f = Future {
        countdown(convertResult.count.get, convertResult.time.get)
        "counting complete"
      }
      val msg = s"${countVal}, ${timeVal}"
      Log.i(LOG_TAG, msg)
    } else {
      val msg = convertResult.msgs.foldLeft("") { (s1: String, s2: String) => s1 + "\n" + s2 }
      Log.i(LOG_TAG, s"convert result is not valid $msg")
    }
    return convertResult
  }

  def stopCounting() = {
    if (tts != null) { tts.stop(); countState = Ready; }
  }

  /** validates the count and integer entries and converts from String */
  def convert(count: String, time: String) : ConvertResult = {
    var valid = true
    val msgs = mutable.HashSet[String]()
    if( ! (count forall Character.isDigit) || ! (time forall Character.isDigit) ) {
      valid = false
      msgs += "Count and time must be numbers"
      return ConvertResult(valid, msgs, None, None)
    }
    val countVal : Int = count.toInt
    if(countVal > 1000) {
      valid = false
      msgs += "count cannot be greater than 1000"
    }
    val timeVal : Int = time.toInt
    if(timeVal > 1000) {
      valid = false
      msgs += "time cannot be greater than 1000"
    }
    if(valid) {
      val delay:Float = (timeVal*1000)/countVal
      if(delay < 50) {
        valid = false
        msgs += "The count is too high for the specified time. Either reduce the count or increase the time."
        return ConvertResult(valid, msgs, None, None)
      }
    }
    ConvertResult(valid, msgs, Some(countVal), Some(timeVal))
  }

  def countdown(count: Int, time: Int) : Unit = {
    Log.i(LOG_TAG, s"countdown starting... count = $count, time = $time")
    Log.d(LOG_TAG, s"countState = $countState")

    while(countState == Initializing) {}
    countState = Counting

    val startTime = System.nanoTime()
    val delayInMillis = (time * 1000 )/ count
    Log.i(LOG_TAG, s"Delay $delayInMillis")

    for(i <- 1 to (count-1)){
      Log.d(LOG_TAG, s"$i ...")
      tts.speak(i.toString, TextToSpeech.QUEUE_ADD, null, UUID.randomUUID().toString)
      tts.playSilentUtterance(delayInMillis, TextToSpeech.QUEUE_ADD, UUID.randomUUID().toString)
//      if(countState == Interrupting) {
//        countState = Ready
//        return
//      }
//      Thread.sleep(delayInMillis)
    }

    tts.speak(count.toString, TextToSpeech.QUEUE_ADD, null, UUID.randomUUID().toString)
    val finalUttId: String = UUID.randomUUID().toString
    tts.playSilentUtterance(1, TextToSpeech.QUEUE_ADD, finalUttId)
    tts.setOnUtteranceProgressListener(new ProgressListener(this, finalUttId))
    println((System.nanoTime() - startTime))
//    countState = Ready
  }

}

class ProgressListener(counter:Counter, utteranceId: String) extends UtteranceProgressListener {
  override def onError(utteranceId: String): Unit = {}

  override def onDone(utteranceId: String): Unit = if (this.utteranceId == utteranceId) counter.countState = CountingState.Ready

  override def onStart(utteranceId: String): Unit = {}
}

case class ConvertResult(valid: Boolean, msgs: mutable.Set[String], count: Option[Int], time: Option[Int]) {
  def allMsgs = {
    msgs.foldLeft("") { (s1: String, s2: String) => s1 + "\n" + s2 }
  }
}

//class Init extends OnInitListener {
//  override def onInit(i: Int): Unit = {
//    countState = Ready
//  }
//}

object CountingState extends Enumeration {
  type CountingState = Value
//  val Initializing, Ready, Counting, Interrupting = Value
  val Initializing, Ready, Counting = Value
}