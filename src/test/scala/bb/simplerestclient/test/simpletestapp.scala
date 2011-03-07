package simplerestclient.test

import simplerestclient._

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

  val testBasicAuth = () => {
    val provider: BasicAuthenticationConnectionProvider =
        new BasicAuthenticationConnectionProvider("foo", "bar")

    assert(provider.credentials.length > 0)
    assert(provider.credentials != "foo:bar")

    val r: HttpResponse = Get("http://www.google.com/", connectionProvider=provider)
    val responseCode: Int = r.responseCode
    assert(responseCode == 200)

    true
  }

  val testPost = () => {
    // yahoo lets me post from a non-browser client, others do not
    val r: HttpResponse = 
      Post("http://search.yahoo.com/search", Map("q" -> "foobar"))
    val responseCode: Int = r.responseCode
    assert(responseCode == 200)
    true
  }

  // PUT, HEAD, and DELETE do not have specialized functional objects
  // because these are less common operations
  // use SendHttpRequest and specify the method

  val testPut = () => {
    // yahoo lets me put from a non-browser client, others do not
    val r: HttpResponse = 
      SendHttpRequest(PUT, "http://search.yahoo.com/search", Map("q" -> "foobar"))
    val responseCode: Int = r.responseCode
    assert(responseCode == 200)
    true
  }

  val testHead = () => {
    val r: HttpResponse = SendHttpRequest(HEAD, "http://www.google.com")
    val responseCode: Int = r.responseCode
    assert(responseCode == 200)
    true
  }

  val testDelete = () => {
    val r: HttpResponse = 
      SendHttpRequest(DELETE, "http://search.yahoo.com/search", Map("q" -> "foobar"))
    val responseCode: Int = r.responseCode

    // yahoo ignores the delete verb, so... this test isn't really valid
    // but it shows how to do a DELETE
    assert(responseCode == 200)
    true
  }

  def main(args: Array[String]) {
    println("--- RUNNING SOME SUPER SIMPLE TESTS ---");

    // make a list of test functions
    val allTheTests = List(
      testSimple, 
      testGet,
      testGetWithParams,
      test404,
      testBasicAuth,
      testPost,
      testPut,
      testHead,
      testDelete
    )

    // set up a little incrementer
    var i = 0
    def inc:Int = {i=i+1; i;}

    // run all the tests
    for (test <- allTheTests)
      println("Test " + inc + " : " + test())
    
  }
}
