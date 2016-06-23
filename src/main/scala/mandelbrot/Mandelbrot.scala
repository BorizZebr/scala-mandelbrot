package mandelbrot

/**
  * Created by borisbondarenko on 22.06.16.
  */
object Mandelbrot {

  implicit val fj = new collection.parallel.ForkJoinTaskSupport(
    new scala.concurrent.forkjoin.ForkJoinPool(Runtime.getRuntime.availableProcessors))

  def compute(lo: Point, hi: Point, wdt: Int, hgt: Int, threshold: Int): Array[Int] = {

    val res = new Array[Int](wdt * hgt)
    val range = 0 until (wdt * hgt)
    for (idx <- range.par) {
      val x = idx % wdt
      val y = idx / wdt
      val xc = lo.x + (hi.x - lo.x) * x / wdt
      val yc = lo.y + (hi.y - lo.y) * y / hgt

      val iters = computePixel(xc, yc, threshold)

      res(idx) =
        if (iters < threshold) {
          val t = iters.toDouble / threshold.toDouble

          val r = (9*(1-t)*t*t*t*255).toInt
          val g = (15*(1-t)*(1-t)*t*t*255).toInt
          val b = (8.5*(1-t)*(1-t)*(1-t)*t*255).toInt
          255 << 24 | r << 16 | g << 8 | b
        }
        else 255 << 24 | 0 << 16 | 0 << 8 | 0
    }
    res
  }

  private def computePixel(xc: Double, yc: Double, threshold: Int): Int = {
    var i = 0
    var x = 0.0
    var y = 0.0
    while (x * x + y * y < 4 && i < threshold) {
      val xt = x * x - y * y + xc
      val yt = 2 * x * y + yc

      x = xt
      y = yt

      i += 1
    }
    i
  }
}

case class Point(x: Double, y: Double) {
  def magnitude : Double = x * x + y * y
}
