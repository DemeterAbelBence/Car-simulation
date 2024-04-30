import vision.gears.webglmath.*
import kotlin.math.exp
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.floor

open class GameObject(
  vararg val meshes : Mesh
   ) : UniformProvider("gameObject") {

  var position = Vec3(0.0f, 0.0f, 0.0f)
  var scale = Vec3(1.0f, 1.0f, 1.0f)
  var axis = Vec3(0.0f, 1.0f, 0.0f)
  var angle = 0.0f

  var collider = Collider(Vec3(0.0f), 0.0f)
  var body = Body(0.0f, 0.0f)

  var needsShadow = false

  var modelMatrix by Mat4()

  var parent : GameObject? = null

  init { 
    addComponentsAndGatherUniforms(*meshes)
  }

  fun update() {
    val T = Mat4 (
      1.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 1.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 1.0f, 0.0f,
      position.x, position.y, position.z, 1.0f
    )
    val S = Mat4 (
      scale.x, 0.0f, 0.0f, 0.0f,
      0.0f, scale.y, 0.0f, 0.0f,
      0.0f, 0.0f, scale.z, 0.0f,
      0.0f, 0.0f, 0.0f, 1.0f
    )
    val R = Mat4().rotate(-angle, axis.x, axis.y, axis.z)

    modelMatrix = S * R * T

    /*parent?.let{ parent -> 
      modelMatrix *= parent.modelMatrix
    }*/
  }

  open class Motion {
    open operator fun invoke(
        dt : Float = 0.016666f,
        t : Float = 0.0f,
        keysPressed : Set<String> = emptySet<String>(),
        gameObjects : List<GameObject> = emptyList<GameObject>()
        ) : Boolean {
      return true;
    }
  }
  var move = Motion()

}
