class Yoink(info: sbt.ProjectInfo) extends sbt.DefaultProject(info) with posterous.Publish {
  /**
   * Publish the source as well as the class files.
   */
  override def packageSrcJar= defaultJarPath("-sources.jar")
  val sourceArtifact = sbt.Artifact(artifactID, "src", "jar", Some("sources"), Nil, None)
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc)

  /**
   * Publish to a local temp repo, then rsync the files over to repo.codahale.com.
   */
  override def managedStyle = sbt.ManagedStyle.Maven
  val publishTo = sbt.Resolver.file("Local Cache", ("." / "target" / "repo").asFile)
  def publishToLocalRepoAction = super.publishAction
  override def publishAction = task {
    log.info("Uploading to repo.codahale.com")
    sbt.Process("rsync", "-avz" :: "target/repo/" :: "codahale.com:/home/codahale/repo.codahale.com" :: Nil) ! log
    None
  } describedAs("Publish binary and source JARs to repo.codahale.com") dependsOn(test, publishToLocalRepoAction)

  /**
   * Dependencies
   */
  val scalaSnapshots = "Scala Snapshots" at "http://scala-tools.org/repo-snapshots/"
  val clojure = "org.clojure" % "clojure" % "1.1.0" withSources()  
  val scalaTest = buildScalaVersion match {
    case "2.8.0.Beta1" => "org.scalatest" % "scalatest" % "1.0.1-for-scala-2.8.0.Beta1-with-test-interfaces-0.3-SNAPSHOT" % "test" withSources() intransitive()
    case "2.8.0.RC2" => "org.scalatest" % "scalatest" % "1.2-for-scala-2.8.0.RC2-SNAPSHOT" % "test" withSources() intransitive()
    case unknown => error("no known scalatest impl for %s".format(unknown))
  }
}
