import org.w3c.dom.HTMLCanvasElement
import org.khronos.webgl.WebGLRenderingContext as GL //# GL# we need this for the constants declared ˙HUN˙ a constansok miatt kell
import kotlin.js.Date
import vision.gears.webglmath.UniformProvider
import vision.gears.webglmath.Vec1
import vision.gears.webglmath.Vec2
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Vec4
import vision.gears.webglmath.Mat4
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos

class Scene (
  val gl : WebGL2RenderingContext)  : UniformProvider("scene") {

  val vsTextured = Shader(gl, GL.VERTEX_SHADER, "textured-vs.glsl")
  val fsTextured = Shader(gl, GL.FRAGMENT_SHADER, "textured-fs.glsl")
  val texturedProgram = Program(gl, vsTextured, fsTextured)
  
  val vsEnv = Shader(gl, GL.VERTEX_SHADER, "env-vs.glsl")
  val fsEnv = Shader(gl, GL.FRAGMENT_SHADER, "env-fs.glsl")
  val envProgram = Program(gl, vsEnv, fsEnv)

  val vsShadow = Shader(gl, GL.VERTEX_SHADER, "shadow-vs.glsl")
  val fsShadow = Shader(gl, GL.FRAGMENT_SHADER, "shadow-fs.glsl")
  val shadowProgram = Program(gl, vsShadow, fsShadow)

  var shadowMatrix by Mat4()

  val shadowMaterial = Material(shadowProgram)

  val jsonLoader = JsonLoader()
  val avatarMeshes = jsonLoader.loadMeshes(gl,
    "media/json/chevy/chassis.json",
    Material(texturedProgram).apply{
      this["colorTexture"]?.set(
          Texture2D(gl, "media/json/chevy/chevy.png"))
    }
  )
  var avatar = Car(*avatarMeshes).apply { 
    position = Vec3(0.0f, 1.0f, 0.0f)
  }


  val ballMesh = jsonLoader.loadMeshes(gl,
    "media/json/sphere.json",
    Material(texturedProgram).apply{
      this["colorTexture"]?.set(
          Texture2D(gl, "media/football.jpg"))
    }
  ) 

  val texturedQuadGeometry = TexturedQuadGeometry(gl)
  val envTexture = TextureCube(gl, 
      "media/posx512.jpg",
      "media/negx512.jpg",
      "media/posy512.jpg",
      "media/negy512.jpg",
      "media/posz512.jpg",
      "media/negz512.jpg"
      )
  val envMaterial = Material(envProgram).apply {
    this["envTexture"]?.set(envTexture)
  }
  val envMesh = Mesh(envMaterial, texturedQuadGeometry)

  val groundGeometry = GroundQuadGeometry(gl)
  val groundMaterial = Material(texturedProgram).apply {
      this["colorTexture"]?.set(
          Texture2D(gl, "media/grass.jpg"))
  }
  var ground = GameObject(Mesh(groundMaterial, groundGeometry)).apply {
    scale = Vec3(1000.0f, 0.0f, 1000.0f)
  }

  val gameObjects = ArrayList<GameObject>()

  init {
    gameObjects += ground
    gameObjects += avatar
    gameObjects += Ball(Vec3(50.0f, 0.0f, 50.0f), 50.0f, *ballMesh)
    gameObjects += Ball(Vec3(50.0f, 0.0f, -30.0f), 20.0f, *ballMesh)
    gameObjects += Ball(Vec3(-60.0f, 0.0f, 50.0f), 30.0f, *ballMesh)
    gameObjects += GameObject(envMesh)

    val l = Vec3(1.0f, -0.5f, 0.0f).normalize()

    shadowMatrix = Mat4(
      1.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 0.0f, 0.0f,
      0.0f, 0.0f, 1.0f, 0.0f,
      l.x, 0.01f, l.z, 1.0f,
    )
  }

  val camera = PerspectiveCamera(*Program.all)

  fun resize(canvas : HTMLCanvasElement) {
    gl.viewport(0, 0, canvas.width, canvas.height)
    camera.setAspectRatio(canvas.width.toFloat()/canvas.height)
  }

  val timeAtFirstFrame = Date().getTime()
  var timeAtLastFrame =  timeAtFirstFrame

  init{
    gl.enable(GL.DEPTH_TEST)
    addComponentsAndGatherUniforms(*Program.all)
  }

  @Suppress("UNUSED_PARAMETER")
  fun update(keysPressed : Set<String>) {
    val timeAtThisFrame = Date().getTime() 
    val dt = (timeAtThisFrame - timeAtLastFrame).toFloat() / 1000.0f
    val t = (timeAtThisFrame - timeAtFirstFrame).toFloat() / 1000.0f
    timeAtLastFrame = timeAtThisFrame

    gl.clearColor(0.3f, 0.0f, 0.3f, 1.0f)
    gl.clearDepth(1.0f)
    gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)

    gl.enable(GL.BLEND)
    gl.blendFunc(
      GL.SRC_ALPHA,
      GL.ONE_MINUS_SRC_ALPHA)

    camera.move(avatar, keysPressed)

    gameObjects.forEach{ it.move(dt, t, keysPressed, gameObjects) }

    gameObjects.forEach{ it.update() }
    gameObjects.forEach{ it.draw(this, camera) }

    gameObjects.forEach {
      if(it.needsShadow) { 
        it.using(shadowMaterial).draw(this, this.camera);
      }
    }
  }
}
