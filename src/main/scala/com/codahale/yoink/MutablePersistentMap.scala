package com.codahale.yoink

import collection.mutable.{MapLike, Map}
import collection.generic.{MutableMapFactory, CanBuildFrom}
import clojure.lang.{APersistentMap, PersistentTreeMap => CPersistentTreeMap, PersistentHashMap => CPersistentHashMap}

trait MutablePersistentMap[A, B] extends Map[A, B] {
  val ref: Atom[APersistentMap]

  def -=(elem: A): this.type = {
    ref.swap { _.without(elem).asInstanceOf[APersistentMap] }
    this
  }

  def iterator = new PersistentMapIterator[A, B](ref.value.iterator)

  def get(key: A): Option[B] = {
    val e = ref.value.get(key)
    if (e eq null) None else Some(e.asInstanceOf[B])
  }

  def +=(elem: (A, B)): this.type = {
    ref.swap { _.assoc(elem._1, elem._2).asInstanceOf[APersistentMap] }
    this
  }
}

object MutablePersistentTreeMap extends MutableMapFactory[MutablePersistentTreeMap] {
  implicit def canBuildFrom[A, B]: CanBuildFrom[Coll, (A, B), MutablePersistentTreeMap[A, B]] = new MapCanBuildFrom[A, B]
  def empty[A, B]: MutablePersistentTreeMap[A, B] = new MutablePersistentTreeMap[A, B](new Atom(CPersistentTreeMap.EMPTY))
}

/**
 * A mutable, persistent map, backed by an atomic reference to a Clojure
 * persistent tree map.
 */
@serializable @SerialVersionUID(1L)
class MutablePersistentTreeMap[A, B](val ref: Atom[APersistentMap]) extends MutablePersistentMap[A, B]
                       with MapLike[A, B, MutablePersistentTreeMap[A, B]] {
  override def empty = MutablePersistentTreeMap.empty
}

object MutablePersistentHashMap extends MutableMapFactory[MutablePersistentHashMap] {
  implicit def canBuildFrom[A, B]: CanBuildFrom[Coll, (A, B), MutablePersistentHashMap[A, B]] = new MapCanBuildFrom[A, B]
  def empty[A, B]: MutablePersistentHashMap[A, B] = new MutablePersistentHashMap[A, B](new Atom(CPersistentHashMap.EMPTY))
}

/**
 * A mutable, persistent map, backed by an atomic reference to a Clojure
 * persistent hash map.
 */
@serializable @SerialVersionUID(1L)
class MutablePersistentHashMap[A, B](val ref: Atom[APersistentMap]) extends MutablePersistentMap[A, B]
                       with MapLike[A, B, MutablePersistentHashMap[A, B]] {
  override def empty = MutablePersistentHashMap.empty
}
