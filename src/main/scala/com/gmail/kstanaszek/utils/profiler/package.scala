package com.gmail.kstanaszek.utils.profiler

import org.apache.spark.sql._
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.StructType

object DataFrameUtils {

  implicit class DataFrameImprovements(df: org.apache.spark.sql.DataFrame) {
    def profile: DataFrame = {
      DataFrameProfile(df).toDataFrame
    }

  }

  def flattenSchema(schema: StructType, prefix: String = null): Array[Column] = {
    schema.fields.flatMap(f => {
      val colName = if (prefix == null) f.name else (prefix + "." + f.name)

      f.dataType match {
        case st: StructType => flattenSchema(st, colName)
        case _ => Array(col(colName))
      }
    })
  }


}

