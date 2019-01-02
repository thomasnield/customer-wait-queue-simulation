import org.ojalgo.random.Exponential
import org.ojalgo.random.Normal
import org.ojalgo.random.RandomNumber
import java.lang.Double.max

data class ArrivedCustomer(val id: Int, val arrivalTime: Double)

data class ServedCustomer(val customer: ArrivedCustomer, val serverID: Int, val servedAtTime: Double) {
    val waitTime get() = servedAtTime - customer.arrivalTime

    override fun toString(): String {
        return "ServedCustomer(customer=$customer, serverID=$serverID, servedAtTime=$servedAtTime, waitTime=$waitTime)"
    }
}


class CustomerQueue(val serverNum: Int, val serveTime: RandomNumber) {

    val servedChannel = mutableListOf<ServedCustomer>()

    val servers = (0 until serverNum).map { Server(it) }

    inner class Server(val id: Int) {
        var time = 0.0

        fun receiveCustomer(customer: ArrivedCustomer) {
            // selecting arrival time if it is larger than server freeing time
            time = max(time, customer.arrivalTime)
            //Send result to output channel
            servedChannel.add(ServedCustomer(customer, id, time))
            //Advance time after customer is served
            time += max(serveTime.get(), 0.0)
        }
    }

    fun receiveCustomer(customer: ArrivedCustomer) {
        servers.minBy { it.time }!!.receiveCustomer(customer)
    }

}

fun main(args: Array<String>) {
    val serveTimeDistribution = Normal(6.0, 4.0)
    val nextArrivalTimeLapseDistribution = Exponential(10.0 / 60.0)
    val numberOfCustomers = 10

    val queue = CustomerQueue(1, serveTimeDistribution)
    var arrivalTime = 0.0

    (0 until numberOfCustomers).forEach { id ->
        arrivalTime += nextArrivalTimeLapseDistribution.get().also { println("LAPSE: $it")}
        val customer = ArrivedCustomer(id, arrivalTime)
        queue.receiveCustomer(customer)
    }

    for (served in queue.servedChannel) {
        println(served)
    }

    val averageWaitTime = queue.servedChannel.sumByDouble { it.waitTime } / numberOfCustomers
    println("Average wait time is $averageWaitTime")
}
