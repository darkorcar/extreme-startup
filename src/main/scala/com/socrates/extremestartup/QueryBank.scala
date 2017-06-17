package com.socrates.extremestartup

import java.util.UUID

import scala.util.Random

trait QueryBank {


  private val questions: List[() => Query] = List(
    () => BasicQuery(1, "what is the capital of Mali", "Bamako"),
    () => BasicQuery(1, "who is the Prime Minister of Great Britain", "Theresa May"),
    () => BasicQuery(1, "what is the currency used in Great Britain", "Pound"),
    () => BasicQuery(1, "what currency did Spain use before the Euro", "Peseta"),
    () => BasicQuery(1, "what is the highest mountain in the world", "Mount Everest"),
    () => BinaryOperationQuery(1, "what is @1@ plus @2@", { case (a, b) => a + b }),
    () => UnaryOperationQuery(2, "what is the square root of @1@", { case (a) => Math.sqrt(a).toInt }),
    () => UnaryOperationQuery(2, "what is the @1@^2", { case (a) => a * a }),
    () => UnaryOperationQuery(2, "what is the @1@^3", { case (a) => a * a * a }),
    () => BinaryOperationQuery(3, "what is @1@ minus @2@", { case (a, b) => a - b }),
    () => BinaryOperationQuery(3, "what is @1@ times @2@", { case (a, b) => a * b }),
    () => ListOperationQuery(4, "what is the highest number", { list => list.max }),
    () => ListOperationQuery(4, "what is the lowest number", { list => list.min }),
    () => UnaryOperationQuery(5, "what is the factorial of @1@", { case (a) => factorial(a) }),
    () => ListToStringOperationQuery(5, "which are the even numbers", { case list: List[Int] => list.filter(_ % 2 == 0).mkString(",") }),
    () => ListToStringOperationQuery(5, "which are the odd numbers", { case list: List[Int] => list.filter(_ % 2 == 1).mkString(",") }),
    () => ListToStringOperationQuery(7, "which are the prime numbers", { case list: List[Int] => list.filter(isPrime).mkString(",") }),
    () => UnaryOperationQuery(7, "what is the @1@th number in the Fibonacci sequence", { case (a) => fib(a) }),
    () => TernaryOperationQuery(8, "what is @1@ plus @2@ plus @3@", { case (a, b, c) => a + b + c }),
    () => TernaryOperationQuery(8, "what is @1@ plus @2@ multiplied by @3@", { case (a, b, c) => a + b * c }),
    () => TernaryOperationQuery(8, "what is @1@ multiplied by @2@ plus @3@", { case (a, b, c) => a * b + c })
  )


  def nextQuery(round: Int): Query = {
    val minLevel = calculateMinLevel(round)
    val maxLevel = calculateMaxLevel(round)

    val filtered = questions
      .map(_())
      .filter(_.difficultyLevel >= minLevel)
      .filter(_.difficultyLevel <= maxLevel)

    Random.shuffle(filtered).head
  }

  private def fib(n: Int): Int = n match {
    case 0 | 1 => n
    case _ => fib(n - 1) + fib(n - 2)
  }

  private def factorial(n: Int): Int = n match {
    case 0 => 1
    case _ => n * factorial(n - 1)
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

  //One round each 10 seconds
  private def calculateMinLevel(round: Int): Int = (round / 60) - 2
  private def calculateMaxLevel(round: Int): Int = if (round < 180) (round / 60) + 1 else Int.MaxValue
}


trait Query {
  val hash:String = UUID.randomUUID().toString
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

  val number = Random.nextInt(10)

  override val question: String = text
    .replace("@1@", number.toString)
  override val expectedAnswer: String = operation(number).toString

}


case class BinaryOperationQuery(override val difficultyLevel: Int, text: String, operation: (Int, Int) => Int) extends Query {

  val number1 = Random.nextInt(10)
  val number2 = Random.nextInt(10)

  override val question: String = text
    .replace("@1@", number1.toString)
    .replace("@2@", number2.toString)
  override val expectedAnswer: String = operation(number1, number2).toString

}

case class TernaryOperationQuery(override val difficultyLevel: Int, text: String, operation: (Int, Int, Int) => Int) extends Query {

  val number1 = Random.nextInt(10)
  val number2 = Random.nextInt(10)
  val number3 = Random.nextInt(10)

  override val question: String = text
    .replace("@1@", number1.toString)
    .replace("@2@", number2.toString)
    .replace("@3@", number2.toString)
  override val expectedAnswer: String = operation(number1, number2, number3).toString

}

case class ListOperationQuery(override val difficultyLevel: Int, text: String, operation: (List[Int]) => Int) extends Query {

  var numbers = List(Random.nextInt(10), Random.nextInt(10) + 10, Random.nextInt(10) + 20, Random.nextInt(10) + 30)

  override val question: String = s"$text: ${numbers.mkString(",")}"

  override val expectedAnswer: String = operation(numbers).toString

}

case class ListToStringOperationQuery(override val difficultyLevel: Int, text: String, operation: (List[Int]) => String) extends Query {

  var numbers = List(Random.nextInt(10), Random.nextInt(10) + 10, Random.nextInt(10) + 20, Random.nextInt(10) + 30)

  override val question: String = s"$text: ${numbers.mkString(",")}"

  override val expectedAnswer: String = operation(numbers).toString

}
