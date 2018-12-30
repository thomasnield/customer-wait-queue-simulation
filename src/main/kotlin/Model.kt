import org.ojalgo.random.Poisson

val scenarioDuration = 2 //hours
val customerAvgArrivalRate =  50 // customers per hour
val customerAvgProcessingRate = 5 // minutes per customer


val arrivalDistribution = Poisson(customerAvgArrivalRate.toDouble() / 60.0) // convert from hours to minutes
val processingDistribution  = Poisson(customerAvgProcessingRate.toDouble())

fun main(args: Array<String>) {

    // this is working! approx 50 customers per hour
    (0 until 6000).asSequence().map { arrivalDistribution.get() }
            .let { it.sum().toDouble() / 100.0 }
            .let(::println)
}


class Frame(val hour: Int) {
}
