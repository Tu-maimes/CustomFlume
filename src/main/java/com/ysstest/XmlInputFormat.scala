package com.ysstest

import java.io.RandomAccessFile
import java.nio.charset.Charset

import com.ysstest.xmltojson.XML
import org.apache.hadoop.io.DataOutputBuffer


/**
  * @Auther: wangshaui
  * @Date: 2018/9/12 16:08
  * @Description:
  */
object XmlInputFormat {
  /** configuration key for start tag */
  val START_TAG_KEY: String = "<properties>"
  /** configuration key for end tag */
  val END_TAG_KEY: String = "</properties>"
  /** configuration key for encoding type */
  val ENCODING_KEY: String = "UTF-8"
}

case class XmlInputFormat() {
  private var startTag: Array[Byte] = _
  private var currentStartTag: Array[Byte] = _
  private var endTag: Array[Byte] = _
  private var endEmptyTag: Array[Byte] = _
  private var space: Array[Byte] = _
  private var angleBracket: Array[Byte] = _
  private var start: Long = _
  private var end: Long = _
  //  private var in: InputStream = _
  private var filePosition: RandomAccessFile = _
  private val buffer: DataOutputBuffer = new DataOutputBuffer


  def initialize(file: RandomAccessFile) = {
    //    val file = new RandomAccessFile(path, "r")
    val charset = Charset.forName(XmlInputFormat.ENCODING_KEY)
    startTag = XmlInputFormat.START_TAG_KEY.getBytes(charset)
    endTag = XmlInputFormat.END_TAG_KEY.getBytes(charset)
    endEmptyTag = "/>".getBytes(charset)
    space = " ".getBytes(charset)
    angleBracket = ">".getBytes(charset)
    require(startTag != null, "Start tag cannot be null.")
    require(endTag != null, "End tag cannot be null.")
    require(space != null, "White space cannot be null.")
    require(angleBracket != null, "Angle bracket cannot be null.")
    start = file.getFilePointer
    end = start + file.length()
    filePosition = file
    filePosition.seek(start)
  }


  /**
    * Finds the start of the next record.
    * It treats data from `startTag` and `endTag` as a record.
    *
    * @param key   the current key that will be written
    * @param value the object that will be written
    * @return whether it reads successfully
    */
  def next(): Boolean = {
    if (readUntilStartElement()) {
      try {
        buffer.write(currentStartTag)
        if (readUntilEndElement()) {
          val nObject = XML.toJSONObject(new String(buffer.getData, 0, buffer.getLength))
          println(nObject.toString)
          true
        } else {
          false
        }
      } finally {
        buffer.reset
      }
    } else {
      false
    }
  }

  /**
    * @author wangshuai
    * @date 2018/9/13 13:48
    * @param []
     * @description 判断开始读的位置
    * @return boolean
    */
  private def readUntilStartElement(): Boolean = {

    currentStartTag = startTag
    var i = 0
    while (true) {
      val b = filePosition.read()
      if (b == -1 || (i == 0 && filePosition.getFilePointer > end)) {
        // End of file or end of split.
        return false
      } else {
        if (b.toByte == startTag(i)) {
          if (i >= startTag.length - 1) {
            // Found start tag.
            return true
          } else {
            // In start tag.
            i += 1
          }
        } else {
          if (i == (startTag.length - angleBracket.length) && checkAttributes(b)) {
            // Found start tag with attributes.
            return true
          } else {
            // Not in start tag.
            i = 0
          }
        }
      }
    }
    // Unreachable.
    false
  }

  /**
    * @author wangshuai
    * @date 2018/9/13 13:49
    * @param []
     * @description  判断结束读的位置
    * @return boolean
    */

  private def readUntilEndElement(): Boolean = {

    var si = 0
    var ei = 0
    var depth = 0

    while (true) {
      val rb = filePosition.read()
      if (rb == -1) {
        // End of file (ignore end of split).
        return false
      } else {
        buffer.write(rb)
        val b = rb.toByte
        if (b == startTag(si) && (b == endTag(ei) || checkEmptyTag(b, ei))) {
          // In start tag or end tag.
          si += 1
          ei += 1
        } else if (b == startTag(si)) {
          if (si >= startTag.length - 1) {
            // Found start tag.
            si = 0
            ei = 0
            depth += 1
          } else {
            // In start tag.
            si += 1
            ei = 0
          }
        } else if (b == endTag(ei) || checkEmptyTag(b, ei)) {
          if ((b == endTag(ei) && ei >= endTag.length - 1) ||
            (checkEmptyTag(b, ei) && ei >= endEmptyTag.length - 1)) {
            if (depth == 0) {
              // Found closing end tag.
              return true
            } else {
              // Found nested end tag.
              si = 0
              ei = 0
              depth -= 1
            }
          } else {
            // In end tag.
            si = 0
            ei += 1
          }
        } else {
          // Not in start tag or end tag.
          si = 0
          ei = 0
        }
      }
    }
    // Unreachable.
    false
  }


  private def checkEmptyTag(currentLetter: Int, position: Int): Boolean = {

    if (position >= endEmptyTag.length) false
    else currentLetter == endEmptyTag(position)
  }

  private def checkAttributes(current: Int): Boolean = {
    var len = 0
    var b = current
    while (len < space.length && b == space(len)) {
      len += 1
      if (len >= space.length) {
        currentStartTag = startTag.take(startTag.length - angleBracket.length) ++ space
        return true
      }
      b = filePosition.read
    }
    false
  }
}
