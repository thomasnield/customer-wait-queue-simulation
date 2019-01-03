import javafx.animation.SequentialTransition
import javafx.animation.Timeline
import javafx.application.Application
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import tornadofx.*
import java.util.concurrent.ThreadLocalRandom

fun main(args:Array<String>) = Application.launch(AnimationApp::class.java, *args)

class AnimationApp: App(AnimationView::class)

val edgeTop = 100.0
val edgeLeft = 250.0
val radius = 10.0
val lobbyHeight = 200.0


val deskWidth = 60.0
val deskHeight = 30.0

val queueStartX =  edgeLeft + 400.0

operator fun SequentialTransition.plusAssign(timeline: Timeline) { children.add(timeline) }
operator fun SequentialTransition.plusAssign(timelines: Iterable<Timeline>) { timelines.forEach { children.add(it) }  }


class AnimationView: View() {


    override val root = borderpane {

        primaryStage.isMaximized=true

        center = pane {

            val simulation = Simulation(
                            scenarioDuration = 60,
                            customersPerHour = 100,
                            processingTimePerCustomer = 6,
                            tellerCount = 7
            ).also {
                it.frames.forEach(::println)
            }

            SimulationFX(simulation, this).animate()

        }
    }
}

class SimulationFX(val simulation: Simulation, val pane: Pane) {

    val animationQueue = SequentialTransition()
    val minuteNumberFx = SimpleIntegerProperty(0)
    val arrivingNumberFx = SimpleIntegerProperty(0)
    val servingNumberFx = SimpleIntegerProperty(0)
    val waitingNumberFx = SimpleIntegerProperty(0)
    val maxWaitingNumberFx = SimpleIntegerProperty(0)
    val customersArrivedFx = SimpleIntegerProperty(0)
    val customersServedFx = SimpleIntegerProperty(0)
    val maxWaitingTimeFx = SimpleIntegerProperty(0)
    val avgWaitingTimeFx = SimpleIntegerProperty(0)

    val desks = (1..simulation.tellerCount).map {
        TellerFX(it).also { pane += it }
    }.toList()

    fun animate()  {

        pane.form {

            fieldset("PARAMETERS") {
                field("AVG ARRIVALS/HR") {
                    label(simulation.customersPerHour.toString())
                }
                field("AVG SERVICE TIME (MINS)") {
                    label(simulation.processingTimePerCustomer .toString())
                }
                field("# CLERKS") {
                    label(simulation.tellerCount.toString())
                }
                field("SIMULATION LENGTH (MINS)") {
                    label(simulation.scenarioDuration .toString())
                }
            }
            fieldset("SCENARIO") {
                field("MINUTE") {
                     label(minuteNumberFx)
                }
                field("ARRIVING") {
                    label(arrivingNumberFx)
                }
                field("SERVING") {
                    label(servingNumberFx)
                }
                field("WAITING") {
                    label(waitingNumberFx)
                }
            }
            fieldset("PERFORMANCE") {
                field("CUSTOMERS ARRIVED CT") {
                    label(customersArrivedFx)
                }
                field("CUSTOMERS SERVED CT") {
                    label(customersServedFx)
                }
                field("MAX WAITING CT") {
                    label(maxWaitingNumberFx)
                }
                field("MAX WAITING TIME") {
                    label(maxWaitingTimeFx)
                }
                field("AVG WAITING TIME") {
                    label(avgWaitingTimeFx)
                }
            }
        }

        val customerFxCache = mutableMapOf<Int,CustomerFX>()

        var queueSize = 0

        simulation.frames.asSequence().forEach { frame ->

            animationQueue += timeline(play=false) {
                keyframe(if (frame.movingCustomers.size == 0) 1.seconds else 1.millis) {
                    keyvalue(minuteNumberFx, frame.minute)
                    keyvalue(servingNumberFx, frame.servingCustomers.count())
                    keyvalue(waitingNumberFx, frame.waitingCustomers.count())
                    keyvalue(arrivingNumberFx, frame.arrivingCustomers.count())
                    keyvalue(customersArrivedFx, frame.traverseBackwards.flatMap { it.arrivingCustomers.asSequence() }.distinct().count() )
                    keyvalue(customersServedFx, frame.traverseBackwards.flatMap { it.servingCustomers.asSequence() }.distinct().count() )
                    keyvalue(maxWaitingNumberFx, frame.traverseBackwards.map { it.waitingCustomers.count() }.max()?:0)
                    keyvalue(maxWaitingTimeFx, frame.traverseBackwards.flatMap { it.servingCustomerWaitTimes.values.map { it }.asSequence() }.max()?:0)
                    keyvalue(avgWaitingTimeFx, frame.traverseBackwards.flatMap { it.servingCustomerWaitTimes.entries.asSequence() }.distinctBy { it.key }.map { it.value }.average())
                }
            }
            val changeMovements = timeline(play=false) { }
            val leaveMovements = timeline(play=false) { }
            val arrivalMovements = timeline(play=false) { }

            // handle customers leaving
            frame.departingCustomers.asSequence()
                    .map { customerFxCache[it.id]!! }
                    .forEach {
                        it.leave(leaveMovements)
                        customerFxCache.remove(it.customer.id)
                    }

            // handle customer arrivals
            frame.arrivingCustomers.asSequence()
                    .mapIndexed { index, customer ->
                        customerFxCache.computeIfAbsent(customer.id) {
                            CustomerFX(customer, queueSize++, this).also {
                                pane += it
                                it.entrance(arrivalMovements)
                            }
                        }
                    }.toList()

            val customersToMove = frame.movingCustomers.asSequence()
                    .map { customerFxCache[it.id]!! }
                    .toList()

            // handle customers moving to desk
            customersToMove.forEach { movingCustomer ->
                movingCustomer.moveToDesk(desks.first { it.currentCustomerFX == null }, changeMovements)
                queueSize--
            }

            val queueMovements = frame.waitingCustomers.asSequence()
                    .map { customerFxCache[it.id]!! }
                    .mapIndexed { index, customerFX ->
                        timeline(play=false) {
                            customerFX.moveToPosition(index, this)
                        }
                    }.toList()

            animationQueue += arrivalMovements
            animationQueue += leaveMovements
            animationQueue += changeMovements
            animationQueue += queueMovements

        }
        animationQueue.play()

    }
}

