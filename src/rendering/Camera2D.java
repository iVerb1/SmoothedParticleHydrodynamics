package rendering;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.vector.Vector3f;
import particle.PhysicsSystem;
import particle.PhysicsObject;
import particle.StandardParticle;
import particle.State;
import particle.constraint.Constraint;
import particle.force.Force;
import particle.rigid.RigidBody;
import particle.sph.HydroParticle;
import util.Indexable;
import view.MainForm;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by iVerb on 23-5-2015.
 */
public class Camera2D extends Camera {

    float worldWidth = 16.0f;
    float worldHeight = 9.0f;

    public float STANDARD_PARTICLE_SIZE;
    public float HYDRO_PARTICLE_SIZE;
    public Vector3f STANDARD_PARTICLE_COLOR;
    public Vector3f HYDRO_PARTICLE_COLOR;
    public Vector3f RIGID_BODY_COLOR;


    public Camera2D(PhysicsSystem physicsSystem) {
        super(physicsSystem);

        STANDARD_PARTICLE_SIZE = 0.13f;
        HYDRO_PARTICLE_SIZE = 0.08f;
        STANDARD_PARTICLE_COLOR = new Vector3f(1f, 0f, 0f);
        HYDRO_PARTICLE_COLOR = new Vector3f(0f, 0f, 1f);
        RIGID_BODY_COLOR = new Vector3f(0f, 1f, 0f);
    }

    @Override
    public void initializeOpenGL() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, worldWidth, 0, worldHeight, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glTranslated(worldWidth / 2, worldHeight / 2, 0.0);
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {
        // Clear the screen and depth buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        State currentState = physicsSystem.currentState;

        if (drawFluidParticles) {
            drawFluidParticles();
        }

        if (drawStandardParticles) {
            drawStandardParticles();
        }

        if (drawConstraints) {
            for (Constraint c : physicsSystem.constraints.values()) {
                c.render(currentState);
            }
        }

        if (drawForces) {
            for (Force f : physicsSystem.forces) {
                f.render(currentState);
            }
        }

        if (drawRigidBodies) {
            for (RigidBody r : physicsSystem.objects.rigid) {
                r.render(physicsSystem.currentState);
            }
        }
    }

    @Override
    protected void renderPicking() {
        glOrtho(0, 16, 0, 9, 1, -1);

        // Clear the screen and depth buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        State currentState = physicsSystem.currentState;

        for (StandardParticle p : physicsSystem.objects.standard) {
            glLoadName(p.getIndex(Indexable.PICKING_INDEX));
            glPushMatrix();
            p.render(currentState);
            glPopMatrix();
        }

        for (RigidBody rb : physicsSystem.objects.rigid) {
            glLoadName(rb.getIndex(Indexable.PICKING_INDEX));
            glPushMatrix();
            rb.render(currentState);
            glPopMatrix();
        }
    }

    protected void drawFluidParticles() {
        State currentState = physicsSystem.currentState;
        for (HydroParticle p : physicsSystem.objects.hydro) {
            glPushMatrix();
            p.render(currentState);
            glPopMatrix();
        }
    }

    private void drawStandardParticles() {
        State currentState = physicsSystem.currentState;
        for (StandardParticle p : physicsSystem.objects.standard) {
            glPushMatrix();
            p.render(currentState);
            glPopMatrix();
        }
    }

    @Override
    public Vector3f getCoordinatesAtMouse(PhysicsObject particle) {
        float x = (worldWidth * (((float)Mouse.getX()) / MainForm.VIEWPORT_WIDTH)) - (worldWidth / 2);
        float y = (worldHeight * (((float)Mouse.getY()) / MainForm.VIEWPORT_HEIGHT)) - (worldHeight / 2);
        return new Vector3f(x, y, 0f);
    }

}