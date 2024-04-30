import vision.gears.webglmath.*
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.PI

class Car(vararg meshes : Mesh) : GameObject(*meshes) {
    val move_speed = 5.0f;
    val turn_speed = 6.0f;

    val offset = Vec3(0.0f, 2.0f, 0.0f);

    init {
        body = Body(0.1f, 0.1f)
        body.resistance = 3.0f
        collider = Collider(position, 10.0f)

        move = object : GameObject.Motion() {
            override operator fun invoke(
            dt : Float,
            t : Float,
            keysPressed : Set<String>,
            gameObjects : List<GameObject>
            ) : Boolean {
                val dir = move_speed * calculateDirection()
                val torque = turn_speed * body.velocity.length()

                var move : Boolean = false
                var turn : Boolean = false

                if(keysPressed.contains("w")) {
                    body.exertForce(dir)
                    move = true
                }

                if(keysPressed.contains("s")) {
                    body.exertForce(-dir)
                    move = true
                }

                if(keysPressed.contains("a")) {
                    body.exertRotation(Vec3(0.0f, -torque, 0.0f))
                    turn = true
                }

                if(keysPressed.contains("d")) {
                    body.exertRotation(Vec3(0.0f, torque, 0.0f))
                    turn = true
                }

                if(!move)
                    body.slowDown(dt)

                if(!turn)
                    body.torque = Vec3(0.0f)
                    
                body.update(dt)
                angle = body.orientation.y
                position = (body.position + offset)
                collider.position = position

                return true
            }
        }
    }

    fun calculateDirection() : Vec3 {
        val a = angle + PI.toFloat() / 2.0f
        var dir = Vec3(cos(a), 0.0f, sin(a))
        return dir.normalize()
    }
}