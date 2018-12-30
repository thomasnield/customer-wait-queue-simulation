import org.ojalgo.random.Poisson

val scenarioDuration = 2 //hours
val customerAvgArrivalRate =  50 // customers per hour
val customerAvgProcessingRate = 3 // minutes per customer


// model distribution converted into minutes
val arrivalDistribution = Poisson(50.0 / 60.0)

val processingDistribution  = Poisson(3.0)

fun main(args: Array<String>) {

    arrivalDistribution.getProbability(1).let(::println)


}


class Frame(val hour: Int) {
}
