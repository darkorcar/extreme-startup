package com.socrates.extremestartup

import scala.util.Random

trait QueryBank {


  private val questions = List(
    BasicQuery("what is the capital of Serbia?", "Belgrade"),
    BinaryOperationQuery("what is the sum of @1@ plus @2@?", { case (a, b) => a + b })
  )


  def nextQuery(round: Int): Query = {
    val minLevel = calculateMaxLevel(round)

    val filtered = questions
      .filter(_.difficultyLevel >= minLevel)

    Random.shuffle(filtered).head
  }

  private def calculateMaxLevel(round: Int): Int = 0
}


trait Query {
  val question: String
  val expectedAnswer: String

  def difficultyLevel: Int
}

case class BasicQuery(
                       override val question: String,
                       override val expectedAnswer: String
                     ) extends Query {
  override def difficultyLevel: Int = 1
}

case class BinaryOperationQuery(text: String, operation: (Int, Int) => Int) extends Query {

  val number1 = Random.nextInt(10)
  val number2 = Random.nextInt(10)

  override val question: String = text
    .replace("@1@", number1.toString)
    .replace("@2@", number2.toString)
  override val expectedAnswer: String = operation(number1, number2).toString

  override def difficultyLevel: Int = 2
}
