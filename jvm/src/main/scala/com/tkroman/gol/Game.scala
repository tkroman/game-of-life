package com.tkroman.gol

import cats.effect.ExitCode
import com.tkroman.gol.*
import monix.eval.Task
import monix.execution.schedulers.CanBlock
import org.fusesource.jansi.*
import scala.concurrent.duration.*

object Game extends monix.eval.TaskApp {
  override def run(args: List[String]): Task[ExitCode] =
    for
      argMap  <- Task(args.zipWithIndex.map(_.swap).toMap)
      w       <- Task(argMap.getOrElse(0, "21").toInt)
      h       <- Task(argMap.getOrElse(1, "21").toInt)
      fps     <- Task(argMap.getOrElse(2, "8").toInt)
      shape   <- Task(Shape.fromRle(argMap.getOrElse(3, "bo$2bo$3o!")))
      _       <- Task(AnsiConsole.systemInstall())
      _       <- Task(Ansi.ansi().eraseScreen(Ansi.Erase.ALL))
      game    = new Life(w, h, shape)
      _       <- game
        .launchWithRendering(fps, 1024)(draw(w))
        .timeoutTo(1.minute, Task.unit)
    yield
      ExitCode.Success

  private def draw(w: Int): Grid => Task[Unit] = grid => Task {
    val mw = w - 1
    print(Ansi.ansi().cursor(0, 0))
    print(Ansi.ansi().eraseScreen())
    println(
      grid.crossIterator.foldLeft(new StringBuilder()) { case (sb, (x, y, alive)) =>
        sb.append(if alive then "⬛️️️" else "⬜️")
        if x == mw then sb.append('\n') else sb
      }
    )
  }
}

