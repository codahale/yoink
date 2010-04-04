package com.codahale.yoink

import collection.immutable.MapLike
import collection.generic.{CanBuildFrom, ImmutableMapFactory}
import clojure.lang.{IMapEntry, APersistentMap, PersistentTreeMap => CPersistentTreeMap, PersistentHashMap => CPersistentHashMap}

@serializable
class PersistentMapIterator[A, +B](underlying: java.util.Iterator[_]) extends Iterator[(A, B)] {
  def next() = {
    val entry = underlying.next.asInstanceOf[IMapEntry]
    (entry.key.asInstanceOf[A], entry.`val`.asInstanceOf[B])
  }
  def hasNext = underlying.hasNext
}

@serializable
trait PersistentMap[A, +B] extends Map[A, B] {
  val underlying: APersistentMap

  override def get(key: A): Option[B] = {
    val e = underlying.get(key)
    if (e eq null) None else Some(e.asInstanceOf[B])
  }

  override def iterator = new PersistentMapIterator[A, B](underlying.iterator)

  protected def wrappedAdd[B1 >: B] (kv: (A, B1)) =
    underlying.assoc(kv._1, kv._2).asInstanceOf[APersistentMap]

  protected def wrappedRemove(key: A) =
    underlying.without(key).asInstanceOf[APersistentMap]
}

@serializable
object PersistentTreeMap extends ImmutableMapFactory[PersistentTreeMap] {
  implicit def canBuildFrom[A, B]: CanBuildFrom[Coll, (A, B), PersistentTreeMap[A, B]] = new MapCanBuildFrom[A, B]
  def empty[A, B]: PersistentTreeMap[A, B] = new PersistentTreeMap(CPersistentTreeMap.EMPTY)
}

@serializable
class PersistentTreeMap[A, +B](val underlying: APersistentMap) extends PersistentMap[A,B] with MapLike[A, B, PersistentTreeMap[A, B]] {
  override def empty = PersistentTreeMap.empty[A, B]
  override def + [B1 >: B] (kv: (A, B1)) = new PersistentTreeMap[A, B1](wrappedAdd(kv))
  override def - (key: A) = new PersistentTreeMap[A, B](wrappedRemove(key))
}

@serializable
object PersistentHashMap extends ImmutableMapFactory[PersistentHashMap] {
  implicit def canBuildFrom[A, B]: CanBuildFrom[Coll, (A, B), PersistentHashMap[A, B]] = new MapCanBuildFrom[A, B]
  def empty[A, B]: PersistentHashMap[A, B] = new PersistentHashMap(CPersistentHashMap.EMPTY)
}

@serializable
class PersistentHashMap[A, +B](val underlying: APersistentMap) extends PersistentMap[A,B] with MapLike[A, B, PersistentHashMap[A, B]] {
  override def empty = PersistentHashMap.empty[A, B]
  override def + [B1 >: B] (kv: (A, B1)) = new PersistentHashMap[A, B1](wrappedAdd(kv))
  override def - (key: A) = new PersistentHashMap[A, B](wrappedRemove(key))
}
