package ru.semester3

import java.io.File

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await

object ApplicationMain extends App {

  if (args.length < 3) {
    println("usage:  <src-data> <dest-data> <numberOfIterations> <addresses-data>")
    System.exit(1)
  }

  val srcPath = args(0)
  val destPath = args(1)
  val numberOfIterations = args(2).toInt

  val field = IOUtils.loadFromFile(new File(srcPath))

  val system = ActorSystem("MyActorSystem")
  val gameActor = system.actorOf(GameActor.props, "gameActor")

  val addresses = if (args.length == 4) {
    Some(IOUtils.getAddresses(args(3)))
  } else {
    None
  }

  implicit val timeout = Timeout(100 second)

  val beginTime = System.nanoTime()

  val future = gameActor ? GameActor.Start(field, numberOfIterations, addresses)
  val end = Await.result(future, timeout.duration).asInstanceOf[GameActor.End]

  val endTime = System.nanoTime()
  println("duration " + (endTime - beginTime) / 1000000000.0 + " sec")

  IOUtils.saveToFile(new File(destPath), end.field)

  system.shutdown()
}