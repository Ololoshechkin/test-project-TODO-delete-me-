import akka.actor.ActorSystem
import akka.actor.PoisonPill
import akka.actor.Props
import akka.cluster.singleton.ClusterSingletonManager
import akka.cluster.singleton.ClusterSingletonManagerSettings
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigValueFactory

fun main(args: Array<String>) {
    val masterHost = "169.254.215.106"
    val slaveHost = "169.254.199.44"
    val masterPort = 2551
    val slavePort = 2552

    val hostname = masterHost
    val port = masterPort

    val seedNodes = listOf("akka.tcp://System@$masterHost:$masterPort", "akka.tcp://System@$slaveHost:$slavePort")

    val config = ConfigFactory.load().getConfig("DJConfig").withValue(
            "akka.remote.netty.tcp",
            ConfigValueFactory.fromMap(mutableMapOf("hostname" to hostname, "port" to port))
    ).withValue("akka.cluster.seed-nodes", ConfigValueFactory.fromIterable(seedNodes))

    val system = ActorSystem.create("System", config)


    system.actorOf(ClusterSingletonManager.props(
            Props.create(Listener::class.java),
            PoisonPill.getInstance(),
            ClusterSingletonManagerSettings.create(system)
    ), "singleton")

}