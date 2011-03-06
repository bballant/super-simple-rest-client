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

import simplerestclient.jv.DefaultConnectionProvider
import simplerestclient.jv.IConnectionProvider
//import simplerestclient.jv.HTTPResponse
import java.net.HttpURLConnection
import java.net.URLEncoder
import java.io.OutputStreamWriter

object RequestMethod extends Enumeration {
  type RequestMethod = Value
  val GET, POST, PUT, DELETE, HEAD = Value
}

import RequestMethod._

object Get {
  def apply(
      url: String = null,
      params: Map[String, String] = Map(),
      headers: Map[String, String] = Map(),
      cookies: Map[String, String] = Map(),
      connectionProvider: IConnectionProvider = new DefaultConnectionProvider()
    ): HttpResponse = {

    val req = new HttpRequest(
      GET, url, params, headers, cookies, false, connectionProvider)
    req.doRequest()
  }
}
  
object Post {
  def apply(
      url: String = null,
      params: Map[String, String] = Map(),
      headers: Map[String, String] = Map(),
      cookies: Map[String, String] = Map(),
      isMultipart:Boolean = false,
      connectionProvider: IConnectionProvider = new DefaultConnectionProvider()
    ): HttpResponse = {

    val req = new HttpRequest(
      POST, url, params, headers, cookies, isMultipart, connectionProvider)
    req.doRequest()
  }
}

class HttpRequest(
    val method: RequestMethod = GET, 
    val url: String = null,
    val params: Map[String, String] = Map(),
    val headers: Map[String, String] = Map(),
    val cookies: Map[String, String] = Map(),
    val isMultipart: Boolean = false,
    val connectionProvider: IConnectionProvider = new DefaultConnectionProvider()
  ) {

  def doRequest(): HttpResponse = {
    val rurl = if (method == GET) Util.mkUrl(url, params) else url
    val conn: HttpURLConnection = connectionProvider.getConnection(rurl)
    conn.setDoInput(true)
    Util.setHeaders(conn, headers)
    Util.setCookies(conn, cookies)
    if (method == POST || method == PUT){  
      Util.setHeaders(conn, Map("Content-Type" -> "application/x-www-form-urlencoded"))
      conn.setDoOutput(true)
      Util.writeData(conn, Util.paramString(params)) 
    } else {
      conn.setDoOutput(false)
    }
    new HttpResponse(conn)
  }
  
}

object Util {
  def encode(value: String) = URLEncoder.encode(value, "UTF-8")

  def paramString(params: Map[String, String]): String = {
    params.map{kv => encode(kv._1) + 
      "=" + encode(kv._2)}.mkString("&")     
  }

  def mkUrl(url: String, params: Map[String, String]): String = {
    if (params.nonEmpty) url + "?" + paramString(params)
    else url
  }

  def setHeaders(conn: HttpURLConnection, headers: Map[String, String]) {
    for (kv <- headers) conn.setRequestProperty(kv._1, kv._2)
  }

  // TODO - this is low-budget cookies, need something more robust
  def setCookies(conn: HttpURLConnection, cookies: Map[String, String]) {
    if (cookies != null && cookies.size > 0) {
      val cookieString = (for (kv <- cookies) yield (kv._1 + "=" + kv._2)).mkString("; ")
      conn.setRequestProperty("Cookie", cookieString)
    }
  }

  def writeData(conn: HttpURLConnection, data: String) {
    var osr:OutputStreamWriter = new OutputStreamWriter(conn.getOutputStream());
    osr.write(data);
    osr.flush();
    osr.close();
  }    
}
