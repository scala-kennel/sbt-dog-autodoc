package dog
package autodoc

private[autodoc] object Html {

  val tocFileName = "toc.html"

  def generateTocBody(path: String, contents: Seq[String]): String = {
    val lists = contents
      .map(c =>  s"      <li>${c.replaceAll("</?h2>", "")}</li>")
      .mkString("\n")
      s"""  <li><a href ="$path">$path</a>
    <ul>
$lists
    </ul>
  </li>"""
  }

  def appendTemplate(contents: String): String = s"""<html>
<head>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.7/styles/default.min.css">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.7/highlight.min.js"></script>
</head>
<body>

$contents

<script>hljs.initHighlightingOnLoad();</script>
</body>
</html>"""
}
