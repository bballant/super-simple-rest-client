package bb.simplerestclient.test

import bb.simplerestclient.HttpResponse
import bb.simplerestclient.Get
import bb.simplerestclient.RequestMethod

import java.net.HttpURLConnection

object TestApp {
  import RequestMethod._

  val testSimple = () => {
    assert(GET != POST)
    true
  }

  val testGet = () => {
    val r: HttpResponse = Get(url = "http://localhost:8666")
    val response:String = r.getString
    assert(response != null || response.length > 0)
    true
  }

  val testGetWithParams = () => {
    val r: HttpResponse = Get(
      url = "http://localhost:8666/account", 
      params = Map("locale"->"en_gb", "a"->"1", "b"->"2", "c"->"c3"))

    val response:String = r.getString
    assert(response != null || response.length > 0)
    true
  }

  def main(args: Array[String]) {
    println("--- RUNNING SOME SUPER SIMPLE TESTS ---");

    // make a list of test functions
    val allTheTests = List(
      testSimple, 
      testGet,
      testGetWithParams
    )

    // set up a little incrementer
    var i = 0
    def inc:Int = {i=i+1; i;}

    // run all the tests
    for (test <- allTheTests)
      println("Test " + inc + " : " + test())
    
  }
}
