import akka.actor.AbstractActor
import akka.actor.ExtendedActorSystem
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator
import akka.japi.pf.ReceiveBuilder
import scala.PartialFunction
import scala.runtime.BoxedUnit

class ContentSubscriber : AbstractActor() {
    override fun createReceive(): Receive = ReceiveBuilder.create()
            .match(String::class.java, {
                log.debug("subscriber received message : $it")
            })
            .build()

    private val log = context.system().log()
    private val mediator = DistributedPubSub.get(context.system()).mediator()

    override fun preStart() {
        mediator.tell(DistributedPubSubMediator.Subscribe("content", self()), self())
    }

    override fun postStop() {
        mediator.tell(DistributedPubSubMediator.Unsubscribe("content", self()), self())
    }

}