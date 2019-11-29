package com.gmail.kstanaszek.utils.profiler

import org.apache.log4j.{Level, Logger}

trait Logger {
  @transient lazy val logger = Logger.getLogger("org").setLevel(Level.ERROR)
}
