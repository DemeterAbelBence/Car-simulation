import vision.gears.webglmath.*
import kotlin.math.exp

class Body(_inverseMass : Float, _angularMass : Float) {
    var position : Vec3
    var inverseMass : Float
    var acceleration : Vec3
    var velocity : Vec3 

    var orientation : Vec3
    var angularMass : Float
    var angularVelocity : Vec3

    var force : Vec3
    var torque : Vec3

    var resistance : Float = 1.0f

    init {
        inverseMass = _inverseMass
        angularMass = _angularMass

        position = Vec3(0.0f)
        acceleration = Vec3(0.0f)
        velocity = Vec3(0.0f)

        orientation = Vec3(0.0f)
        angularVelocity = Vec3(0.0f)

        force = Vec3(0.0f)
        torque = Vec3(0.0f)
    }

    fun calmState() {
        acceleration = Vec3(0.0f)
        velocity = Vec3(0.0f)

        angularVelocity = Vec3(0.0f)

        force = Vec3(0.0f)
        torque = Vec3(0.0f)
    }

    fun exertForce(f : Vec3) {
        force = f
    }

    fun exertRotation(t : Vec3) {
        torque = t
    }

    fun update(dt : Float) {
        acceleration = force * inverseMass * dt
        velocity = velocity + acceleration
        position = position + velocity

        angularVelocity = torque * angularMass * dt
        orientation = orientation + angularVelocity
    }

    fun slowDown(dt : Float) {
        velocity = velocity * exp(-dt * resistance)
        force = Vec3(0.0f, 0.0f, 0.0f)
    }
}