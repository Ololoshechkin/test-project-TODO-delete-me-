import akka.actor.AbstractActor
import akka.actor.ActorRef
import akka.actor.ExtendedActorSystem
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator
import akka.japi.pf.ReceiveBuilder
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class ContentMaker: AbstractActor() {

    private val log = context.system().log()
    private val mediator = DistributedPubSub.get(context.system()).mediator()
    private var spamCnt = 0

    init {
        preStart()
        scheduleWakeUp()
    }

    override fun createReceive(): Receive = ReceiveBuilder()
            .matchEquals("start", {
                mediator.tell(DistributedPubSubMediator.Publish("content", "some new message"),
                self)
                scheduleWakeUp()
            }).matchEquals("spam", {
                spamCnt++
                if (spamCnt >= 3) {
                    sender.tell("ban", self)
                }
            })
            .matchEquals(1, { _ ->
                println("FOT")
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
                "start",
                context().dispatcher(),
                ActorRef.noSender()
        )
    }
}