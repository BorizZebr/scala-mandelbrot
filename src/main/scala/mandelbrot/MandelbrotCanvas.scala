package mandelbrot

import java.awt.event.{MouseAdapter, MouseEvent, MouseMotionAdapter, MouseWheelEvent}
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.awt.{Graphics, Graphics2D, RenderingHints}
import javax.swing.JComponent

/**
  * Created by borisbondarenko on 22.06.16.
  */
class MandelbrotCanvas(frame: MandelbrotFrame) extends JComponent {

  def threshold = frame.threshold.getText.toInt
  def zoom = frame.zoomlevel.getValue.asInstanceOf[Int] / 10.0 * 500.0
  def resFactor = frame.resFactor.getText.toDouble

  var xoff = -0.9572428
  var yoff = -0.2956327
  var xlast = -1
  var ylast = -1
  def xlo = xoff - getWidth / zoom
  def ylo = yoff - getHeight / zoom
  def xhi = xoff + getWidth / zoom
  def yhi = yoff + getHeight / zoom

  addMouseMotionListener(new MouseMotionAdapter {
    override def mouseDragged(e: MouseEvent) {
      val xcurr = e.getX
      val ycurr = e.getY
      if (xlast != -1) {
        val xd = xcurr - xlast
        val yd = ycurr - ylast
        xoff -= xd / zoom
        yoff -= yd / zoom
      }
      xlast = xcurr
      ylast = ycurr
      repaint()
    }
  })

  var buffResFactor : String = "1"
  addMouseListener(new MouseAdapter {
    override def mousePressed(e: MouseEvent) {
      buffResFactor = resFactor.toString
      xlast = -1
      ylast = -1
      frame.resFactor.setText((resFactor / 4).toString)
    }

    override def mouseReleased(e: MouseEvent) {
      frame.resFactor.setText(buffResFactor)
      repaint()
    }
  })

  addMouseWheelListener(new MouseAdapter {
    override def mouseWheelMoved(e: MouseWheelEvent) {
      val prev = frame.zoomlevel.getValue.asInstanceOf[Int]
      val next = prev + (prev * -0.1 * e.getWheelRotation - e.getWheelRotation)
      frame.zoomlevel.setValue(math.max(1, next.toInt))
    }
  })

  var image: Option[BufferedImage] = None
  override def paintComponent(g: Graphics) {
    super.paintComponent(g)
    val start = System.nanoTime

    val scW = (getWidth * resFactor).toInt
    val scH = (getHeight * resFactor).toInt

    val pixels = Mandelbrot.compute(
      Point(xlo, ylo),
      Point(xhi, yhi),
      scW,
      scH,
      threshold)

    val end = System.nanoTime
    val time = (end - start) / 1000000.0
    val stats = s"size: $getWidth x $getHeight, time: $time ms, bounds=($xoff, $yoff)"
    frame.setTitle(s"Mandelbrot: $stats")

    val img = new BufferedImage(scW, scH, BufferedImage.TYPE_INT_ARGB)
    image = Some(img)
    for (x <- 0 until scW; y <- 0 until scH) {
      val color = pixels(y * scW + x)
      img.setRGB(x, y, color)
    }

    val g2 = g.asInstanceOf[Graphics2D]
      val xform = AffineTransform.getScaleInstance(1.0 / resFactor, 1.0 / resFactor)
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
      RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    g2.drawImage(img, xform, null)
  }
}

