package ru.semester3

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import ru.semester3.ComputeActor.Computed

class GameActor extends Actor with ActorLogging {

  import ru.semester3.GameActor._

  val widthThreshold = 1000
  val heightThreshold = 1000

  var senderActor: ActorRef = _
  var newField: Field = _
  var topology: Topology = _

  def receive = {
    case Start(field, iterations) =>
      println("width " + field.width)
      println("height " + field.height)
      println("cells " + field.cells.length)
      println("iterations " + iterations)

      senderActor = sender()
      newField = new Field(field.width, field.height, new Array[Boolean](field.width * field.height))

      val (topologyWidth, topologyHeight) = computeTopologyBounds(field.width, field.height)

      val actors = Range(0, topologyWidth * topologyHeight)
        .map(i => context.actorOf(ComputeActor.props(i), "ComputeActor-n" + i)).toArray

      topology = new Topology(topologyWidth, topologyHeight, actors)

      for (i <- 0 to actors.length - 1) {
        actors(i) ! ComputeActor.Initialize(getPartField(field, i), topology, iterations)
      }

      context become work(0)
  }

  def work(waitNum: Int): Receive = {
    case Computed(field, actorNum) =>
      setPartField(field, actorNum)

      if (topology.size > waitNum + 1) {
        context become work(waitNum + 1)
      } else {
        senderActor ! End(newField)
        context.stop(self)
      }
  }

  def computeTopologyBounds(width: Int, height: Int) = {
    val numberColumns = width / widthThreshold
    val numberRows = height / heightThreshold

    if (numberColumns == 0 || width % numberColumns != 0 || numberRows == 0 || height % numberRows != 0) {
      (1, 1)
    } else {
      (numberColumns, numberRows)
    }
  }

  def getPartField(field: Field, index: Int) = {
    val row = index / topology.height
    val col = index % topology.height

    val (width, height) = (field.width / topology.width, field.height / topology.height)
    val cells = new Array[Boolean](width * height)
    val partField = new Field(width, height, cells)

    for (y <- 0 to height - 1) {
      for (x <- 0 to width - 1) {
        partField.setCell(x, y, field.getCell(x + col * width, y + row * height));
      }
    }

    partField
  }

  def setPartField(part: Field, index: Int): Unit = {
    for (y <- 0 to part.height - 1) {
      for (x <- 0 to part.width - 1) {
        val row = index / topology.height
        val col = index % topology.height

        newField.setCell(x + col * part.width, y + row * part.height, part.getCell(x, y))
      }
    }
  }
}

object GameActor {
  val props = Props[GameActor]

  case class Start(field: Field, iteration: Int)
  case class End(field: Field)
}


