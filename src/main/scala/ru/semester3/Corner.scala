package ru.semester3

import scala.language.implicitConversions

object Corner extends Enumeration {
  type Corner = Value
  val TopLeft, Top, TopRight, Right, BottomRight, Bottom, BottomLeft, Left = Value

  class CornerValue(corner: Value) {
    def opposite = corner match {
      case TopLeft => BottomRight
      case Top => Bottom
      case TopRight => BottomLeft
      case Right => Left
      case BottomRight => TopLeft
      case Bottom => Top
      case BottomLeft => TopRight
      case Left => Right
    }
  }

  implicit def valueToCornerValue(corner: Value): CornerValue = new CornerValue(corner)
}
