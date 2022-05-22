package com.tkroman.gol

import scala.collection.mutable
import scala.util.chaining.*

// https://conwaylife.com/wiki/Run_Length_Encoded
object ShapeReader:
  private val rleRegex = """\d+[bo$]""".r

  def fromRle(rle: String): Shape =
    val points = rleRegex
      .replaceAllIn(
        rle.stripSuffix("!"), m => {
          val char = m.matched.last
          val reps = m.matched.takeWhile(_.isDigit).toInt
          // escape for replacement
          (if char == '$' then """\$""" else s"$char") * reps
        }
      )
      .split('$')
      .zipWithIndex.flatMap { case (r, y) =>
        r.zipWithIndex.collect { case ('o', x) => (x, y) }
      }
      .toSet

    val pw = points.map(_._1).max
    val ph = points.map(_._2).max
    (w: Int, h: Int) => {
      val cx = (w - pw) / 2
      val cy = (h - ph) / 2
      points.map { case (x, y) => (x + cx, y + cy) }
    }