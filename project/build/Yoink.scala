class Yoink(info: sbt.ProjectInfo) extends sbt.DefaultProject(info)
        with posterous.Publish with rsync.RsyncPublishing {
  /**
   * Publish the source as well as the class files.
   */
  override def packageSrcJar= defaultJarPath("-sources.jar")
  val sourceArtifact = sbt.Artifact(artifactID, "src", "jar", Some("sources"), Nil, None)
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc)

  /**
   * Publish via rsync.
   */
  def rsyncRepo = "codahale.com:/home/codahale/repo.codahale.com"

  /**
   * Dependencies
   */
  val scalaSnapshots = "Scala Snapshots" at "http://scala-tools.org/repo-snapshots/"
  val clojure = "org.clojure" % "clojure" % "1.1.0" withSources()  
  val scalaTest = "org.scalatest" % "scalatest" % "1.2" % "test" withSources() intransitive()

    val fastJVMOptions: Seq[String] = List(
    "-server", "-d64",                     // make sure we're using the 64-bit server VM
    "-XX:+UseNUMA",                        // make sure we use NUMA-specific GCs if possible
    "-XX:+UseCompressedOops",              // use 32-bit pointers to reduce heap usage
    "-XX:+UseParallelOldGC",               // use parallel GCs for both new and old generations
    "-XX:SurvivorRatio=8",                 // increase size of survivor space (keep objects in young gen longer)
    "-XX:TargetSurvivorRatio=90",          // allow 90% of survivor spaces to be used
    "-XX:MaxTenuringThreshold=15",         // take longer to tenure objects
    "-XX:+AggressiveOpts",                 // use the latest and greatest in JVM tech
    "-XX:+UseFastAccessorMethods",         // be sure to inline simple accessor methods
    "-XX:+UseBiasedLocking",               // speed up uncontended locks
    "-Xss128k",                            // reduce the thread stack size, freeing up space for the heap
    "-Xms500M",                            // set the min heap size to the max allowed by the system
    "-Xmx500M",                            // same with the max heap size
    "-Xmn300M"                             // increase the size of the young generation
  )

  override def fork = {
    val si = buildScalaInstance
    Some(new sbt.ForkScalaRun {
      override def scalaJars = si.libraryJar :: si.compilerJar :: Nil
      override def runJVMOptions: Seq[String] = fastJVMOptions
    })
  }
}
