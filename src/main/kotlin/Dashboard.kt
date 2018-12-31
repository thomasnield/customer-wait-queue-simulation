import javafx.animation.SequentialTransition
import javafx.animation.Timeline
import javafx.application.Application
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import tornadofx.*

fun main(args:Array<String>) = Application.launch(AnimationApp::class.java, *args)

class AnimationApp: App(AnimationView::class)


val edgeLeft = 500.0

class AnimationView: View() {

    val animationQueue = SequentialTransition()
    operator fun SequentialTransition.plusAssign(timeline: Timeline) { children.add(timeline) }

    override val root = borderpane {

        primaryStage.isMaximized=true

        left = toolbar {
            button("Add to Queue")

        }
        center = pane {

            // initialize desks
            val desks = (1 until 10).map {
                TellerFX(it).also { this += it }
            }.toList()

            // initialize queue of circles
            val circles = (0 until 100).map {
                CustomerFX(it).also { this += it }
            }.toList()

            // animate a moving queue
            for ((i,c) in circles.withIndex()) {

                animationQueue += timeline(play=false)  {
                    keyframe(1.seconds) {
                        val randomDesk = desks.shuffled().first()
                        randomDesk.currentCustomerFX?.let {
                            keyvalue(it.centerYProperty(), 400.0)
                            keyvalue(it.centerXProperty(), 100.0)
                            keyvalue(it.opacityProperty(),0.0)
                        }
                        randomDesk.currentCustomerFX = c
                        keyvalue(c.centerXProperty(), randomDesk.x + 30.0)
                        keyvalue(c.centerYProperty(), randomDesk.y + 30.0)
                    }
                }
                animationQueue += timeline(play=false) {

                circles.filterIndexed { index, circle -> index > i }.forEachIndexed { index, circle ->
                        keyframe(100.millis) {
                            keyvalue(circle.centerXProperty(), edgeLeft + index * 24.0)
                        }
                    }
                }
            }
            animationQueue.play()
        }
    }
}

class CustomerFX(val startingPosition: Int): Circle() {

    init {
        radius = 10.0
        centerX = edgeLeft + startingPosition * 24.0
        centerY = 500.0
    }
}

class TellerFX(val startingPosition: Int): Rectangle() {
    var currentCustomerFX: CustomerFX? = null

    fun removeCustomer() {
        currentCustomerFX?.isVisible = false
        currentCustomerFX = null
    }
    fun setCustomer(customerFX: CustomerFX) {
        currentCustomerFX = customerFX
    }
    init {
        width = 60.0
        height = 30.0
        fill = Color.BROWN
        y = 300.0
        x = startingPosition * 200.0
    }
}