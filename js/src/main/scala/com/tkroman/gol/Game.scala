package com.tkroman.gol

import cats.effect.ExitCode
import com.tkroman.gol.*
import monix.eval.*
import monix.reactive.*
import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D
import scala.concurrent.duration.*
import scala.util.Right
import scala.util.chaining.*

object Game extends TaskApp:
  private def setupCanvas(canvas: dom.html.Canvas): dom.CanvasRenderingContext2D =
    canvas.height = dom.document.documentElement.clientWidth.min(dom.document.documentElement.clientHeight)
    canvas.width = dom.document.documentElement.clientWidth.min(dom.document.documentElement.clientHeight)
    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    ctx

  private def draw(h: Int): Grid => Task[Unit] = grid => Task {
    val canvas = dom.document.getElementById("grid").asInstanceOf[dom.html.Canvas]
    val ctx = setupCanvas(canvas)
    val pxDim = canvas.height.toDouble / h
    ctx.fillStyle = "lightskyblue"
    ctx.fillRect(0, 0, canvas.height, canvas.width)
    dom.window.requestAnimationFrame { _ =>
      grid.iterator.zipWithIndex.foreach { case (r, y) =>
        r.iterator.zipWithIndex.foreach { case (alive, x) =>
          ctx.fillStyle = if alive then "yellow" else "lightskyblue"
          ctx.fillRect(x * pxDim, y * pxDim, pxDim, pxDim)
        }
      }
    }
  }

  override def run(args: List[String]): Task[ExitCode] =
    val args = dom.document.location.search.stripPrefix("?")
      .split('&')
      .filterNot(_.isBlank)
      .map(_.split('=').pipe(xs => xs(0) -> xs(1)))
      .toMap
    val w = args.getOrElse("w", "21").toInt
    val h = args.getOrElse("h", "21").toInt
    val fps = args.getOrElse("fps", "8").toInt
    val shape = ShapeReader.fromRle(args.getOrElse("shape", "bo$2bo$3o!"))
    Task {
      val canvas = dom.document.getElementById("grid").asInstanceOf[dom.html.Canvas]
      setupCanvas(canvas)//.scale(dom.window.devicePixelRatio, dom.window.devicePixelRatio)
      dom.window.onresize = { _ => setupCanvas(canvas) }
    } *> new Life(w, h)
      .launchWithRendering(shape)(fps, 1024)(draw(h))
      .timeoutTo(1.minute, Task.unit)
      *> Task(ExitCode.Success)

