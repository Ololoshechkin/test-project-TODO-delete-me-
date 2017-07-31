import akka.actor.ActorSystem
import akka.actor.PoisonPill
import akka.actor.Props
import akka.cluster.singleton.ClusterSingletonManager
import akka.cluster.singleton.ClusterSingletonManagerSettings
import akka.routing.RandomPool
import com.typesafe.config.ConfigFactory
import scala.Option

fun main(args: Array<String>) {
    val actorSystem = ActorSystem.create("SystemCluster", ConfigFactory.load().getConfig("SystemCluster"))

    val settings = ClusterSingletonManagerSettings.create(actorSystem)
    actorSystem.actorOf(ClusterSingletonManager.props(
            Props.create(ContentMaker::class.java),
            "master",
            settings
    ),
    "master")

    actorSystem.actorOf(
            RandomPool(5).props(Props.create(ContentSubscriber::class.java)),
            "user"
    )
    actorSystem.actorOf(
            Props.create(SpamMaker::class.java),
            "spammer"
    )
}