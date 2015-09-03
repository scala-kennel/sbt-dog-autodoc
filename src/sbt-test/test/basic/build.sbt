autodocSettings

autodocVersion := System.getProperty("autodoc.version")

val scala211 = "2.11.7"

scalaVersion := scala211

crossScalaVersions := scala211 :: "2.10.5" :: Nil

testListeners += new dog.autodoc.AutodocListener(file("doc"))

scalacOptions ++= (
  "-deprecation" ::
  "-unchecked" ::
  "-Xlint" ::
  "-language:existentials" ::
  "-language:higherKinds" ::
  "-language:implicitConversions" ::
  Nil
)
