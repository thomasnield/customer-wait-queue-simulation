import org.ojalgo.matrix.PrimitiveMatrix
import org.ojalgo.okalgo.populate
import org.ojalgo.okalgo.primitivematrix
import org.ojalgo.okalgo.times

/**
 * x + y = 240
 * 2x + y = 320
 *
 * Ax = b
 * x = A`b
 */

fun main(args: Array<String>) {

    val a = primitivematrix(2,3) {
        populate(
                1.0, 1.0, 10.0,
                2.0, 1.0, 20
        )
    }

    val b = primitivematrix(2,1) {
        populate(
                240.0,
                320.0
        )
    }

    println(a.invert() * b)
}
