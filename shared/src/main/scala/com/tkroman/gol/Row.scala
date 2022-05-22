package com.tkroman.gol

import scala.collection.immutable.BitSet

case class Row(cells: BitSet, w: Int):
  def iterator: Iterator[Boolean] = Iterator.tabulate(w)(cells)
  def isAlive(x: Int): Boolean = cells(x)

object Row:
  def make(w: Int): Row = Row(BitSet.empty, w)
