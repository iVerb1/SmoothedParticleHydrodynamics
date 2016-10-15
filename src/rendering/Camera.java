package rendering;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import particle.PhysicsSystem;
import particle.PhysicsObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public abstract class Camera {

    public PhysicsSystem physicsSystem;

    public boolean drawStandardParticles;
    public boolean drawFluidParticles;
    public boolean drawForces;
    public boolean drawConstraints;
    public boolean drawRigidBodies;

    public Camera(PhysicsSystem physicsSystem) {
        this.physicsSystem = physicsSystem;

        drawStandardParticles = true;
        drawFluidParticles = true;
        drawForces = true;
        drawConstraints = true;
        drawRigidBodies = true;
    }

    public abstract void initializeOpenGL();

    public abstract void update();

    public abstract void render();

    protected abstract void renderPicking();

    public int pick() {
        int x = Mouse.getX();
        int y = Mouse.getY();

        // The selection buffer
        IntBuffer selBuffer = ByteBuffer.allocateDirect(1024).order(ByteOrder.nativeOrder()).asIntBuffer();
        int buffer[] = new int[256];

        IntBuffer vpBuffer = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
        // The size of the viewport. [0] Is <x>, [1] Is <y>, [2] Is <width>, [3] Is <height>
        int[] viewport = new int[4];

        // The number of "hits" (objects within the pick area).
        int hits;
        // Get the viewport info
        glGetInteger(GL_VIEWPORT, vpBuffer);
        vpBuffer.get(viewport);

        // Set the buffer that OpenGL uses for selection to our buffer
        glSelectBuffer(selBuffer);

        // Change to selection mode
        glRenderMode(GL_SELECT);

        // Initialize the name stack (used for identifying which object was selected)
        glInitNames();
        glPushName(0);

        glMatrixMode(GL_PROJECTION);
        glPushMatrix();
        glLoadIdentity();

        /*  create 5x5 pixel picking region near cursor location */
        GLU.gluPickMatrix((float) x, (float) y, 1.0f, 1.0f, IntBuffer.wrap(viewport));

        renderPicking();

        glMatrixMode (GL_PROJECTION);
        glPopMatrix();
        glFlush();

        // Exit selection mode and return to render mode, returns number selected
        hits = glRenderMode(GL_RENDER);

        int picked = -1;

        selBuffer.get(buffer);
        // Objects Were Drawn Where The Mouse Was
        int depth = Integer.MAX_VALUE;
        if (hits > 0) {
            for (int i = 0; i < hits; i++) {
                int hitId = buffer[i * 4 + 3];
                int hitDepth = buffer[i * 4 + 1];

                // Loop Through All The Detected Hits
                // If This Object Is Closer To Us Than The One We Have Selected
                if (hitDepth <  depth) {
                    picked = hitId; // Select The Closer Object
                    depth = hitDepth; // Store How Far Away It Is
                }
            }
        }
        return picked;
    }

    public abstract Vector3f getCoordinatesAtMouse(PhysicsObject particle);

    public Vector3f getUpVector() {
        if (physicsSystem.is2D()) {
            return new Vector3f(0,1,0);
        }
        else if (physicsSystem.is3D()) {
            FloatBuffer modelMatrix = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
            glGetFloat(GL_MODELVIEW_MATRIX, modelMatrix);

            FloatBuffer projectionMatrix = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
            glGetFloat(GL_PROJECTION_MATRIX, projectionMatrix);

            IntBuffer viewport = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
            glGetInteger(GL_VIEWPORT, viewport);

            FloatBuffer objectPosition = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
            float[] pos = new float[3];

            GLU.gluUnProject(0, 0, 0, modelMatrix, projectionMatrix, viewport, objectPosition);

            objectPosition.get(pos);

            float[] pos2 = new float[3];

            GLU.gluUnProject(0, 1, 0, modelMatrix, projectionMatrix, viewport, objectPosition);

            objectPosition.get(pos2);

            return new Vector3f(pos2[0] - pos[0], pos2[1] - pos[1], pos2[2] - pos[2]);
        }
        else {
            throw new IllegalStateException("Cannot use this method in 1D");
        }
    }

    public Vector3f getRightVector() {
        if (physicsSystem.is2D()) {
            return new Vector3f(1,0,0);
        }
        else if (physicsSystem.is3D()) {
            FloatBuffer modelMatrix = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
            glGetFloat(GL_MODELVIEW_MATRIX, modelMatrix);

            FloatBuffer projectionMatrix = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
            glGetFloat(GL_PROJECTION_MATRIX, projectionMatrix);

            IntBuffer viewport = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder()).asIntBuffer();
            glGetInteger(GL_VIEWPORT, viewport);

            FloatBuffer objectPosition = ByteBuffer.allocateDirect(2048).order(ByteOrder.nativeOrder()).asFloatBuffer();
            float[] pos = new float[3];

            GLU.gluUnProject(0, 0, 0, modelMatrix, projectionMatrix, viewport, objectPosition);

            objectPosition.get(pos);

            float[] pos2 = new float[3];

            GLU.gluUnProject(1, 0, 0, modelMatrix, projectionMatrix, viewport, objectPosition);

            objectPosition.get(pos2);

            return new Vector3f(pos2[0] - pos[0], pos2[1] - pos[1], pos2[2] - pos[2]);
        }
        else {
            throw new IllegalStateException("Cannot use this method in 1D");
        }
    }

}