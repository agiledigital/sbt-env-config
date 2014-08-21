package au.com.agiledigital

import com.typesafe.config._
import java.io._
import sbt._
import Keys._

object EnvConfigPlugin extends AutoPlugin {
  import EnvConfigSettings._

  object EnvConfigSettings {
    val source        = settingKey[String]("Source .conf file.")
    val dest          = settingKey[String]("Destination .conf file.")
    val path          = settingKey[String]("Path in resolved .conf to write")
    val envConfigTask = TaskKey[Unit]("envConfigTask")
  }

  override lazy val projectSettings = Seq (
    // Default settings.
    source := "environment-dev.conf",
    dest   := "application-environment.conf",
    path   := "",
    envConfigTask <<= (source, dest, path) map configTask
  )

  def configTask(source: String, dest: String, path: String) = {
    // Source file.
    val sourceConf = new File(source)
    if (!sourceConf.exists() || !sourceConf.isFile) {
      System.err.println(s"$source does not exist or is not a file")
    } else {
      // Parse and resolve source file.
      val config = ConfigFactory.parseFile(sourceConf).resolve
      // Output the full configuration, or a subset depending on the path.
      val trimmedConfig = path match {
        case "" => config
        case trimmedPath if (config.hasPath(trimmedPath)) => config.getConfig(trimmedPath)
        // Default case.
        case trimmedPath => {
          System.err.println(s"File [${source}] has no path [${trimmedPath}]")
          ConfigFactory.empty();
        }
      }
      // Write the specified path of the resolved .conf to the destination.
      val writer = new PrintWriter(new File(dest))
      writer.write(trimmedConfig.root.render)
      writer.close
    }
  }
}
