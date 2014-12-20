package ru.semester3

class Solver(_field: Field) {
  import Corner._

  var oldField = _field
  var newField: Field = {
    val cells = new Array[Boolean](oldField.height * oldField.width)
    new Field(oldField.width, oldField.height, cells)
  }

  var outer = scala.collection.mutable.Map[Corner, Array[Boolean]]()

  def computedField = newField

  def reinit = {
    val temp = newField
    newField = oldField
    oldField = temp
  }

  def setCornerCells(corner: Corner, cells: Array[Boolean]): Unit = {
    outer += (corner -> cells)
  }

  def solveInnerPart(): Unit = {
    for (y <- 1 to oldField.height - 2) {
      for (x <- 1 to oldField.width - 2) {
        newField.setCell(x, y, isAlive(x, y))
      }
    }
  }

  def solveOuterPart(): Unit = {
    for (x <- 1 to oldField.width - 2) {
      newField.setCell(x, 0, isAlive(x, 0))
      newField.setCell(x, oldField.height - 1, isAlive(x, oldField.height - 1))
    }

    for (y <- 0 to oldField.height - 1) {
      newField.setCell(0, y, isAlive(0, y))
      newField.setCell(oldField.width - 1, y, isAlive(oldField.width - 1, y))
    }
  }

  private def isAlive(x: Int, y:Int) = {
    val n = neighbors(x, y)

    n == 3 || (oldField.getCell(x, y) && n == 2)
  }

  private def neighbors(x: Int, y: Int) = {
    def cast(v: Boolean) = if(v) 1 else 0

    (cast(getCell(x - 1, y - 1))
    + cast(getCell(x - 1, y))
    + cast(getCell(x - 1, y + 1))
    + cast(getCell(x, y - 1))
    + cast(getCell(x, y + 1))
    + cast(getCell(x + 1, y - 1))
    + cast(getCell(x + 1, y))
    + cast(getCell(x + 1, y + 1)))
  }

  private def getCell(x: Int, y: Int): Boolean = {
    def between(lo: Int, hi: Int, v: Int) = {
      if (v > hi) {
        1
      } else if (v < lo) {
        -1
      } else {
        0
      }
    }

    (between(0, oldField.width - 1, x), between(0, oldField.height - 1, y)) match  {
      case (0,0) => oldField.getCell(x, y)
      case (-1,0) => outer(Left)(y)
      case (1,0) => outer(Right)(y)
      case (-1,-1) => outer(TopLeft)(0)
      case (0,-1) => outer(Top)(x)
      case (1,-1) => outer(TopRight)(0)
      case (-1,1) => outer(BottomLeft)(0)
      case (0,1) => outer(Bottom)(x)
      case (1,1) => outer(BottomRight)(0)
    }
  }
}
