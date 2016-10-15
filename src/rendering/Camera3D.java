package rendering;

import particle.PhysicsObject;
import particle.StandardParticle;
import particle.rigid.RigidBody;
import particle.sph.HydroParticle;
import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import particle.PhysicsSystem;
import particle.State;
import particle.constraint.Constraint;
import particle.force.Force;
import rendering.mesh.Mesh3D;
import rendering.mesh.Model;
import rendering.shade3D.LightSource;
import rendering.shade3D.LightSourceManager;
import picking.picking3D.MouseRay;
import util.Indexable;
import util.Time;
import util.Util;
import view.MainForm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static rendering.shade3D.ShaderUtils.loadShader;
import static rendering.shade3D.ShaderUtils.loadShaderProgram;
import static util.Util.asFloatBuffer;

/**
 * Created by iVerb on 23-5-2015.
 */
public class Camera3D extends Camera {

    public Vector3f position = new Vector3f(0f, 0f, 15f);
    public Vector3f rotation = new Vector3f();

    private List<Mesh3D> worldEntities;

    private float[] rayStart = new float[3];
    private float[] rayEnd = new float[3];

    private float MOUSE_SENSITIVITY = 0.2f;
    private float PITCH_MAX = 90;
    private float PITCH_MIN = -90;

    private float MOVEMENT_SPEED = 10f;
    private float MOVEMENT_SPEED_MULTIPLIER_FAST = 3f;
    private float MOVEMENT_SPEED_MULTIPLIER_SLOW = 0.3f;

    private MouseRay mouseRay;
    private Vector3f horDir;
    private Vector3f verDir;

    private int ACTION_KEY_MOVE_FORWARDS = Keyboard.KEY_W;
    private int ACTION_KEY_MOVE_BACKWARDS = Keyboard.KEY_S;
    private int ACTION_KEY_STRAFE_LEFT = Keyboard.KEY_D;
    private int ACTION_KEY_STRAFE_RIGHT = Keyboard.KEY_A;
    private int ACTION_KEY_ASCEND = Keyboard.KEY_LSHIFT;
    private int ACTION_KEY_DESCEND = Keyboard.KEY_SPACE;
    private int ACTION_KEY_MOVE_FAST = Keyboard.KEY_E;
    private int ACTION_KEY_MOVE_SLOW = Keyboard.KEY_Q;

    private float FOV_Y = 45.0f;
    private float Z_NEAR = 0.1f;
    private float Z_FAR = 300.0f;

    private String VERTEX_SHADER_LOCATION = "resources/shaders/phongShader.vert";
    private String FRAGMENT_SHADER_LOCATION = "resources/shaders/phongShader.frag";
    private static int shaderProgram = 0;

    private float[] CLEAR_COLOR = new float[] {0.05f, 0.05f, 0.05f, 1f};
    private float[] AMBIENT_LIGHT_RGBA = new float[] {0.25f, 0.25f, 0.25f, 1f};
    public static float LIGHT_ATTENUATION = 0f;


    public Camera3D(PhysicsSystem physicsSystem) {
        super(physicsSystem);
    }

    @Override
    public void initializeOpenGL() {
        initBasic();
        initShaders();
        initLighting();
        initWorld();
    }

    private void initWorld() {
        Model.recreateAllVBOs();

        Mesh3D plane = new Mesh3D("plane", new Vector3f(0f, -5f, 0f));
        plane.scalingFactor = 10f;
        plane.material.specularColor = new Vector4f(0f, 0f, 0f, 1f);
        plane.material.shininess = 0f;

        LightSource light = new LightSource(new Vector3f(0f, 10f, 5f));
        light.doDraw = false;

        worldEntities = Arrays.asList(plane, light);
        LightSourceManager.setLightSources(light);
    }

    private void initBasic() {
        glClearDepth(1.0f); // clear depth buffer
        glEnable(GL_DEPTH_TEST); // Enables depth testing
        glDepthFunc(GL_LEQUAL); // sets the type of test to use for depth
        glMatrixMode(GL_PROJECTION); // sets the matrix mode to project

        setPerspective();

        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

        //clear color
        glClearColor(CLEAR_COLOR[0],CLEAR_COLOR[1], CLEAR_COLOR[2], CLEAR_COLOR[3]);
    }

    private void setPerspective() {
        GLU.gluPerspective(FOV_Y, ((float) MainForm.VIEWPORT_WIDTH) / (float) MainForm.VIEWPORT_HEIGHT, Z_NEAR, Z_FAR);
    }

    private void initLighting() {
        glEnable(GL_LIGHTING);
        glEnable(GL_COLOR_MATERIAL);
        glLightModel(GL_LIGHT_MODEL_AMBIENT, asFloatBuffer(AMBIENT_LIGHT_RGBA));
    }

