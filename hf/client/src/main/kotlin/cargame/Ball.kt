import vision.gears.webglmath.*
import kotlin.math.cos
import kotlin.math.sin

class Ball(_position : Vec3, _radius : Float, vararg meshes : Mesh) : GameObject(*meshes) {
    val b = this
    var moving = false

    init {
        val r = _radius
        val p = _position + Vec3(0.0f, r, 0.0f)

        scale = Vec3(r, r, r)
        position = p
        collider = Collider(p, r)
        body = Body(0.1f, 0.1f)
        body.position = p
        body.resistance = 1.0f

        needsShadow = true

        move = object : GameObject.Motion() {
            override operator fun invoke(
            dt : Float,
            t : Float,
            keysPressed : Set<String>,
            gameObjects : List<GameObject>
            ) : Boolean {
                for(gameObject in gameObjects){
                    if(gameObject === b)
                        continue

                    val v = collider.collidesWith(gameObject.collider)
                    if(v.x != 0.0f) 
                        body.velocity = 5.0f * v
                }

                val vel = body.velocity
                if(vel.x != 0.0f) {
                    val a = Vec3(0.0f, 1.0f, 0.0f).cross(vel)
                    angle = a.length()
                    axis = a.normalize()
                } 

                body.slowDown(dt)
                body.update(dt)

                position = body.position
                collider.position = body.position

                return true
            }
        }
    }
}