package ru.semester3

import akka.actor.{ActorRef, Props, ActorLogging, Actor}

class ComputeActor(num: Int) extends Actor with ActorLogging {

  import ComputeActor._

  val neighbors = 8
  var topology: Topology = _
  var iterations: Int = _
  var gameActor: ActorRef = _
  var solver: Solver = _
  var mailbox: List[CornerCells] = List[CornerCells]()

  def receive = {
    case Initialize(_field, _topology, _iterations) =>
      topology = _topology
      iterations = _iterations
      gameActor = sender()
      solver = new Solver(_field)
//
//      var s: Set[String] = Set()
//      Corner.values.foreach(c =>
//        s += "num " + num + " neigh " + topology.neighbor(num, c) + "\n")
//      println(s)
//      println("size " + (s.size < 8))

      Corner.values.foreach(c =>
        topology.neighbor(num, c) ! CornerCells(c.opposite, _field.cornerCells(c), 0))
      mailbox.foreach(letter => self ! letter)
      mailbox = List()

      solver.solveInnerPart()
      context become work(0, 0)
    case CornerCells(corner, cells, iteration) =>
      mailbox = CornerCells(corner, cells, iteration) :: mailbox
  }

  def work(curIteration: Int, waitNum: Int): Receive = {
    case CornerCells(corner, cells, iteration) if iteration == curIteration =>
//      println("numl " + num + "; iter " + iteration + "; corner" + corner)
      processCornerCells(CornerCells(corner, cells, iteration), waitNum)
    case CornerCells(corner, cells, iteration) if iteration > curIteration =>
//      println("num " + num + "; iter " + iteration + "; waitNum" + waitNum + "; outer size" + solver.outer.size)
      mailbox = CornerCells(corner, cells, iteration) :: mailbox
  }

  def processCornerCells(letter: CornerCells, waitNum: Int): Unit = {
    solver.setCornerCells(letter.corner, letter.cells)

    if (neighbors > waitNum + 1) {
      context become work(letter.iteration, waitNum + 1)
    } else {
    //  println("num " + num + "; iter " + letter.iteration + "; waitNum" + waitNum + "; outer size" + solver.outer.size)
      solver.solveOuterPart()

      if (iterations > letter.iteration + 1) {
        Corner.values.foreach(c =>
          topology.neighbor(num, c) ! CornerCells(c.opposite, solver.computedField.cornerCells(c), letter.iteration + 1))
        mailbox.foreach(letter => self ! letter)

        solver.reinit
        mailbox = List()

        solver.solveInnerPart()
        context become work(letter.iteration + 1, 0)
      } else {
        gameActor ! Computed(solver.computedField, num)
        context.stop(self)
      }
    }
  }
}

object ComputeActor {

  import Corner._

  def props(number: Int): Props = Props(new ComputeActor(number))
  case class Initialize(field: Field, topology: Topology, iterations: Int)
  case class CornerCells(corner: Corner, cells: Array[Boolean], iteration: Int)
  case class Computed(field: Field, num: Int)
}