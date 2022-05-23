package com.tkroman.gol

import cats.effect.ExitCode
import com.tkroman.gol.*
import monix.eval.*
import monix.reactive.*
import org.scalajs.dom
import org.scalajs.dom.CanvasRenderingContext2D
import org.scalajs.dom.html.Canvas
import scala.concurrent.duration.*
import scala.util.Right
import scala.util.chaining.*

object Game extends TaskApp:
  private val bg = "lightskyblue"
  private val fg = "yellow"

  override def run(args: List[String]): Task[ExitCode] =
    for
      args  <- parseArgs
      w     <- Task(args.getOrElse("w", "21").toInt)
      h     <- Task(args.getOrElse("h", "21").toInt)
      fps   <- Task(args.getOrElse("fps", "8").toInt)
      shape <- Task(Shape.fromRle(args.getOrElse("shape", "bo$2bo$3o!")))
      _     <- Task(dom.window.onresize = _ => canvas.fitDimensions)
      _     <- fitCanvas
      game  = new Life(w, h, shape)
      _     <- game
        .launchWithRendering(fps, 1024)(draw(h))
        .timeoutTo(1.minute, Task.unit)
    yield
      ExitCode.Success

  private def draw(h: Int): Grid => Task[Unit] = grid => Task {
    val cv = canvas.fitDimensions
    val pxDim = cv.height.toDouble / h
    val ctx = cv.ctx2d
    ctx.fillStyle = bg
    ctx.fillRect(0, 0, cv.height, cv.width)
    dom.window.requestAnimationFrame { _ =>
      grid.crossIterator.foreach { case (x, y, alive) =>
        ctx.fillStyle = alive.color
        ctx.fillRect(x * pxDim, y * pxDim, pxDim, pxDim)
      }
    }
  }

  private def canvas: Canvas =
    dom.document.getElementById("grid").asInstanceOf[Canvas]

  extension (b: Boolean)
    private def color: String = if b then fg else bg

  extension (c: Canvas)
    private def ctx2d: CanvasRenderingContext2D =
      c.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
    private def fitDimensions: Canvas =
      val minDim = math.min(
        dom.document.documentElement.clientHeight,
        dom.document.documentElement.clientWidth,
      )
      c.height = minDim
      c.width = minDim
      c

  private val fitCanvas: Task[Unit] = Task {
    canvas
      .fitDimensions
      .ctx2d
      .scale(dom.window.devicePixelRatio, dom.window.devicePixelRatio)
  }

  private val parseArgs: Task[Map[String, String]] = Task {
    dom.document.location.search.stripPrefix("?")
      .split('&')
      .filterNot(_.isBlank)
      .map(_.split('=').pipe(xs => xs(0) -> xs(1)))
      .toMap
  }
