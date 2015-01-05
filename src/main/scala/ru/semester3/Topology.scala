package ru.semester3

import akka.actor.ActorRef
import ru.semester3.Corner._

class Topology(_width: Int, _height: Int, actors: Array[ActorRef]) extends Serializable {

  def width = _width
  def height = _height
  def size = width * height

  private def actor(_x: Int, _y: Int): ActorRef = {
    def improveParams = {
      var x: Int = _x
      var y: Int = _y

      if (x < 0 || x >= width) {
        x = (x + width) % width
      }

      if (y < 0 || y >= height) {
        y = (y + height) % height
      }

      (x, y)
    }

    val (x, y) = improveParams

    actors(y * width + x)
  }

  def neighbor(num: Int, corner: Corner) = {
    val y = num / height
    val x = num % height

    corner match {
      case TopLeft => actor(x - 1, y - 1)
      case Top => actor(x, y - 1)
      case TopRight => actor(x + 1, y - 1)
      case Right => actor(x + 1, y)
      case BottomRight => actor(x + 1, y + 1)
      case Bottom => actor(x, y + 1)
      case BottomLeft => actor(x - 1, y + 1)
      case Left => actor(x - 1, y)
    }
  }
}
