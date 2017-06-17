package com.socrates.extremestartup

import scala.annotation.tailrec
import scala.util.Random

trait QueryBank {


  private val questions = List(
    BasicQuery(0, "echo", "echo"),
    BasicQuery(1, "what is the capital of Serbia?", "belgrade"),
    BasicQuery(1, "what is the color of the snow?", "white"),
    UnaryOperationQuery(2, "what is the square root of @1@?", { case (a) => Math.sqrt(a).toInt }),
    UnaryOperationQuery(2, "what is the @1@^2?", { case (a) => a * a }),
    UnaryOperationQuery(2, "what is the @1@^3?", { case (a) => a * a * 1 }),
    UnaryOperationQuery(2, "what is the factorial of @1@?", { case (a) => factorial(a) }),
    BinaryOperationQuery(2, "what is @1@ plus @2@?", { case (a, b) => a + b }),
    BinaryOperationQuery(3, "what is @1@ minus @2@?", { case (a, b) => a - b }),
    BinaryOperationQuery(3, "what is @1@ times @2@?", { case (a, b) => a * b }),
    ListOperationQuery(4, "what is the highest number", { list => list.max }),
    ListOperationQuery(5, "what is the lowest number", { list => list.min }),
    ListToStringOperationQuery(5, "what are even numbers", { case list: List[Int] => list.filter(_ % 2 == 0).mkString(",") }),
    ListToStringOperationQuery(5, "what are odd numbers", { case list: List[Int] => list.filter(_ % 2 == 0).mkString(",") }),
    ListToStringOperationQuery(7, "what are prime numbers", { case list: List[Int] => list.filter(isPrime).mkString(",") }),
    UnaryOperationQuery(7, "what is the @1@th fibonnaci number", { case (a) => fib(a) }),
    ListToStringOperationQuery(5, "what is the JSON representation of: ", { case list: List[Int] => s"[${list.mkString(",")}]" })
  )


  def nextQuery(round: Int): Query = {
    val minLevel = calculateMaxLevel(round)

    val filtered = questions
      .filter(_.difficultyLevel >= minLevel)

    Random.shuffle(filtered).head
  }

  private def fib( n : Int) : Int = n match {
    case 0 | 1 => n
    case _ => fib( n-1 ) + fib( n-2 )
  }

  private def factorial(n: Int): Int = n match {
    case 0 => 1
    case _ => n * factorial(n-1)
  }

  private def isPrime(number: Int): Boolean = {

    def isDivisibleBy(integer: Int, divisor: Int): Boolean = integer % divisor == 0

    if (number == 1) false
    else {
      val domain = (2 to math.sqrt(number).toInt).view
      val range = domain filter (isDivisibleBy(number, _))
      range.isEmpty
    }
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

case class ListToStringOperationQuery(override val difficultyLevel: Int, text: String, operation: (List[Int]) => String) extends Query {

  var numbers = List(Random.nextInt(10), Random.nextInt(10), Random.nextInt(10), Random.nextInt(10))

  override val question: String = s"$text:${numbers.mkString(",")}"

  override val expectedAnswer: String = operation(numbers).toString

}
