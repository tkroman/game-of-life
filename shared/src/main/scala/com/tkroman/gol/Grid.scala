package com.tkroman.gol

import scala.collection.immutable.BitSet

class Grid(
  private val rows: Array[Row],
  private val w: Int,
  private val h: Int
):
  private val wRange = w.range
  private val hRange = h.range
  private def isAlive(x: Int, y: Int): Boolean = rows(y).isAlive(x)

  def width: Int = w
  def height: Int = h

  private def aliveNeighbourCount(x: Int, y: Int): Int = {
    (-1 to 1).cross(-1 to 1)
      .filterNot(_ == (0, 0))
      .filter { case (dx, dy) => wRange.contains(x + dx) && hRange.contains(y + dy) }
      .count { case (dx, dy) => isAlive(x + dx, y + dy) }
  }

  def next: Option[Grid] =
    val nextRows = Array.fill(h)(scala.collection.mutable.BitSet())
    for
      y <- h.range
      x <- w.range
    do
      val imAlive = isAlive(x, y)
      val aliveNeighbours = aliveNeighbourCount(x, y)
      val iSurvive = imAlive && (aliveNeighbours == 2 || aliveNeighbours == 3)
      val iRise = !imAlive && (aliveNeighbours == 3)
      if iSurvive || iRise then
        nextRows(y).add(x)
    val readyNextRows = nextRows.map(r => Row(r.toImmutable, w))
    Option.when(!readyNextRows.sameElements(rows))(new Grid(readyNextRows, w, h))

  def iterator: Iterator[Row] = rows.iterator

  def use(liveCoordinates: Set[(Int, Int)]): Grid =
    new Grid(
      Array.tabulate(h) { y =>
        val bs = BitSet.newBuilder
        w.range.foreach { x =>
          if liveCoordinates(x, y) then bs += x
        }
        Row(bs.result(), w)
      },
      w,
      h,
    )


object Grid:
  def make(w: Int, h: Int): Grid = Grid(Array.fill(h)(Row.make(w)), w, h)
