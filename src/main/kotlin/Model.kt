import org.ojalgo.random.Poisson

val scenarioDuration = 2 //hours
val customerAvgArrivalRate =  50 // customers per minute
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

    val carryOverServingCustomers: List<Customer> by lazy {
        if (previousFrame == null)
            listOf()
        else
            previousFrame.servingCustomers.filter { (minute - it.arrivalFrame.minute) < it.processingTime }
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
    val servingCustomers: List<Customer> = carryOverServingCustomers.plus(carryOverWaitingCustomers).plus(arrivingCustomers).take(tellerCount)

    val waitingCustomers = carryOverWaitingCustomers.plus(arrivingCustomers).minus(servingCustomers)

    override fun toString() = "Frame(minute=$minute)"
}