    private void initShaders() {
        glShadeModel(GL_SMOOTH);

        shaderProgram = loadShaderProgram(
                loadShader(VERTEX_SHADER_LOCATION, GL_VERTEX_SHADER),
                loadShader(FRAGMENT_SHADER_LOCATION, GL_FRAGMENT_SHADER));
        glUseProgram(shaderProgram);
    }

    @Override
    public void update() {
        float movementSpeed = MOVEMENT_SPEED;

        float[] origin = {rayStart[0], rayStart[1], rayStart[2]};
        float[] direction = {rayEnd[0] - rayStart[0], rayEnd[1] - rayStart[1], rayEnd[2] - rayStart[2]};

        if (Keyboard.isKeyDown(ACTION_KEY_MOVE_FAST)) {
            movementSpeed *= MOVEMENT_SPEED_MULTIPLIER_FAST;
        }

        if (Keyboard.isKeyDown(ACTION_KEY_MOVE_SLOW)) {
            movementSpeed *= MOVEMENT_SPEED_MULTIPLIER_SLOW;
        }

        if (Mouse.isGrabbed()) {
            this.rotation.x = Math.max(Math.min(this.rotation.x - Mouse.getDY() * this.MOUSE_SENSITIVITY, this.PITCH_MAX), this.PITCH_MIN);
            this.rotation.y += Mouse.getDX() * this.MOUSE_SENSITIVITY;
        }

        if (Keyboard.isKeyDown(ACTION_KEY_MOVE_FORWARDS)) {
            double xzPlaneAngle = Math.toRadians(this.rotation.y);

            this.position.z -= (float) Math.cos(xzPlaneAngle) * movementSpeed * Time.getDelta();
            this.position.x += (float) Math.sin(xzPlaneAngle) * movementSpeed * Time.getDelta();
        }

        if (Keyboard.isKeyDown(ACTION_KEY_MOVE_BACKWARDS)) {
            double xzPlaneAngle = Math.toRadians(this.rotation.y);

            this.position.z += (float) Math.cos(xzPlaneAngle) * movementSpeed * Time.getDelta();
            this.position.x -= (float) Math.sin(xzPlaneAngle) * movementSpeed * Time.getDelta();
        }

        if (Keyboard.isKeyDown(ACTION_KEY_STRAFE_LEFT)) {
            double xzPlaneAngle = Math.toRadians(this.rotation.y + 90);

            this.position.z -= (float) Math.cos(xzPlaneAngle) * movementSpeed * Time.getDelta();
            this.position.x += (float) Math.sin(xzPlaneAngle) * movementSpeed * Time.getDelta();
        }

        if (Keyboard.isKeyDown(ACTION_KEY_STRAFE_RIGHT)) {
            double xzPlaneAngle = Math.toRadians(this.rotation.y - 90);

            this.position.z -= (float) Math.cos(xzPlaneAngle) * movementSpeed * Time.getDelta();
            this.position.x += (float) Math.sin(xzPlaneAngle) * movementSpeed * Time.getDelta();
        }

        if (Keyboard.isKeyDown(ACTION_KEY_ASCEND)) {
            this.position.y -= movementSpeed * Time.getDelta();
        }

        if (Keyboard.isKeyDown(ACTION_KEY_DESCEND)) {
            this.position.y += movementSpeed * Time.getDelta();
        }

        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_C) {
                    Mouse.setGrabbed(!Mouse.isGrabbed());
                }

