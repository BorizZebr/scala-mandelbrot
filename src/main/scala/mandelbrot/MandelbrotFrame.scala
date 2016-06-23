package mandelbrot

import java.awt.event.{ActionEvent, ActionListener}
import java.awt.{BorderLayout, GridLayout}
import java.io.File
import java.time.LocalDateTime
import javax.imageio.ImageIO
import javax.swing.event.{ChangeEvent, ChangeListener}
import javax.swing.{JSpinner, JTextField, _}

/**
  * Created by borisbondarenko on 22.06.16.
  */
class MandelbrotFrame extends JFrame("Mandelbrot") {

  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setSize(700, 450)
  setLayout(new BorderLayout)

  val canvas = new MandelbrotCanvas(this)
  add(canvas, BorderLayout.CENTER)

  val right = new JPanel
  right.setBorder(BorderFactory.createEtchedBorder(border.EtchedBorder.LOWERED))
  right.setLayout(new BorderLayout)

  val panel = new JPanel
  panel.setLayout(new GridLayout(0, 1))

  val controls = new JPanel
  controls.setLayout(new GridLayout(0, 2))

  controls.add(new JLabel("Zoom"))
  val zoomlevel = new JSpinner
  zoomlevel.setValue(20)
  zoomlevel.addChangeListener(new ChangeListener {
    def stateChanged(e: ChangeEvent) {
      canvas.repaint()
    }
  })
  controls.add(zoomlevel)

  controls.add(new JLabel("Threshold"))
  val threshold = new JTextField("512")
  threshold.addActionListener(new ActionListener {
    def actionPerformed(e: ActionEvent) {
      canvas.repaint()
    }
  })
  controls.add(threshold)

  controls.add(new JLabel("Resolution factor"))
  val resFactor = new JTextField("1")
  resFactor.addActionListener(new ActionListener {
    def actionPerformed(e: ActionEvent) {
      canvas.repaint()
    }
  })
  controls.add(resFactor)

  val takePictureButton = new JButton("Take a picture")
  takePictureButton.addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) {
      canvas.image.map { i =>
        val time = LocalDateTime.now
        ImageIO.write(
          i,
          "png",
          new File(s"mandelbrot_${time.getMinute}_${time.getSecond}_.png"))
      }
    }
  })
  controls.add(takePictureButton)

  panel.add(controls)

  right.add(panel, BorderLayout.NORTH)
  add(right, BorderLayout.EAST)
  setVisible(true)
}
