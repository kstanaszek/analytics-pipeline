# Spark Skeleton 

This is a skeleton project for Scala and Spark.

## Prerequisites
On a Debian-based system
* Install Java 8 JDK
    ```
    sudo apt-get update && sudo apt-get install java-common
    sudo dpkg --install java-1.8.0-amazon-corretto-jdk_8.232.09-1_amd64.deb
    ```
* Install Apache Spark
    * Download binaries : 
        ```
        curl -0 https://www.apache.org/dyn/closer.lua/spark/spark-2.4.4/spark-2.4.4-bin-hadoop2.7.tgz
        tar xvf tar xvf spark-2.4.4-bin-hadoop2.7.tgz
        sudo mv spark-2.4.4-bin-hadoop2.7/ /opt/spark 
        ```
   * Set Spark environment
        * Open your bashrc configuration file, e.g. `nano ~/.bashrc`
        * Add following lines to the file:
            ```
            export SPARK_HOME=/opt/spark
            export PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin
            ```
    * Activate the changes `source ~/.bashrc`
