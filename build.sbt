name := "spark-skeleton"
organization := "com.gmail.kstanaszek"

version := "0.1"

scalaVersion := "2.11.12"


val sparkVersion = "2.4.4"
val sparkXMLVersion = "0.4.1"
val sparkMongoConnectorVersion = "2.4.1"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "com.databricks" %% "spark-xml" % sparkXMLVersion,
  "org.mongodb.spark" %% "mongo-spark-connector" % sparkMongoConnectorVersion,
  "com.databricks" %% "spark-csv" % "1.3.0",

 // testing
  "org.scalatest" %% "scalatest" % "3.2.0-SNAP10" % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.2" % Test
)

// ScalaTest settings.
// Ignore tests tagged as @Slow (they should be picked only by integration test)
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-l",
  "org.scalatest.tags.Slow", "-u","target/junit-xml-reports", "-oD", "-eS")


//////////
///// Databricks Settings
//////////

// Your username to login to Databricks
dbcUsername := sys.env("DATABRICKSUSERNAME")

// Your password (Can be set as an environment variable)
dbcPassword := sys.env("DATABRICKSPASSWORD")
// Gotcha: Setting environment variables in IDE's may differ.
// IDE's usually don't pick up environment variables from .bash_profile or .bashrc

// The URL to the Databricks REST API
dbcApiUrl := "https://your-sub-domain.cloud.databricks.com/api/1.2"

// Add any clusters that you would like to deploy your work to. e.g. "My Cluster"
dbcClusters += "my-cluster"  // Add "ALL_CLUSTERS" if you want to attach your work to all clusters

// An optional parameter to set the location to upload your libraries to in the workspace
// e.g. "/Shared/libraries"
// This location must be an existing path and all folders must exist.
// NOTE: Specifying this parameter is *strongly* recommended as many jars will be uploaded to your cluster.
// Putting them in one folder will make it easy for your to delete all the libraries at once.
// Default is "/"
dbcLibraryPath := "/Shared/Libraries"

// Whether to restart the clusters everytime a new version is uploaded to Databricks.
dbcRestartOnAttach := false // Default true

//////////
///// END Databricks Settings
//////////
