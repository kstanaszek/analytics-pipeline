package com.gmail.kstanaszek.utils.io

import java.io.{InputStream, OutputStream}
import java.util.zip.GZIPInputStream

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream

object IOUtils {

  /**
   * Copy all bytes from input to output. Don't close any stream.
   */
  def copy(in: InputStream, out: OutputStream): Unit = {
    val buf = new Array[Byte](1 << 20) // 1 MB
    while (true) {
      val read = in.read(buf)
      if (read == -1) {
        out.flush
        return
      }
      out.write(buf, 0, read)
    }
  }

  val unzippers = Map[String, InputStream => InputStream] (
    "gz" -> { new GZIPInputStream(_) },
    "bz2" -> { new BZip2CompressorInputStream(_, true) }
  )

}
