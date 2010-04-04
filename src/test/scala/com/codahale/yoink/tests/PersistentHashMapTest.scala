package com.codahale.yoink.tests

import org.scalatest.Spec
import org.scalatest.matchers.MustMatchers
import com.codahale.yoink.PersistentHashMap

class PersistentHashMapTest extends Spec with MustMatchers {
  describe("a persistent hash map") {
    val map = PersistentHashMap("one" -> 1, "two" -> 2, "three" -> 3)

    it("accesses values") {
      map.get("one") must equal(Some(1))
      map("one") must equal(1)
    }

    it("iterates over all the elements") {
      map.toList must equal(List(("one", 1), ("two", 2), ("three", 3)))
    }

    describe("with an added value") {
      val other = map + ("four" -> 4)

      it("has the added value") {
        other("four") must equal(4)
      }

      it("has an updated size") {
        other.size must equal(4)
      }
    }

    describe("with a removed value") {
      val other = map - "three"

      it("does not have the removed value") {
        other.get("three") must equal(None)
      }

      it("has an updated size") {
        other.size must equal(2)
      }
    }
  }
}
