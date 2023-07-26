ThisBuild / tlBaseVersion       := "0.75"
ThisBuild / tlCiReleaseBranches := Seq("master")

lazy val reactJS = "17.0.2"

lazy val catsVersion              = "2.9.0"
lazy val catsRetryVersion         = "3.1.0"
lazy val circeVersion             = "0.14.5"
lazy val crystalVersion           = "0.34.1"
lazy val kittensVersion           = "3.0.0"
lazy val http4sVersion            = "0.23.23"
lazy val http4sDomVersion         = "0.2.9"
lazy val lucumaBCVersion          = "0.4.0"
lazy val lucumaCoreVersion        = "0.80.2"
lazy val lucumaPrimeStylesVersion = "0.2.9"
lazy val lucumaReactVersion       = "0.38.0"
lazy val lucumaRefinedVersion     = "0.1.2"
lazy val lucumaSsoVersion         = "0.6.1"
lazy val monocleVersion           = "3.2.0"
lazy val mouseVersion             = "1.1.0"
lazy val pprintVersion            = "0.8.1"
lazy val scalaJsReactVersion      = "2.1.1"

ThisBuild / resolvers ++= Resolver.sonatypeOssRepos("snapshots")

Global / onChangedBuildSource                                        := ReloadOnSourceChanges
ThisBuild / ScalafixConfig / bspEnabled.withRank(KeyRanks.Invisible) := false

addCommandAlias(
  "fixImports",
  "; scalafix OrganizeImports; Test/scalafix OrganizeImports"
)

ThisBuild / turbo                    := true
ThisBuild / Test / parallelExecution := false
ThisBuild / scalaVersion             := "3.3.0"
ThisBuild / crossScalaVersions       := Seq("3.3.0")
ThisBuild / scalacOptions ++= Seq("-language:implicitConversions")

enablePlugins(NoPublishPlugin)

addCommandAlias(
  "restartWDS",
  "; demo/fastOptJS/stopWebpackDevServer; demo/fastOptJS/startWebpackDevServer; ~demo/fastOptJS"
)

addCommandAlias(
  "stopWDS",
  "; demo/fastOptJS/stopWebpackDevServer"
)

lazy val demo =
  project
    .in(file("modules/demo"))
    .enablePlugins(ScalaJSBundlerPlugin, NoPublishPlugin)
    .settings(
      webpack / version                     := "4.44.1",
      startWebpackDevServer / version       := "3.11.0",
      fastOptJS / webpackConfigFile         := Some(
        baseDirectory.value / "webpack" / "dev.webpack.config.js"
      ),
      fullOptJS / webpackConfigFile         := Some(
        baseDirectory.value / "webpack" / "prod.webpack.config.js"
      ),
      webpackMonitoredDirectories += (Compile / resourceDirectory).value,
      webpackResources                      := (baseDirectory.value / "webpack") * "*.js",
      webpackDevServerPort                  := 7800,
      Compile / npmDependencies ++= Seq(
        "react"     -> reactJS,
        "react-dom" -> reactJS
      ),
      webpackMonitoredFiles / includeFilter := "*",
      useYarn                               := true,
      fastOptJS / webpackBundlingMode       := BundlingMode.LibraryOnly(),
      fullOptJS / webpackBundlingMode       := BundlingMode.Application,
      Compile / fastOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      Compile / fullOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      test                                  := {},
      Compile / doc / sources               := Seq.empty,
      libraryDependencies ++= List(
        "com.github.japgolly.scalajs-react" %%% "callback-ext-cats" % scalaJsReactVersion,
        "com.rpiaggio"                      %%% "log4cats-loglevel" % "0.3.1"
      )
    )
    .dependsOn(ui)

lazy val ui =
  project
    .in(file("modules/ui"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name := "lucuma-ui",
      libraryDependencies ++= Seq(
        "org.typelevel"                     %%% "cats-core"                    % catsVersion,
        "org.typelevel"                     %%% "kittens"                      % kittensVersion,
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cb_io"            % scalaJsReactVersion,
        "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3"           % scalaJsReactVersion,
        "edu.gemini"                        %%% "lucuma-core"                  % lucumaCoreVersion,
        "edu.gemini"                        %%% "lucuma-react-common"          % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-clipboard"       % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-font-awesome"    % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-resize-detector" % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-tanstack-table"  % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-floatingui"      % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-prime-react"     % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-prime-styles"          % lucumaPrimeStylesVersion,
        "dev.optics"                        %%% "monocle-core"                 % monocleVersion,
        "dev.optics"                        %%% "monocle-macro"                % monocleVersion,
        "edu.gemini"                        %%% "crystal"                      % crystalVersion,
        "com.lihaoyi"                       %%% "pprint"                       % pprintVersion,
        "org.http4s"                        %%% "http4s-core"                  % http4sVersion,
        "org.http4s"                        %%% "http4s-circe"                 % http4sVersion,
        "org.http4s"                        %%% "http4s-dom"                   % http4sDomVersion,
        "com.github.cb372"                  %%% "cats-retry"                   % catsRetryVersion,
        "io.circe"                          %%% "circe-core"                   % circeVersion,
        "io.circe"                          %%% "circe-parser"                 % circeVersion,
        "edu.gemini"                        %%% "lucuma-sso-frontend-client"   % lucumaSsoVersion,
        "edu.gemini"                        %%% "lucuma-broadcast-channel"     % lucumaBCVersion
      )
    )

lazy val testkit =
  project
    .in(file("modules/testkit"))
    .dependsOn(ui)
    .enablePlugins(ScalaJSPlugin)
    .settings(
      name := "lucuma-ui-testkit",
      libraryDependencies ++= Seq(
        "edu.gemini" %%% "lucuma-core-testkit" % lucumaCoreVersion
      )
    )

lazy val tests =
  project
    .in(file("modules/tests"))
    .dependsOn(testkit)
    .settings(
      libraryDependencies ++= Seq(
        "edu.gemini"    %%% "lucuma-core-testkit" % lucumaCoreVersion % Test,
        "org.scalameta" %%% "munit"               % "0.7.29"          % Test,
        "org.typelevel" %%% "discipline-munit"    % "1.0.9"           % Test
      )
    )
    .enablePlugins(ScalaJSPlugin, NoPublishPlugin)

// for publishing CSS to npm
lazy val npmPublish = taskKey[Unit]("Run npm publish")

lazy val css = project
  .in(file("modules/css"))
  .dependsOn(ui)
  .enablePlugins(LucumaCssPlugin, NoPublishPlugin)
  .settings(
    npmPublish := {
      import scala.sys.process._
      val _      = (Compile / lucumaCss).value
      val cssDir = target.value / "lucuma-css"
      IO.write(
        cssDir / "package.json",
        s"""|{
            |  "name": "lucuma-ui-css",
            |  "version": "${version.value}",
            |  "license": "${licenses.value.head._1}"
            |}
            |""".stripMargin
      )
      Process(List("npm", "publish"), cssDir).!!
    }
  )

ThisBuild / githubWorkflowPublishPreamble +=
  WorkflowStep.Use(
    UseRef.Public("actions", "setup-node", "v3"),
    Map(
      "node-version" -> "18",
      "registry-url" -> "https://registry.npmjs.org"
    )
  )

ThisBuild / githubWorkflowPublish ++= Seq(
  WorkflowStep.Sbt(
    List("css/npmPublish"),
    name = Some("NPM Publish"),
    env = Map("NODE_AUTH_TOKEN" -> s"$${{ secrets.NPM_REPO_TOKEN }}")
  )
)
