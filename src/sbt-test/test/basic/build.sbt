autodocSettings

autodocVersion := System.getProperty("autodoc.version")

val scala211 = "2.11.7"

fullResolvers ~= {_.filterNot(_.name == "jcenter")}

scalaVersion := scala211

crossScalaVersions := scala211 :: "2.10.5" :: "2.12.0" :: Nil

autodocEnable := true
autodocToc := true
autodocHtml := true

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)
