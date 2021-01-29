package bus.cart

import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement

object Main {

  def main(args: Array[String]): Unit = {
    ActorSystem[Nothing](Main(), "BusCartService")
  }

  def apply(): Behavior[Nothing] = {
    Behaviors.setup[Nothing](context => new Main(context))
  }
}

class Main(context: ActorContext[Nothing])
    extends AbstractBehavior[Nothing](context) {
  val system = context.system

  AkkaManagement(system).start()
  ClusterBootstrap(system).start()

  BusCart.init(system)

  val grpcInterface =
    system.settings.config.getString("bus-cart-service.grpc.interface")
  val grpcPort =
    system.settings.config.getInt("bus-cart-service.grpc.port")
  val grpcService = new BusCartServiceImpl(system)
  BusCartServer.start(grpcInterface, grpcPort, system, grpcService)

  override def onMessage(msg: Nothing): Behavior[Nothing] =
    this
}
