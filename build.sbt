name := "SlickApplication"
version := "0.1"
scalaVersion := "2.13.5"

idePackagePrefix := Some("com.haile.app")

// Assembly configuration
mainClass in assembly       := Some(s"${idePackagePrefix.key}.Boot")
// TODO add runnable JAR directory and create it if not exist
assemblyJarName in assembly := ""

libraryDependencies ++= {
  val akkaVersion     = "2.6.8"
  val akkaHttpVersion = "10.2.4"
  val slickVersion    = "3.3.3"

  Seq(
    // Akka basic environment TODO Isn't in use for now but hope it will be
    "com.typesafe.akka"           %% "akka-actor-typed"     % akkaVersion,
    "com.typesafe.akka"           %% "akka-stream"          % akkaVersion,
    "com.typesafe.akka"           %% "akka-http"            % akkaHttpVersion,

    // Configuration lib TODO Isn't in use for now and probably will be deleted soon
    "com.typesafe"                %  "config"               % "1.4.1",

    // Logger
    "com.typesafe.scala-logging"  %% "scala-logging"        % "3.9.3",
    "ch.qos.logback"              %  "logback-classic"      % "1.2.3",

    // Scala test environment
    "org.scalatest"               %% "scalatest"            % "3.2.5"           % "test",

    // Slick basic environment, no slf4j cause it conflicts with scala-logging
    "com.typesafe.slick"          %  "slick_2.13"           % slickVersion,
    "com.typesafe.slick"          %  "slick-hikaricp_2.13"  % slickVersion,

    // H2 database for simplicity
    "com.h2database"              %  "h2"                   % "1.4.200"
  )
}
