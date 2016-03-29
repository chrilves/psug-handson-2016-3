lazy val commonSettings = Seq(
  organization  := "psug-handson-2016-3",
  version       := "0.1-SNAPSHOT"  ,
  scalaVersion  := "2.11.8",
  scalacOptions ++= Seq(
    "-deprecation",
    "-explaintypes",
    "-language:_",
    "-Xlint:_"
  )
)

lazy val macros = project in file("macros") settings(commonSettings :+ (libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _)))

lazy val root = Project("core", file(".")) dependsOn (macros) settings(commonSettings ++ Seq(
  libraryDependencies += "com.lihaoyi" % "ammonite-repl" % "0.5.7" cross CrossVersion.full ,

initialCommands := """
ammonite.repl.Main.run("import handson._ ; import desugar._ ; import scala.reflect.runtime.universe._ ")
"""))