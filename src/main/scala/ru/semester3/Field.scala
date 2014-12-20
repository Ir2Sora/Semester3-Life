package ru.semester3

import ru.semester3.Corner._

class Field(_width: Int, _height: Int, _cells: Array[Boolean]) {

  def width = _width

  def height = _height

  def cells = _cells

  def getCell(x: Int, y: Int) = cells(y * width + x)

  def setCell(x: Int, y: Int, value: Boolean) = cells(y * width + x) = value

  def cornerCells(corner: Corner): Array[Boolean] = {
    corner match {
      case Top => cells.slice(0, width)
      case TopRight => Array(getCell(width - 1, 0))
      case Right => Range(0, height).map(i => getCell(width - 1, i)).toArray
      case BottomRight => Array(getCell(width - 1, height - 1))
      case Bottom => cells.slice(cells.length - width, cells.length)
      case BottomLeft => Array(getCell(0, height - 1))
      case Left => Range(0, height).map(i => getCell(0, i)).toArray
      case TopLeft => Array(getCell(0, 0))
    }
  }
}
