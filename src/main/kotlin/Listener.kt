import akka.actor.AbstractActor
import akka.cluster.Cluster
import akka.cluster.ClusterEvent
import akka.cluster.MemberStatus
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator
import akka.japi.pf.ReceiveBuilder
import scala.Option

class Listener : AbstractActor() {
    private val mediator = DistributedPubSub.get(context.system()).mediator()
    private val cluster = Cluster.get(context.system)

    override fun createReceive() = ReceiveBuilder()
            .match(ClusterEvent.MemberJoined::class.java, { msg ->
                println("joined : ${msg.member()}")
            })
            .match(ClusterEvent.MemberRemoved::class.java, { msg ->
                println("removed : ${msg.member()}")
            })
            .build()

    init {
        println("start members : ${cluster.state().members.joinToString(", ","{", "}")})")
    }

    override fun preRestart(reason: Throwable?, message: Option<Any>?) {
        cluster.subscribe(self, ClusterEvent.MemberEvent::class.java)
    }

    override fun postStop() {
        cluster.unsubscribe(self, ClusterEvent.MemberEvent::class.java)
    }
}