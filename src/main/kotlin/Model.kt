import org.ojalgo.random.Normal
import org.ojalgo.random.Poisson


class Simulation(val scenarioDuration: Int, val customersPerHour: Int, val processingTimePerCustomer: Int, val tellerCount: Int) {

    val arrivalDistribution = Poisson(customersPerHour.toDouble() / 60.0) // convert from hours to minutes
    val processingDistribution  = Normal(6.0, 4.0)

    val frames by lazy {
        var lastFrame: Frame? = null

        (1..(scenarioDuration))
                .map {
                    val frm = Frame(it, lastFrame, this)
                    lastFrame = frm
                    frm
                }
    }

    val customerArrivalCount get() = frames.asSequence().flatMap { it.arrivingCustomers.asSequence() }.distinct().count()
    val customersServedCount get() = frames.asSequence().flatMap {  it.servingCustomers.asSequence() }.distinct().count()
    val maximumQueueSize get() = frames.asSequence().map { it.waitingCustomers.count() }.max()?:0
    val maxmimumWaitTime get() = frames.asSequence().flatMap {  it.servingCustomerWaitTimes.values.map { it }.asSequence() }.max()?:0
    val waitTimes get() = frames.asSequence().flatMap {  it.servingCustomerWaitTimes.entries.asSequence() }.distinctBy { it.key }.map { it.value }


}


/**
 * A Customer assigns its own ID and processing lapsedTime
 */
class Customer(val arrivalFrame: Frame) {

    val id: Int = customerIndexer++
    val processingTime: Int = arrivalFrame.simulation.processingDistribution.get().toInt()

    companion object {
        private var customerIndexer = 0
    }
    override fun toString(): String {
        return "Customer(id=$id, arrivalHour=${arrivalFrame.minute} processingTime=$processingTime)"
    }
}

class Frame(val minute: Int, val previousFrame: Frame? = null, val simulation: Simulation) {

    val traverseBackwards = generateSequence(this) { it.previousFrame }

    val carryOverServingCustomers: List<Customer> by lazy {
        if (previousFrame == null)
            listOf()
        else
            previousFrame.servingCustomers.filter { (minute - (it.arrivalFrame.minute + (previousFrame?.servingCustomerWaitTimes[it.id]?:0) ) ) < it.processingTime }
    }

    val carryOverWaitingCustomers: List<Customer> by lazy {
        if (previousFrame == null)
            listOf()
        else
            previousFrame.waitingCustomers
    }

    val arrivingCustomers by lazy {
        (0 until simulation.arrivalDistribution.get().toInt())
                .map { Customer(this) }
                .toList()
    }
    val servingCustomers by lazy {
        carryOverServingCustomers
                .plus(carryOverWaitingCustomers)
                .plus(arrivingCustomers)
                .take(simulation.tellerCount)
    }

    val movingCustomers by lazy {
        previousFrame?.servingCustomers?.let { prevServing ->
            servingCustomers.filter { it !in prevServing }
        }?: servingCustomers
    }

    // track how long each serving customer has been delayed
    val servingCustomerWaitTimes by lazy {
        servingCustomers.map { cust ->
            cust.id to traverseBackwards.count { cust in it.waitingCustomers}
        }.toMap()
    }

    val waitingCustomers by lazy  { carryOverWaitingCustomers.plus(arrivingCustomers).minus(servingCustomers) }

    val departingCustomers by lazy {
        previousFrame?.servingCustomers?.asSequence()?.filter { it !in servingCustomers }?.toList() ?: listOf()
    }
    override fun toString() = let {
        "Frame(minute=${it.minute} arriving=${it.arrivingCustomers.map { "${it.id}[${it.processingTime}]" }.joinToString(",")} " +
                "serving=${it.servingCustomers.map { "${it.id}[${it.processingTime}]" }.joinToString(",")} " +
                "waiting=${it.waitingCustomers.map { "${it.id}[${it.processingTime}]" }.joinToString(",")})"
    }
}
