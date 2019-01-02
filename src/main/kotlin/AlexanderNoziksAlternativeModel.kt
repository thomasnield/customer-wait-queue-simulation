
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sumByDouble
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.ojalgo.random.Exponential
import org.ojalgo.random.Normal
import org.ojalgo.random.RandomNumber
import kotlin.math.max

data class CustomerModel(val id: Int, val arrivalTime: Double)



data class ServedCustomerModel(val customer: CustomerModel, val serverID: Int, val servedTime: Double) {

    val waitTime get() = servedTime - customer.arrivalTime

}

class CustomerQueue(val serverNum: Int, val serveTime: RandomNumber, val scope: CoroutineScope) {

    //private val arriveChannel = Channel<CustomerModel>(Channel.UNLIMITED)

    val servedChannel: Channel<ServedCustomerModel> = Channel(Channel.UNLIMITED)

    val servers = (0 until serverNum).map { Server(it) }

    inner class Server(val id: Int) {

        val channel = Channel<CustomerModel>(Channel.RENDEZVOUS)

        var time = 0.0

        suspend fun send(customer: CustomerModel){

            // selecting arrival time if it is larger than server freeing time

            time = max(time, customer.arrivalTime)

            //Send result to output channel

            servedChannel.send(ServedCustomerModel(customer, id, time))

            //Advance time after customer is served

            time += serveTime.get()

        }
    }

    suspend fun send(customer: CustomerModel) {

        //Select a server that is freed first. There could be some bugs with parallel computations here

        servers.minBy { it.time }!!.send(customer)

    }

    fun close() {

        servedChannel.close()

    }
}



suspend fun main(args: Array<String>) {

    val serveTimeDistribution = Normal(6.0, 1.0)

    val arrivalTimeDifDistribution = Exponential(50.0 / 60.0)

    val customerNum = 100



    coroutineScope {

        val queue: CustomerQueue = CustomerQueue(5, serveTimeDistribution, this)

        var arrivalTime = 0.0

        (0 until customerNum).forEach { id ->

            arrivalTime += arrivalTimeDifDistribution.get()

            val customer = CustomerModel(id, arrivalTime)

            queue.send(customer)

        }

        queue.close()



        for (served in queue.servedChannel) {
            launch(Dispatchers.Unconfined) {

                println(served)

            }

        }



        val averageWaitTime = queue.servedChannel.sumByDouble { it.waitTime } / customerNum

        launch(Dispatchers.Unconfined) {

            println("Average wait time is $averageWaitTime")

        }

    }

}