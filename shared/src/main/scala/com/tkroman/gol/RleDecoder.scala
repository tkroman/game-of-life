package com.tkroman.gol

import scala.collection.mutable
import scala.util.chaining.*

// https://conwaylife.com/wiki/Run_Length_Encoded
object RleDecoder:
  private val rleRegex = """\d+[bo$]""".r

class RleDecoder(rle: String):
  def shape(): Shape =
    val points = RleDecoder
      .rleRegex
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

    val (pw, ph) = points.reduce((x, y) => (x._1.max(y._1), x._2.max(y._2)))
    new Shape:
      override def get(w: Int, h: Int): Set[(Int, Int)] =
        val cx = (w - pw) / 2
        val cy = (h - ph) / 2
        points.map { case (x, y) => (x + cx, y + cy) }

      override def toString: String = rle
      