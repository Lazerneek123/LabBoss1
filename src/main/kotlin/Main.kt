import java.util.concurrent.*

val data = intArrayOf(1, 2, 3, 4, 5, 6)

fun main() {
    val result = calculateSum(data)
    println("Cума елементів масиву: $result")
}

fun calculateSum(data: IntArray): Int {
    val queue = ConcurrentLinkedDeque<Int>()
    queue.addAll(listOf(*data.toTypedArray()))

    val executor = Executors.newFixedThreadPool(data.size / 2) // Створюємо пул потоків
    var countWave = 1
    while (queue.size > 1) {
        println(queue)
        val tasks = mutableListOf<Callable<Int>>()
        println("Хвиля: $countWave")
        countWave++
        for (i in 0 until queue.size / 2) {
            tasks.add(SumCallable(queue.pollFirst(), queue.pollLast()))
        }

        val futures: List<Future<Int>> = executor.invokeAll(tasks)

        for (future in futures) {
            queue.add(future.get())
        }
    }

    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.MINUTES)

    return queue.poll()
}

class SumCallable(private val num1: Int, private val num2: Int) : Callable<Int> {
    override fun call(): Int {
        println("$num1 + $num2")
        return num1 + num2
    }
}