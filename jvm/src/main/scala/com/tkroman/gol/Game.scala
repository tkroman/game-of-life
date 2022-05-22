package com.tkroman.gol

import cats.effect.ExitCode
import com.tkroman.gol.*
import monix.eval.Task
import monix.execution.schedulers.CanBlock
import org.fusesource.jansi.*
import scala.concurrent.duration.*

object Game extends monix.eval.TaskApp {
  override def run(args: List[String]): Task[ExitCode] = {
    val w = args.lift(0).map(_.toInt).getOrElse(21)
    val h = args.lift(1).map(_.toInt).getOrElse(21)
    val fps = args.lift(2).map(_.toInt).getOrElse(8)
    val shape = args.lift(3).map(ShapeReader.fromRle).getOrElse(Shape.Blinker)
    AnsiConsole.systemInstall()
    Ansi.ansi().eraseScreen(Ansi.Erase.ALL)
    val draw: Grid => Task[Unit] = grid => Task {
      print(Ansi.ansi().cursor(0, 0))
      print(Ansi.ansi().eraseScreen())
      println(
        grid.iterator.foldLeft(new StringBuilder()) { case (sb, r) =>
          r.iterator.foldLeft(sb) { case (sb, alive) =>
            sb.append(if alive then "⬛️️️" else "⬜️")
          }
          sb.append('\n')
        }
      )
    }
    new Life(w, h).launchWithRendering(shape)(fps, 1024)(draw) *> Task(ExitCode.Success)

  }
}

