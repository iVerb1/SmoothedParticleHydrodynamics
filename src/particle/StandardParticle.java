package particle;

import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import rendering.mesh.Mesh3D;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glTranslated;

/**
 * Created by iVerb on 17-6-2015.
 */
public class StandardParticle extends PhysicsObject {

    public Mesh3D sphere;
    public Vector4f  color;
    public double radius;

    public StandardParticle(Vector3f initialPosition, double mass) {
        super(initialPosition, mass);

        color = new Vector4f(1f, 0f, 0f, 1f);
        radius = 0.13;
        sphere = new Mesh3D("sphere");
        sphere.scalingFactor = 0.2f;
        sphere.material.color = color;
    }

    public StandardParticle(Vector3f initialPosition) {
        this(initialPosition, 1.0);
    }

    public StandardParticle(Vector2f initialPosition, double mass) {
        this(new Vector3f(initialPosition.x, initialPosition.y, 0), mass);
    }

    public StandardParticle(Vector2f initialPosition) {
        this(initialPosition, 1.0);
    }

    @Override
    public RealVector getPositionVector(State state) {
        return state.sPositions;
    }

    @Override
    public RealVector getVelocityVector(State state) {
        return state.sVelocities;
    }

    @Override
    protected RealVector getForceVector(State state) {
        return state.sForces;
    }

    @Override
    public double getDensity(State state) {
        return getMass();
    }

    @Override
    protected void draw2D(State state) {
        glColor3f(color.x, color.y, color.z);
        glTranslated(getX(state), getY(state), 0);
        new Disk().draw(0, (float)radius, 15, 1);
    }

    @Override
    protected void draw3D(State state) {
        glTranslated(getX(state), getY(state), getZ(state));
        sphere.render();
    }

}
