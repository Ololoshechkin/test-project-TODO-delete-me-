import akka.actor.ActorSystem
import akka.actor.PoisonPill
import akka.actor.Props
import akka.cluster.singleton.ClusterSingletonManager
import akka.cluster.singleton.ClusterSingletonManagerSettings

fun main(args: Array<String>) {
    val remoteSystem = ActorSystem.create("ClusterExample")
    remoteSystem.actorOf(Props.create(ContentSubscriber::class.java), "user")
    remoteSystem.actorOf(Props.create(SpamMaker::class.java), "spammer")
}