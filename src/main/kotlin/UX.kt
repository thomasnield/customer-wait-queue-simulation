import javafx.animation.SequentialTransition
import javafx.animation.Timeline
import javafx.application.Application
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import tornadofx.*
import java.util.concurrent.ThreadLocalRandom

fun main(args:Array<String>) = Application.launch(AnimationApp::class.java, *args)

class AnimationApp: App(AnimationView::class)

val edgeTop = 100.0
val edgeLeft = 200.0
val radius = 10.0
val lobbyHeight = 200.0


val deskWidth = 60.0
val deskHeight = 30.0

val centerX = edgeLeft + ((3 * 200.0) * (1.0/3.0))

val queueStartX =  centerX

operator fun SequentialTransition.plusAssign(timeline: Timeline) { children.add(timeline) }


class AnimationView: View() {

    val animationQueue = SequentialTransition()

    override val root = borderpane {

        primaryStage.isMaximized=true

        center = pane {

            val simulation = Simulation(
                            scenarioDuration = 120,
                            customersPerHour = 300,
                            processingTimePerCustomer = 5,
                            tellerCount = 3)

            generateSequence { SimulationFX(simulation, this) }.first { it.simulation.frames.first().arrivingCustomers.count() >= 2 }.animate()

        }
    }
}

class SimulationFX(val simulation: Simulation, val pane: Pane) {

    val animationQueue = SequentialTransition()

    val desks = (1..simulation.tellerCount).map {
        TellerFX(it).also { pane += it }
    }.toList()

    val waitingCustomers = mutableListOf<CustomerFX>()
    val processingCustomers = mutableListOf<CustomerFX>()

    fun arriveCustomer(customerFX: CustomerFX) {
        customerFX.moveToQueueIndex(waitingCustomers.size)
    }
    fun animate()  {

        val customerFxCache = mutableMapOf<Int,CustomerFX>()

        simulation.frames.asSequence().take(1).forEach { frame ->

            // handle customers leaving
            frame.departingCustomers.asSequence()
                    .map { customerFxCache[it.id]!! }
                    .forEach { it.leave() }

            // handle customer arrivals
            val arrivingCustomers = frame.arrivingCustomers.asSequence()
                    .mapIndexed { index, customer ->
                        customerFxCache.computeIfAbsent(customer.id) { CustomerFX(customer, waitingCustomers.size + index, this) }
                               .also { pane += it }
                    }.toList()

            // if no line, arriving customers go straight to desk
            arrivingCustomers.forEach {
                it.animateQueueIndexChange()
                if (it.customer in frame.servingCustomers && desks.any { it.currentCustomerFX == null }) {
                    it.moveToDesk(desks.shuffled().first { it.currentCustomerFX == null })
                    it.leave()
                }
            }


            // send serving customers to desk (if not already)
            val servingCustomers = frame.servingCustomers.asSequence()
                    .map { customerFxCache[it.id]!!}
                    .toList()

            servingCustomers.forEach { customerFx ->
                 val notAtDesk = desks.none { it.currentCustomerFX == customerFx  }

                 if (notAtDesk) {
                     val desk = desks.first { it.currentCustomerFX == null }
                     customerFx.moveToDesk(desk)
                 }
            }
        }
        animationQueue.play()

    }

    fun moveQueue() {
        waitingCustomers.forEach { it.moveUpQueue() }
    }
}

class CustomerFX(val customer: Customer, val startingIndex: Int, val simulationFX: SimulationFX): Circle() {

    private var currentIndex: Int = startingIndex.also { println(it) }
    private var currentTeller: TellerFX? = null

    init {
        radius = 10.0
        centerX = queueStartX + (startingIndex * 24.0) + 400.0
        centerY = edgeTop + lobbyHeight
        opacity = 0.0
        entrance()
    }

    fun entrance() {
        simulationFX.animationQueue += timeline(false) {
            keyframe(500.millis) {
                keyvalue(centerXProperty(), queueStartX + (currentIndex * 24.0))
                keyvalue(opacityProperty(),1.0)
            }
        }
    }

    fun moveUpQueue() {
        if (currentIndex > 0) currentIndex--
        animateQueueIndexChange()
    }

    fun animateQueueIndexChange() {
        simulationFX.animationQueue += timeline(false) {
            keyframe(300.millis) {
                keyvalue(centerXProperty(), queueStartX + (currentIndex * 24.0))
            }
        }
    }
    fun moveToQueueIndex(index: Int) {
        currentIndex = index
        animateQueueIndexChange()
    }

    fun moveToDesk(tellerFX: TellerFX) {
        currentTeller = tellerFX
        tellerFX.currentCustomerFX = this

        simulationFX.animationQueue += timeline(false) {
            keyframe(500.millis) {
                keyvalue(centerXProperty(), (currentTeller?.x?:0.0) + (deskWidth*.5))
                keyvalue(centerYProperty(), (currentTeller?.y?:0.0) + (deskHeight * 1.25))
            }
            simulationFX.waitingCustomers.asSequence()
                    .filter { it.customer.id > customer.id }
                    .forEach {
                        it.moveUpQueue()
                    }
        }
    }

    fun leave() {
        simulationFX.animationQueue += timeline(play=false)  {
            keyframe(1.seconds) {
                keyvalue(centerYProperty(), queueStartX + (startingIndex * 24.0) + (if (ThreadLocalRandom.current().nextInt(0,1) == 1) 200.0 else -200.0))
                keyvalue(centerXProperty(), edgeTop + lobbyHeight + 200)
                keyvalue(opacityProperty(), 0.0)
            }
        }
        currentTeller?.currentCustomerFX = null
    }
}

class TellerFX(val startingPosition: Int): Rectangle() {

    var currentCustomerFX: CustomerFX? = null

    init {
        width = deskWidth
        height = deskHeight
        fill = Color.BROWN
        y = edgeTop
        x = (startingPosition * 200.0) - radius
    }
}
