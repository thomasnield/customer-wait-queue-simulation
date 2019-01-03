import org.ojalgo.random.Exponential
import org.ojalgo.random.Normal
import org.ojalgo.random.RandomNumber
import java.lang.Double.max

data class ArrivedCustomer(val id: Int, val arrivalTime: Double)

data class ServedCustomer(val customer: ArrivedCustomer, val serverID: Int, val servedAtTime: Double, val serveTime: Double) {
    val waitTime get() = servedAtTime - customer.arrivalTime
    val doneAtTime get() = servedAtTime + serveTime

    override fun toString(): String {
        return "ServedCustomer(customer=$customer, serverId=$serverID, servedAtTime=$servedAtTime, waitTime=$waitTime, serveTime=$serveTime, doneAtTime=$doneAtTime)"
    }
}


class CustomerQueue(val serverNum: Int, val serveTime: RandomNumber) {

    val servedCustomers = mutableListOf<ServedCustomer>()

    val servers = (0 until serverNum).map { Server(it) }

    inner class Server(val id: Int) {
        var lapsedTime = 0.0 // lapsed time for this server

        fun receiveCustomer(customer: ArrivedCustomer) {

            val serveTime = max(serveTime.get(), 0.0)
            // selecting arrival lapsedTime if it is larger than server freeing lapsedTime
            lapsedTime = max(lapsedTime, customer.arrivalTime)
            //Send result to output channel
            servedCustomers.add(ServedCustomer(customer, id, lapsedTime, serveTime))
            //Advance lapsedTime after customer is served
            lapsedTime += serveTime
        }
    }

    fun receiveCustomer(customer: ArrivedCustomer) {
        servers.minBy { it.lapsedTime }!!.receiveCustomer(customer)
    }

}

fun main(args: Array<String>) {

    // parameters
    val serveTimeDistribution = Normal(6.0, 4.0)
    val nextArrivalTimeLapseDistribution = Exponential(50.0 / 60.0)
    val numberOfServers = 3
    val numberOfCustomers = 10

    // execution
    val queue = CustomerQueue(numberOfServers, serveTimeDistribution)
    var arrivalTime = 0.0

    (0 until numberOfCustomers).forEach { id ->
        arrivalTime += nextArrivalTimeLapseDistribution.get()
        val customer = ArrivedCustomer(id, arrivalTime)
        queue.receiveCustomer(customer)
    }

    for (served in queue.servedCustomers) {
        println(served)
    }

    val averageWaitTime = queue.servedCustomers.sumByDouble { it.waitTime } / numberOfCustomers
    println("Average wait lapsedTime is $averageWaitTime")
}
