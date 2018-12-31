import javafx.animation.SequentialTransition
import javafx.animation.Timeline
import javafx.application.Application
import javafx.scene.shape.Circle
import tornadofx.*

fun main(args:Array<String>) = Application.launch(AnimationApp::class.java, *args)

class AnimationApp: App(AnimationView::class)

class AnimationView: View() {

    val animationQueue = SequentialTransition()
    operator fun SequentialTransition.plusAssign(timeline: Timeline) { children.add(timeline) }

    override val root = borderpane {

        primaryStage.isMaximized=true

        left = toolbar {
            button("Add to Queue")

        }
        center = pane {

            // initialize queue of circles
            val circles = (0 until 10).map { i ->
                circle(radius=10.0) {
                    centerX = i * 24.0
                    centerY = 100.0
                }
            }.toList()

            // animate a moving queue
            for ((i,c) in circles.withIndex()) {

                animationQueue += timeline(play=false)  {
                    keyframe(2.seconds) {
                        keyvalue(c.centerYProperty(), 300.0)
                    }
                }
                circles.filterIndexed { index, circle -> index > i }.forEachIndexed { index, circle ->
                    animationQueue += timeline(play=false) {
                        keyframe(300.millis) {
                            keyvalue(circle.centerXProperty(), index * 24.0)
                        }
                    }
                }
            }
            animationQueue.play()
        }
    }
}

class CustomerCircle(val startingPosition: Int): Circle() {

}