package com.tkroman.gol

extension (i: Int)
  def range = 0 until i

extension [A](as: Iterable[A])
  def cross[B](bs: Iterable[B]): Iterator[(A, B)] =
    for
      a <- as.iterator
      b <- bs.iterator
    yield
      (a, b)
