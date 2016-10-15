package particle.sph;

import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import particle.PhysicsObject;
import particle.State;
import rendering.mesh.Mesh3D;
import util.Indexable;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glTranslated;

/**
 * Created by iVerb on 17-6-2015.
 */
public class HydroParticle extends PhysicsObject {

    public Vector4f color;
    public double radius;

    public HydroParticle(Vector3f initialPosition, double mass) {
        super(initialPosition, mass);

        color = new Vector4f(0f, 0f, 1f, 1f);
        radius = 0.1;
    }

    public HydroParticle(Vector3f initialPosition) {
        this(initialPosition, 1.0);
    }

    public HydroParticle(Vector2f initialPosition, double mass) {
        this(new Vector3f(initialPosition.x, initialPosition.y, 0), mass);
    }

    public HydroParticle(Vector2f initialPosition) {
        this(initialPosition, 1.0);
    }

    @Override
    public RealVector getPositionVector(State state) {
        return state.hPositions;
    }

    @Override
    public RealVector getVelocityVector(State state) {
        return state.hVelocities;
    }

    @Override
    protected RealVector getForceVector(State state) {
        return state.hForces;
    }

    @Override
    public double getDensity(State state) {
        return state.hDensities.getEntry(getIndex(Indexable.VECTOR_INDEX));
    }

    public void setDensity(State state, double d) {
        state.hDensities.setEntry(getIndex(Indexable.VECTOR_INDEX), d);
    }

    @Override
    protected void draw2D(State state) {
        glColor3f(color.x, color.y, color.z);
        glTranslated(getX(state), getY(state), 0);
        new Disk().draw(0, (float)radius, 15, 1);
    }

    @Override
    protected void draw3D(State state) {

    }

}
