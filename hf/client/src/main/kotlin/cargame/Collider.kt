import vision.gears.webglmath.*
import kotlin.math.abs

class Collider(_position : Vec3, _radius : Float) {
    var position = Vec3(0.0f, 0.0f, 0.0f)
    var radius = 0.0f

    init {
        position = _position
        radius = _radius
    }

    fun collidesWith(collider : Collider) : Vec3 {
        val d = position - collider.position

        if(radius == 0.0f || collider.radius == 0.0f)
            return Vec3(0.0f, 0.0f, 0.0f)

        if(d.length() < abs(radius + collider.radius))
            return Vec3(d.x, 0.0f, d.z).normalize()
        else
            return Vec3(0.0f, 0.0f, 0.0f)
    }
}