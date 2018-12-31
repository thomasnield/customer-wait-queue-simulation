import org.ojalgo.random.Poisson

val scenarioDuration = 2 //hours
val customerAvgArrivalRate =  30 // customers per hour
val customerAvgProcessingTime = 5 // minutes per customer
val tellerCount = 3

val arrivalDistribution = Poisson(customerAvgArrivalRate.toDouble() / 60.0) // convert from hours to minutes
val processingDistribution  = Poisson(customerAvgProcessingTime.toDouble())

fun main(args: Array<String>) {

    var lastFrame: Frame? = null

    (1..(scenarioDuration*60))
            .map {
                val frm = Frame(it, lastFrame)
                lastFrame = frm
                frm
            }
            .forEach {
                println("${it.minute} arriving=${ it.arrivingCustomers.map { "${it.id}[${it.processingTime}]" }.joinToString(",") } " +
                        "serving=${it.servingCustomers.map { "${it.id}[${it.processingTime}]" }.joinToString(",")} " +
                        "waiting=${it.waitingCustomers.map { "${it.id}[${it.processingTime}]" }.joinToString(",")} ")
            }
}

/**
 * A Customer assigns its own ID and processing time
 */
class Customer(val arrivalFrame: Frame) {

    val id: Int = customerIndexer++
    val processingTime: Int = processingDistribution.get().toInt()

    companion object {
        private var customerIndexer = 0
    }
    override fun toString(): String {
        return "Customer(id=$id, arrivalHour=${arrivalFrame.minute} processingTime=$processingTime)"
    }
}

class Frame(val minute: Int, val previousFrame: Frame? = null) {

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
        (0 until arrivalDistribution.get().toInt())
                .map { Customer(this) }
                .toList()
    }
    val servingCustomers: List<Customer> by lazy {  carryOverServingCustomers.plus(carryOverWaitingCustomers).plus(arrivingCustomers).take(tellerCount) }

    // track how long each serving customer has been delayed
    val servingCustomerWaitTimes by lazy {
        servingCustomers.map { cust ->
            cust.id to traverseBackwards.count { cust in it.waitingCustomers}
        }.toMap()

    }

    val waitingCustomers by lazy  { carryOverWaitingCustomers.plus(arrivingCustomers).minus(servingCustomers) }


    override fun toString() = "Frame(minute=$minute)"
}
