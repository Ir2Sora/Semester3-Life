package ru.semester3

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory


object RemoteApplication extends App {

  if (args.length != 3) {
    println("usage:  <system-name> <host-name> <port>")
    System.exit(1)
  }

  val customConf = ConfigFactory.parseString( s"""
        akka {
           actor {
              provider = "akka.remote.RemoteActorRefProvider"
           }

           remote {
              transport = "akka.remote.netty.NettyRemoteTransport"
              maximum-payload-bytes = 3000000 bytes
              netty.tcp {
                 maximum-frame-size = 3000000b
                 hostname = "${args(1)}"
                 port = ${args(2)}
              }
           }
        }
      """)

  val system = ActorSystem(args(0), ConfigFactory.load(customConf))
}