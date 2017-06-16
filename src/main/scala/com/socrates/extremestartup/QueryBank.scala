package com.socrates.extremestartup

import com.socrates.extremestartup.QueryBank.Query

import scala.util.Random

trait QueryBank {


  private val questions = List(
    Query("what is 2 plus 3?", "5"),
    Query("what is 4 minus 3?", "1")
  )

  def nextQuery(): Query = questions(Random.nextInt(questions.size))
}

object QueryBank {

  case class Query(question: String, expectedAnswer: String)

}
