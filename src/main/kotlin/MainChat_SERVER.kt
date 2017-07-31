import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.PoisonPill
import akka.actor.Props
import akka.cluster.singleton.ClusterSingletonManager
import akka.cluster.singleton.ClusterSingletonManagerSettings
import akka.routing.RandomPool
import com.typesafe.config.ConfigFactory
import scala.Option

fun main(args: Array<String>) {
    val actorSystem = ActorSystem.create("ClusterExample")

    val settings = ClusterSingletonManagerSettings.create(actorSystem)
    actorSystem.actorOf(ClusterSingletonManager.props(
            Props.create(ContentMaker::class.java),
            PoisonPill.getInstance(),
            settings
    ), "singleton")
}