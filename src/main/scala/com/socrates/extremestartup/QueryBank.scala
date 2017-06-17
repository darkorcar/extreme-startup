package com.socrates.extremestartup

import scala.util.Random

trait QueryBank {


  private val questions = List(
    BasicQuery(0, "echo", "echo"),
    BasicQuery(1, "what is the capital of Serbia?", "belgrade"),
    BasicQuery(1, "what is the color of the snow?", "white"),
    UnaryOperationQuery(2, "what is the square root of @1@?", { case (a) => Math.sqrt(a).toInt }),
    BinaryOperationQuery(2, "what is @1@ plus @2@?", { case (a, b) => a + b }),
    BinaryOperationQuery(3, "what is @1@ minus @2@?", { case (a, b) => a - b }),
    BinaryOperationQuery(3, "what is @1@ times @2@?", { case (a, b) => a * b }),
    ListOperationQuery(4, "what is the highest number", { list => list.max }),
    ListOperationQuery(5, "what is the lowest number", { list => list.min })
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
                       override val difficultyLevel: Int,
                       override val question: String,
                       override val expectedAnswer: String
                     ) extends Query


case class UnaryOperationQuery(override val difficultyLevel: Int, text: String, operation: (Int) => Int) extends Query {

  val number1 = Random.nextInt(10)

  override val question: String = text
    .replace("@1@", (number1 * number1).toString)
  override val expectedAnswer: String = operation(number1).toString

}

case class BinaryOperationQuery(override val difficultyLevel: Int, text: String, operation: (Int, Int) => Int) extends Query {

  val number1 = Random.nextInt(10)
  val number2 = Random.nextInt(10)

  override val question: String = text
    .replace("@1@", number1.toString)
    .replace("@2@", number2.toString)
  override val expectedAnswer: String = operation(number1, number2).toString

}

case class ListOperationQuery(override val difficultyLevel: Int, text: String, operation: (List[Int]) => Int) extends Query {

  var numbers = List(Random.nextInt(10), Random.nextInt(10), Random.nextInt(10), Random.nextInt(10))

  override val question: String = s"$text:${numbers.mkString(",")}"

  override val expectedAnswer: String = operation(numbers).toString

}
