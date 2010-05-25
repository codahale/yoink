package com.codahale.yoink.benchmarks

import collection.immutable.HashMap
import com.codahale.yoink.PersistentHashMap
import util.Random
import java.io.{PrintWriter, FileWriter}

object HashMapBenchmark {
  val MaxItems = 100000
  val MaxRandScope = MaxItems * 100

  def buildSequentialStdLib() {
    var m: Map[Int, Int] = HashMap()
    for (i <- 1 to MaxItems) {
      m += (i -> i)
    }
    for (i <- 1 to MaxItems) {
      m -= i
    }
  }

  def buildRandomStdLib() {
    var m: Map[Int, Int] = HashMap()
    val r = new Random
    for (i <- 1 to MaxItems) {
      m += (r.nextInt(MaxRandScope) -> i)
    }
    for (i <- 1 to MaxItems) {
      m -= r.nextInt(MaxRandScope)
    }
  }

  def buildSequentialYoink() {
    var m: Map[Int, Int] = PersistentHashMap()
    for (i <- 1 to MaxItems) {
      m += (i -> i)
    }
    for (i <- 1 to MaxItems) {
      m -= i
    }
  }

  def buildRandomYoink() {
    var m: Map[Int, Int] = PersistentHashMap()
    val r = new Random
    for (i <- 1 to MaxItems) {
      m += (r.nextInt(MaxRandScope) -> i)
    }
    for (i <- 1 to MaxItems) {
      m -= r.nextInt(MaxRandScope)
    }
  }

  def main(args: Array[String]) {

    println("warming up")
    for (i <- 1 to 100) {
      buildSequentialStdLib()
      buildRandomStdLib()
      buildSequentialYoink()
      buildRandomYoink()
    }

    System.gc(); System.gc(); System.gc(); System.gc(); System.gc()

    var sequentialStdLib: List[Double] = Nil
    for (i <- 1 to 40) {
      val t = System.nanoTime
      buildSequentialStdLib()
      val elapsed = (System.nanoTime - t) * 1e-6
      sequentialStdLib = elapsed :: sequentialStdLib
      println("StdLib, Sequential: Run %02d: %2.2fms".format(i, elapsed))
      System.gc(); System.gc(); System.gc(); System.gc(); System.gc()
    }
    logResults("stdlib-sequential", sequentialStdLib)

    var randomStdLib: List[Double] = Nil
    for (i <- 1 to 40) {
      val t = System.nanoTime
      buildRandomStdLib()
      val elapsed = (System.nanoTime - t) * 1e-6
      randomStdLib = elapsed :: randomStdLib
      println("StdLib, Random: Run %02d: %2.2fms".format(i, elapsed))
      System.gc(); System.gc(); System.gc(); System.gc(); System.gc()
    }
    logResults("stdlib-random", randomStdLib)

    var sequentialYoink: List[Double] = Nil
    for (i <- 1 to 40) {
      val t = System.nanoTime
      buildSequentialYoink()
      val elapsed = (System.nanoTime - t) * 1e-6
      sequentialYoink = elapsed :: sequentialYoink
      println("Yoink, Sequential: Run %02d: %2.2fms".format(i, elapsed))
      System.gc(); System.gc(); System.gc(); System.gc(); System.gc()
    }
    logResults("yoink-sequential", sequentialYoink)

    var randomYoink: List[Double] = Nil
    for (i <- 1 to 40) {
      val t = System.nanoTime
      buildRandomYoink()
      val elapsed = (System.nanoTime - t) * 1e-6
      randomYoink = elapsed :: randomYoink
      println("Yoink, Random: Run %02d: %2.2fms".format(i, elapsed))
      System.gc(); System.gc(); System.gc(); System.gc(); System.gc()
    }
    logResults("yoink-random", randomYoink)
  }

  def logResults(name: String, timings: Iterable[Double]) {
    val out = new PrintWriter(new FileWriter("target/%s.log".format(name)))
    try {
      timings.foreach { out.println(_) }
    } finally {
      out.close()
    }
  }
}
