lazy val life = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .jvmSettings(
    libraryDependencies += "org.fusesource.jansi" % "jansi" % "2.4.0"
  )
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.2.0",
    scalaJSUseMainModuleInitializer := true,
  )
  .settings(
    name := "life",
    version := "0.1.0",
    scalaVersion := "3.1.2",
    organization := "com.tkroman",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test,
    libraryDependencies += "io.monix" %%% "monix-reactive" % "3.4.1",
  )
