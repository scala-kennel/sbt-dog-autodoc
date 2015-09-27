addSbtPlugin("com.github.pocketberserker" % "sbt-dog-autodoc" % System.getProperty("plugin.version"))

fullResolvers ~= {_.filterNot(_.name == "jcenter")}