class CustomerFX(val customer: Customer, val startingIndex: Int, val simulationFX: SimulationFX): Circle() {

    var currentIndex: Int = startingIndex
    var currentTeller: TellerFX? = null

    init {
        radius = 10.0
        centerX = queueStartX + (startingIndex * 24.0) + 400.0
        centerY = edgeTop + lobbyHeight
        opacity = 0.0
    }

    fun entrance(timeline: Timeline) {
        timeline.keyframe(500.millis) {
                keyvalue(centerXProperty(), queueStartX + (currentIndex * 24.0))
                keyvalue(opacityProperty(),1.0)
            }
    }
    fun moveToPosition(index: Int, timeline: Timeline) {
        timeline.keyframe(if (index <= 3) 300.millis else 100.millis) {
            keyvalue(centerXProperty(), queueStartX + (index * 24.0))
            keyvalue(centerYProperty(),  edgeTop + lobbyHeight)
        }
    }


    fun moveToDesk(tellerFX: TellerFX, timeline: Timeline) {
        currentTeller = tellerFX
        tellerFX.currentCustomerFX = this

        timeline.keyframe(500.millis) {
            keyvalue(centerXProperty(), (tellerFX.x) + (deskWidth * .5))
            keyvalue(centerYProperty(), (tellerFX.y) + (deskHeight * 1.25))
        }
    }

    fun leave(timeline: Timeline) {
        timeline.keyframe(500.millis) {
                keyvalue(centerYProperty(), (currentTeller?.y?:0.0) + 60.0)
                keyvalue(centerXProperty(), (currentTeller?.x?:0.0) + if (ThreadLocalRandom.current().nextInt(0,1) == 1) 20.0 else -20.0)
                keyvalue(opacityProperty(), 0.0)
            }
        currentTeller?.currentCustomerFX = null
    }
}

class TellerFX(val position: Int): Rectangle() {

    var currentCustomerFX: CustomerFX? = null

    init {
        width = deskWidth
        height = deskHeight
        fill = Color.BROWN
        y = edgeTop
        x = edgeLeft + (position * 200.0) - radius
    }
}
