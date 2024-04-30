import vision.gears.webglmath.UniformProvider
import vision.gears.webglmath.Vec2
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Mat4
import kotlin.math.tan
import kotlin.math.PI
import org.w3c.dom.events.*

class PerspectiveCamera(vararg programs : Program) : UniformProvider("camera") {

  var position by Vec3(0.0f, 0.0f, 1.0f) 
  var roll = 0.0f
  var pitch = 0.0f
  var yaw = 0.0f

  var fov = 1.0f 
  var aspect = 1.0f
  var nearPlane = 0.1f
  var farPlane = 1000.0f 

  var ahead = Vec3(0.0f, 0.0f,-1.0f) 
  var right = Vec3(1.0f, 0.0f, 0.0f) 
  var up    = Vec3(0.0f, 1.0f, 0.0f)  

  var speed = 10.5f
  var isDragging = false
  val mouseDelta = Vec2(0.0f, 0.0f) 

  val rotationMatrix = Mat4()
  val viewProjMatrix by Mat4()
  var rayDirMatrix by Mat4()

  companion object {
    val worldUp = Vec3(0.0f, 1.0f, 0.0f)
  }

  init {
    update()
    addComponentsAndGatherUniforms(*programs)
  }
  
  fun update() { 
    rotationMatrix.set(). 
      rotate(roll).
      rotate(pitch, 1.0f, 0.0f, 0.0f).
      rotate(yaw, 0.0f, 1.0f, 0.0f)
    viewProjMatrix.set(rotationMatrix).
      translate(position).
      invert()

    val yScale = 1.0f / tan(fov * 0.5f) 
    val xScale = yScale / aspect
    val f = farPlane 
    val n = nearPlane 
    viewProjMatrix *= Mat4( 
        xScale ,    0.0f ,         0.0f ,   0.0f, 
          0.0f ,  yScale ,         0.0f ,   0.0f, 
          0.0f ,    0.0f ,  (n+f)/(n-f) ,  -1.0f, 
          0.0f ,    0.0f ,  2*n*f/(n-f) ,   0.0f)

    //LABTODO: rayDirMatrix
    val E = Mat4( 
          1.0f , 0.0f ,  0.0f ,  -position.x, 
          0.0f , 1.0f ,  0.0f ,  -position.y, 
          0.0f , 0.0f ,  1.0f ,  -position.z, 
          0.0f , 0.0f ,  0.0f ,  1.0f)

    viewProjMatrix.invert()
    val VPI = viewProjMatrix
    rayDirMatrix = VPI * E
    viewProjMatrix.invert()
  }

  fun setAspectRatio(ar : Float) { 
    aspect = ar
    update()
  } 

  fun move(car : Car, keysPressed : Set<String>) { 
    position = car.position + Vec3(0.0f, 10.0f, 0.0f)
    position = (position - car.calculateDirection() * 40.0f)
    yaw = -car.angle + PI.toFloat()

    if(keysPressed.contains("e"))
      fov += 0.01f

    if(keysPressed.contains("q"))
      fov -= 0.01f

    update()
    ahead = (Vec3(0.0f, 0.0f, -1.0f).xyz0 * rotationMatrix).xyz
    right = (Vec3(1.0f, 0.0f,  0.0f).xyz0 * rotationMatrix).xyz
    up    = (Vec3(0.0f, 1.0f,  0.0f).xyz0 * rotationMatrix).xyz    
  } 
  
  fun mouseDown() { 
    isDragging = true
    mouseDelta.set() 
  } 

  fun mouseMove(event : MouseEvent) { 
    mouseDelta.x += event.asDynamic().movementX as Float
    mouseDelta.y += event.asDynamic().movementY as Float
    event.preventDefault()
  } 
  fun mouseUp() { 
    isDragging = false
  }  
}