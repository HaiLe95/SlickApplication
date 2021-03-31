// Scala Linter
addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1-SNAPSHOT")

// Easy IDEA configs
addSbtPlugin("org.jetbrains" % "sbt-ide-settings" % "1.1.0")

// Scala Style-checker
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

// Scala easy-going deploy and build tool
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0")

// CompilerPlugin configurations
resolvers += Resolver.sonatypeRepo("snapshots")
