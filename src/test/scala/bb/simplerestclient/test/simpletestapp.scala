package bb.simplerestclient.test

import bb.simplerestclient.jv.HTTPResponse
import bb.simplerestclient.Get
import bb.simplerestclient.RequestType

object TestApp {
  import RequestType._

  val basicTest = () => {
    assert(GET != POST)
    true
  }

  val simpleGetTest = () => {
    val r: HTTPResponse = Get("http://localhost:8666")
    val response:String = r.getString
    assert(response != null || response.length > 0)
    true
  }

  def main(args: Array[String]) {
    println("--- RUNNING SOME SUPER SIMPLE TESTS ---");

    // make a list of test functions
    val allTheTests = List(basicTest, simpleGetTest)

    // set up a little incrementer
    var i = 0
    def inc:Int = {i=i+1; i;}

    // run all the tests
    for (test <- allTheTests)
      println("Test " + inc + " : " + test())
    
  }
}
