package com.gmail.kstanaszek.pipeline

import java.nio.file.Path

import org.apache.spark.sql.{DataFrame, Dataset, Encoder}

trait Pipeline[T] {
  def extract(filePath: Path): DataFrame

  def transform[T : Encoder](dataFrame: DataFrame): Dataset[T]

  def load[T : Encoder](dateset: Dataset[T], filePath: Path): Unit
}
