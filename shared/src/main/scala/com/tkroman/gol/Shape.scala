package com.tkroman.gol

trait Shape:
  def get(w: Int, h: Int): Set[(Int, Int)]

object Shape:
  val Predefined: Map[String, Shape] = Map(
    "glider" -> Glider,
    "blinker" -> Blinker,
  )
  
  def fromRle(rle: String): Shape =
    RleDecoder(rle).shape()

  val Glider: Shape = new Shape:
    override def get(w: Int, h: Int): Set[(Int, Int)] =
      val (cx, cy) = (w/2, h/2)
      Set(
        (cx - 1, cy),
        (cx, cy + 1),
        (cx + 1, cy - 1),
        (cx + 1, cy),
        (cx + 1, cy + 1),
      )

  val Blinker: Shape = new Shape:
    override def get(w: Int, h: Int): Set[(Int, Int)] =
      val (cx, cy) = (w/2, h/2)
      Set(
        (cx - 1, cy),
        (cx, cy),
        (cx + 1, cy),
      )
