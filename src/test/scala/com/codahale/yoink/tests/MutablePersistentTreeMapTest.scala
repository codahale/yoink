package com.codahale.yoink.tests

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import com.codahale.yoink.{MutablePersistentTreeMap}
import org.scalatest.concurrent.Conductor

class MutablePersistentTreeMapTest extends Spec with MustMatchers {
  describe("a mutable persistent tree map") {
    val map = makeMap

    it("accesses values") {
      map.get("one") must equal(Some(1))
      map("one") must equal(1)
    }

    it("iterates over all the elements") {
      map.toList.sortBy { case (x, y) => y } must equal(List(("one", 1), ("two", 2), ("three", 3)))
    }

    describe("with an added value") {
      val other = makeMap
      other += ("four" -> 4)

      it("has the added value") {
        other("four") must equal(4)
      }

      it("has an updated size") {
        other.size must equal(4)
      }
    }

    describe("with a removed value") {
      val other = makeMap
      other -= "three"

      it("does not have the removed value") {
        other.get("three") must equal(None)
      }

      it("has an updated size") {
        other.size must equal(2)
      }
    }

    describe("being modified concurrently") {
      val other = MutablePersistentTreeMap[Int, Int]()
      val conductor = new Conductor

      it("maintains referential integrity") {
        1.to(100).foreach { i =>
          conductor.thread("adder " + i) {
            1.to(100).foreach { j =>
              other += ((i * 100 + j) -> 1)
            }
          }
        }

        conductor.whenFinished {
          other.size must equal(100 * 100)
        }
      }
    }
  }

  def makeMap = MutablePersistentTreeMap("one" -> 1, "two" -> 2, "three" -> 3)
}
