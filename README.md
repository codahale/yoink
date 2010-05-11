Yoink
=====

*Why should Lispers have all the fun?*

Yoink is a very small Scala library which provides Scala wrappers for Clojure's
awesome persistent map classes.


Requirements
------------

* Java SE 6
* Scala 2.8 Beta1


How To Use
----------

**First**, specify Yoink as a dependency:

    val codaRepo = "Coda Hale's Repository" at "http://repo.codahale.com/"
    val yoink = "com.codahale" %% "yoink" % "1.1.0" withSources()

**Second**, use some persistent, immutable maps:
    
    import com.codahale.yoink._
    
    val tree = PersistentTreeMap("one" -> 1, "two" -> 2)
    tree("one")
    tree - "one"


A Huge Shoutout
---------------

The new collections framework in Scala 2.8 is *awesome*. Martin Odersky and
Adriaan Moore's [Fighting Bit Rot with Types](http://lampwww.epfl.ch/~odersky/papers/fsttcs2009.pdf)
is an excellent description of how the new collections are structured and how
easy it is to add new collection implementations.


TODO
----

* yoink Clojure's sets
* yoink Clojure's list
* yoink Clojure's vector


License
-------

Copyright (c) 2010 Coda Hale
Published under The MIT License, see LICENSE