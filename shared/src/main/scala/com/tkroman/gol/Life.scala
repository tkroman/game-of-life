package com.tkroman.gol

import com.tkroman.gol.range
import monix.eval.Task
import monix.reactive.Observable
import scala.annotation.{tailrec, targetName}
import scala.collection.BitSet
import scala.concurrent.duration.*

class Life(w: Int, h: Int, shape: Shape):
  def launchPure(): Observable[Grid] =
    Observable.fromIterator(
      Task(
        Iterator
          .unfold(Grid.make(w, h).use(shape.get(w, h)))(_.next.map(g => (g, g)))
          .iterator
      )
    )

  def launchWithRendering(fps: Int, maxGeneraions: Long)
                         (draw: Grid => Task[Unit]): Task[Unit] =
    val delay = (1.0 / fps).seconds
    Task
      .unit
      .flatMapLoop(Grid.make(w, h).use(shape.get(w, h)) -> 0L) { case (_, (grid, gen), cont) =>
        draw(grid) *> Task.defer {
          grid.next match {
            case Some(ng) if gen < maxGeneraions => Task.sleep(delay) *> cont((ng, gen + 1L))
            case _ => Task.now((grid, gen + 1L))
          }
        }
      } *> Task.unit

