/*******************************************************************************
 * Copyright (c) 2011 Brian Ballantine
 *
 * Based on SimpleRestClient
 *
 * Copyright (c) 2008, 2009 Brian Ballantine and Bug Labs, Inc.
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

case class FormFile(filename: String, contentType: String, bytes: Array[Byte])

object MultipartHelper {
  import java.io.OutputStream
  import java.net.HttpURLConnection

	val HeaderType  = "Content-Type"
  val HeaderPara  = "Content-Disposition: form-data"
  val ContentType = "multipart/form-data"
  val LineEnding  = "\r\n"
  val Boundary    = "boundary="
  val ParaName    = "name"
  val FileName    = "filename"

  def sendMultipart(conn: HttpURLConnection, 
      parameters: Map[String, String] = Map(), files: Map[String, FormFile] = Map()) {
    val boundary = createMultipartBoundary
		conn.setRequestProperty(HeaderType, ContentType+"; "+Boundary+boundary)
    var os = conn.getOutputStream
    for (p <- parameters) {
      val buf = new StringBuffer()
      buf.append("--"+boundary+LineEnding)
      buf.append(HeaderPara)
      buf.append("; "+ParaName+"=\""+p._1+"\"")
      buf.append(LineEnding)
      buf.append(LineEnding)
      buf.append(p._2)
      os.write(buf.toString.getBytes)
    }
    for (f <- files) {
      val buf = new StringBuffer()
      buf.append("--"+boundary+LineEnding)
      buf.append(HeaderPara)
      buf.append("; "+ParaName+"=\""+f._1+"\"")
      buf.append("; "+FileName+"=\""+f._2.filename+"\""+LineEnding)
      buf.append(HeaderType+": "+f._2.contentType+";")
      buf.append(LineEnding)
      buf.append(LineEnding)
      os.write(buf.toString.getBytes)
      os.write(f._2.bytes)
    }
	  os.write(("--"+boundary+"--"+LineEnding).getBytes)
    os.flush
    os.close
  }

	private def createMultipartBoundary: String = {
    val buf = new StringBuffer()
    buf.append("---------------------------")
    for (i: Int <- 1 to 15) {
      val rand = (scala.math.random * 35).asInstanceOf[Int]
      if (rand < 10) buf.append(rand)
      else buf.append((87 + rand).asInstanceOf[Char])
    }
    buf.toString
	}
}
