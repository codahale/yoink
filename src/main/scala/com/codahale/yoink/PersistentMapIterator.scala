package com.codahale.yoink

import clojure.lang.IMapEntry

@serializable
class PersistentMapIterator[A, +B](underlying: java.util.Iterator[_]) extends Iterator[(A, B)] {
  def next() = {
    val entry = underlying.next.asInstanceOf[IMapEntry]
    (entry.key.asInstanceOf[A], entry.`val`.asInstanceOf[B])
  }
  def hasNext = underlying.hasNext
}
