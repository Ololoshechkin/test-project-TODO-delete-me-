import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.ExtendedActorSystem
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator
import akka.japi.pf.ReceiveBuilder
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class SpamMaker: AbstractActor() {

    private val log = context.system().log()
    private val mediator = DistributedPubSub.get(context.system()).mediator()
    private var spamCnt = 0

    init {
        preStart()
        scheduleWakeUp()
    }

    override fun createReceive(): Receive = ReceiveBuilder.create()
            .matchEquals("ban", {
                context().stop(self)
            }).matchEquals("spam more!", {
                mediator.tell(DistributedPubSubMediator.Publish("content", "spam"), self)
                scheduleWakeUp()
            })
            .build()

    override fun preStart() {
        mediator.tell(DistributedPubSubMediator.Subscribe("content", self()), self())
    }

    override fun postStop() {
        mediator.tell(DistributedPubSubMediator.Unsubscribe("content", self()), self())
    }

    private fun scheduleWakeUp() {
        context().system().scheduler().scheduleOnce(
                Duration.create(1, TimeUnit.SECONDS),
                self(),
                "spam more!",
                context().dispatcher(),
                ActorRef.noSender()
        )
    }
}