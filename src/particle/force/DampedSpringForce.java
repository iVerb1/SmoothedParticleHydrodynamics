package particle.force;

import org.apache.commons.math3.linear.RealVector;
import particle.PhysicsObject;
import particle.State;
import util.Util;

import static org.lwjgl.opengl.GL11.*;


/**
 * Created by Jeroen van Wijgerden on 16-5-2015.
 */
public class DampedSpringForce extends Force {

    private PhysicsObject p1;
    private PhysicsObject p2;

    private double length;
    private double springConstant;
    private double dampingConstant;

    public DampedSpringForce(PhysicsObject p1, PhysicsObject p2, double length, double springConstant, double dampingConstant) {
        this.p1 = p1;
        this.p2 = p2;

        this.length = length;
        this.springConstant = springConstant;
        this.dampingConstant = dampingConstant;
    }


    @Override
    public void apply(State state) {
        RealVector l = p1.getPosition(state).subtract(p2.getPosition(state));
        double lLength = Util.length(l);

        if (lLength != 0) {
            RealVector lNorm = Util.normalise(l);
            RealVector lDot = p1.getVelocity(state).subtract(p2.getVelocity(state));
            RealVector force = lNorm.mapMultiply((springConstant * (lLength - length)) + (dampingConstant * (lDot.dotProduct(lNorm))));
            p2.addForce(state, force);
            p1.addForce(state, force.mapMultiplyToSelf(-1));
        }
    }

    @Override
    protected void draw2D(State state) {
        glLineWidth(2.5f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex2d(p1.getX(state), p1.getY(state));
        glVertex2d(p2.getX(state), p2.getY(state));
        glEnd();
    }

    @Override
    protected void draw3D(State state) {
        glLineWidth(2.5f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex3d(p1.getX(state), p1.getY(state), p1.getZ(state));
        glVertex3d(p2.getX(state), p2.getY(state), p2.getZ(state));
        glEnd();
    }

}
