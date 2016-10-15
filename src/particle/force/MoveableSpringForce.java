package particle.force;

import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.vector.Vector3f;
import particle.PhysicsObject;
import particle.State;
import util.Util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

/**
 * Created by iVerb on 23-5-2015.
 */
public class MoveableSpringForce extends Force {

    private PhysicsObject object;
    private RealVector position;

    public double springConstant;
    public double dampingConstant;

    public MoveableSpringForce(PhysicsObject object, Vector3f position, double springConstant, double dampingConstant) {
        this.object = object;
        this.springConstant = springConstant;
        this.dampingConstant = dampingConstant;

        setPosition(position);
    }

    public void setPosition(Vector3f position) {
        this.position = Util.toRealVector(position);
    }

    public PhysicsObject getParticleSystemObject() {
        return object;
    }

    @Override
    public void apply(State state) {
        RealVector l = object.getPosition(state).subtract(position);
        double lLength = Math.sqrt(l.dotProduct(l));

        if (lLength != 0) {
            RealVector lDot = object.getVelocity(state);
            RealVector force = l.mapDivideToSelf(lLength).mapMultiplyToSelf(-(this.springConstant * (lLength) + this.dampingConstant * (lDot.dotProduct(l)) / lLength));
            object.addForce(state, force);
        }
    }

    @Override
    protected void draw2D(State state) {
        glLineWidth(2.5f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex2d(object.getX(state), object.getY(state));
        glVertex2d(position.getEntry(0), position.getEntry(1));
        glEnd();
    }

    @Override
    protected void draw3D(State state) {
        glLineWidth(2.5f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex3d(object.getX(state), object.getY(state), object.getZ(state));
        glVertex3d(position.getEntry(0), position.getEntry(1), position.getEntry(2));
        glEnd();
    }

}
