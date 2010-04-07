package com.codahale.yoink.tests

import org.scalatest.matchers.MustMatchers
import com.codahale.yoink.Atom
import org.scalatest.{OneInstancePerTest, Spec}
import org.scalatest.concurrent.Conductor

class AtomTest extends Spec with MustMatchers with OneInstancePerTest {
  describe("an atom") {
    val atom = new Atom("one")

    it("has a value") {
      atom.value must equal("one")
    }

    it("is human readable") {
      atom.toString must equal("one")
    }

    it("can be swapped") {
      atom.swap { s =>
        s + " two three"
      }

      atom.value must equal("one two three")
    }
  }

  describe("an atom being concurrently swapped") {
    val conductor = new Conductor
    val atom = new Atom(0L)

    it("maintains referential integrity") {
      1.to(100).foreach { i =>
        conductor.thread("mutator " + i) {
          1.to(100).foreach { j =>
            atom.swap { v => v + 1 }
          }
        }
      }

      conductor.whenFinished {
        atom.value must equal(100 * 100)
      }
    }
  }
}
