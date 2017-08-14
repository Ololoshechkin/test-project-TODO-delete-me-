import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.File

suspend fun doActions() {
    var x = 0;
    for (i in 0..1000) {
        x += i
    }
    println("x = $x\n")
    val txt = File("/Users/Vadim/Downloads/b/src/main/kotlin/ContentSubscriber.kt").readText()
    println("$txt")
}

fun main(args: Array<String>) {
    async(CommonPool) {
       doActions()
    }
    var s = 0.0
    for (i in 0..100_000_000) {
        s += Math.sin(i.toDouble())
    }
    println("s = $s")
}