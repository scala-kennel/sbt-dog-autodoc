# sbt-dog-autodoc

[![Build Status](https://travis-ci.org/pocketberserker/sbt-dog-autodoc.svg?branch=master)](https://travis-ci.org/pocketberserker/sbt-dog-autodoc)

sbt plugin for [dog-autodoc](https://github.com/pocketberserker/dog-autodoc)

## latest stable version

`project/plugins.sbt`

```scala
addSbtPlugin("com.github.pocketberserker" % "sbt-dog-autodoc" % "0.0.1")
```

`build.sbt`

```scala
autodocSettings

autodocEnable := true

autodocVersion := "0.0.1"
```

