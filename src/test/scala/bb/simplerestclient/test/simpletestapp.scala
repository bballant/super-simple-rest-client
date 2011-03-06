package simplerestclient.test

import simplerestclient.HttpResponse
import simplerestclient.Get
import simplerestclient.RequestMethod

import java.net.HttpURLConnection

object TestApp {
  import RequestMethod._

  val testSimple = () => {
    assert(GET != POST)
    true
  }

  val testGet = () => {
    val r: HttpResponse = Get("http://www.github.com")
    val response:String = r.body
    assert(response != null || response.length > 0)
    true
  }

  val testGetWithParams = () => {
    val r: HttpResponse = Get(
      "http://www.github.com", 
      Map("locale"->"en_gb", "a"->"1", "b"->"2", "c"->"c3"))

    val response:String = r.body
    assert(response != null || response.length > 0)
    true
  }

  val test404 = () => {
    val r: HttpResponse = Get("http://www.google.com/this-will-respond-with-404")

    val responseCode: Int = r.responseCode
    assert(responseCode == 404)

    val errorMessage: String = r.errorMessage
    assert(errorMessage != null || errorMessage.length > 0)
    true
  }

  def main(args: Array[String]) {
    println("--- RUNNING SOME SUPER SIMPLE TESTS ---");

    // make a list of test functions
    val allTheTests = List(
      testSimple, 
      testGet,
      testGetWithParams,
      test404
    )

    // set up a little incrementer
    var i = 0
    def inc:Int = {i=i+1; i;}

    // run all the tests
    for (test <- allTheTests)
      println("Test " + inc + " : " + test())
    
  }
}
