package com.tkroman.gol

import cats.effect.ExitCode
import com.tkroman.gol.*
import monix.eval.*
import monix.reactive.*
import org.scalajs.dom
import scala.concurrent.duration.*
import scala.util.Right
import scala.util.chaining.*

object Game extends TaskApp:
  private def draw(w: Int): Grid => Task[Unit] = grid => Task {
    val container = dom.document.getElementById("container")
    val newGrid = dom.document.createElement("div").asInstanceOf[dom.html.Div].tap { g =>
      g.classList.add("grid")
      g.style = s"grid-template-columns: repeat($w, ${100.0 / w}%)"
    }
    grid.iterator.foreach { r =>
      r.iterator.foreach { alive =>
        dom.document.createElement("div").asInstanceOf[dom.html.Div].tap { e =>
          e.classList.add("cell")
          e.classList.add(if alive then "black" else "white")
          newGrid.appendChild(e)
        }
      }
    }
    if (container.hasChildNodes()) {
      container.replaceChild(newGrid, container.firstChild)
    } else {
      container.appendChild(newGrid)
    }
  }

  override def run(args: List[String]): Task[ExitCode] =
    val args = dom.document.location.search.stripPrefix("?")
      .split('&')
      .map(_.split('=').pipe(xs => xs(0) -> xs(1)))
      .toMap
    val w = args.getOrElse("w", "21").toInt
    val h = args.getOrElse("h", "21").toInt
    val fps = args.getOrElse("fps", "8").toInt
    val shape = ShapeReader.fromRle(args.getOrElse("shape", "bo$2bo$3o!"))

    new Life(w, h)
      .launchWithRendering(shape)(fps, 1024)(draw(w))
      .timeoutTo(1.minute, Task.unit)
      *> Task(ExitCode.Success)