                if (Keyboard.getEventKey() == Keyboard.KEY_K) {
                    mouseRay = this.getMouseRay();
                    horDir = this.getRightVector();
                    verDir = this.getUpVector();
                }
            }
        }
    }


    protected void prepareRendering() {
        glMatrixMode(GL_MODELVIEW);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glLoadIdentity();

        glRotatef(this.rotation.x, 1, 0, 0);
        glRotatef(this.rotation.y, 0, 1, 0);
        glTranslatef(-this.position.x, -this.position.y, -this.position.z);

        glEnableClientState(GL_VERTEX_ARRAY);
    }

    protected void finalizeRendering() {
        glDisableClientState(GL_VERTEX_ARRAY);
    }

    public static void prepareShading() {
        LightSourceManager.enableLights();
        glUseProgram(shaderProgram);
        glEnableClientState(GL_NORMAL_ARRAY);
    }

    public static void finalizeShading() {
        LightSourceManager.disableLights();
        glUseProgram(0);
        glDisableClientState(GL_NORMAL_ARRAY);
    }

    @Override
    public void render() {
        prepareRendering();
        prepareShading();

        for (Mesh3D e : worldEntities) {
            e.render();
        }

        if (drawStandardParticles) {
            drawStandardParticles();
        }

        if (drawFluidParticles) {
            drawFluidParticles();
        }

        finalizeShading();

        if (drawForces) {
            drawForces();
        }

        if (drawConstraints) {
            drawConstraints();
        }

        if (drawRigidBodies) {
            for (RigidBody r : physicsSystem.objects.rigid) {
                r.render(physicsSystem.currentState);
            }
        }

        if (mouseRay != null) {
            glLineWidth(2.5f);
            glColor3f(1.0f, 1.0f, 1.0f);
            glBegin(GL_LINES);
            glVertex3f(mouseRay.origin.x, mouseRay.origin.y, mouseRay.origin.z);
            glVertex3f(mouseRay.origin.x + mouseRay.direction.x, mouseRay.origin.y + mouseRay.direction.y, mouseRay.origin.z + mouseRay.direction.z);
            glEnd();
            glBegin(GL_LINES);
            glVertex3f(mouseRay.origin.x, mouseRay.origin.y, mouseRay.origin.z);
            glVertex3f(mouseRay.origin.x + 100*horDir.x, mouseRay.origin.y + 100*horDir.y, mouseRay.origin.z + 100 * horDir.z);
            glEnd();
            glBegin(GL_LINES);
            glVertex3f(mouseRay.origin.x, mouseRay.origin.y, mouseRay.origin.z);
            glVertex3f(mouseRay.origin.x + 100*verDir.x, mouseRay.origin.y + 100*verDir.y, mouseRay.origin.z + 100*verDir.z);
            glEnd();
        }

        finalizeRendering();
    }

    @Override
    protected void renderPicking() {
        setPerspective();
        prepareRendering();

        State s = physicsSystem.currentState;
        for (StandardParticle p : physicsSystem.objects.standard) {
            glLoadName(p.getIndex(Indexable.PICKING_INDEX));
            glPushMatrix();
            p.render(s);
            glPopMatrix();
        }

        for (RigidBody rb : physicsSystem.objects.rigid) {
            glLoadName(rb.getIndex(Indexable.PICKING_INDEX));
            glPushMatrix();
            rb.render(s);
            glPopMatrix();
        }

        finalizeRendering();
    }

    protected void drawForces() {
        State s = physicsSystem.currentState;
        for (Force f : physicsSystem.forces) {
            glPushMatrix();
            f.render(s);
            glPopMatrix();
        }
    }

    protected void drawConstraints() {
        State s = physicsSystem.currentState;
        for (Constraint c : physicsSystem.constraints) {
            glPushMatrix();
            c.render(s);
            glPopMatrix();
        }
    }

    protected void drawStandardParticles() {
        State s = physicsSystem.currentState;
        for (StandardParticle p : physicsSystem.objects.standard) {
            glPushMatrix();
            p.render(s);
            glPopMatrix();
        }
    }

    protected void drawFluidParticles() {
        State s = physicsSystem.currentState;
        for (HydroParticle p : physicsSystem.objects.hydro) {
            glPushMatrix();
            p.render(s);
            glPopMatrix();
        }
    }

    public MouseRay getMouseRay() {
        FloatBuffer modelMatrix = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
        glGetFloat(GL_MODELVIEW_MATRIX, modelMatrix);

        FloatBuffer projectionMatrix = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
        glGetFloat(GL_PROJECTION_MATRIX, projectionMatrix);

        IntBuffer viewport = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
        glGetInteger(GL_VIEWPORT, viewport);

        FloatBuffer objectPosition = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
        float[] pos = new float[3];

        GLU.gluUnProject(Mouse.getX(), Mouse.getY(), 0, modelMatrix, projectionMatrix, viewport, objectPosition);

        objectPosition.get(pos);

        float[] pos2 = new float[3];

        GLU.gluUnProject(Mouse.getX(), Mouse.getY(), 1, modelMatrix, projectionMatrix, viewport, objectPosition);

        objectPosition.get(pos2);

        return new MouseRay(new Vector3f(pos[0], pos[1], pos[2]), new Vector3f(pos2[0] - pos[0], pos2[1] - pos[1], pos2[2] - pos[2]));
    }

    @Override
    public Vector3f getCoordinatesAtMouse(PhysicsObject particle) {
        RealVector particlePosition = particle.getPosition(physicsSystem.currentState);
        MouseRay mouseRay = getMouseRay();
        return Util.rayPlaneIntersection(mouseRay.origin, mouseRay.direction, (Vector3f)Util.toVector(particlePosition), getRightVector(), getUpVector());
    }

}
	