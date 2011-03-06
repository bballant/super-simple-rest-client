/*******************************************************************************
 * Copyright (c) 2011 Brian Ballantine
 *
 * Based on SimpleRestClient
 *
 * Copyright (c) 2011 Brian Ballantine 
 * 
 * MIT License
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *  
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package simplerestclient

import java.io.{BufferedReader, InputStream, InputStreamReader}
import java.lang.StringBuilder
import java.net.HttpURLConnection

object HttpResponse {
  val DefaultErrorMessage = "There was a connection error.  " + 
                            "The server responded with status code ";

  private def inputStreamToString(is: InputStream) = {
    val rd: BufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8")) 
    val responseBuilder = new StringBuilder()    
    try {
      var line = rd.readLine 
      while (line != null) { 
        responseBuilder.append(line + "\n")
        line = rd.readLine
      }
    } finally {
      rd.close
    }
    responseBuilder.toString
  }
}

class HttpResponse(connection: HttpURLConnection) {
  import HttpResponse._

  val responseCode: Int = connection.getResponseCode

  val body: String = {
    try {
      val is: InputStream = connection.getInputStream
      inputStreamToString(is)
    } catch {
      case e: Exception => errorString = e.getMessage
      new String()
    }
  }

  private var errorString: String = _
  def errorMessage: String = {
    if (errorString == null && responseCode >= 400) {
      val defaultMessage = DefaultErrorMessage + responseCode + "."
      errorString =
        try {
          val is: InputStream = connection.getErrorStream 
          if (is != null) inputStreamToString(is)
          else defaultMessage
        } catch { 
          case _ => defaultMessage 
        }
    }
    errorString 
  }
}
