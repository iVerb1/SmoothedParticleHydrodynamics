package particle.constraint;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import particle.StandardParticle;
import particle.State;
import util.Indexable;

import static org.lwjgl.opengl.GL11.*;


/**
 * Created by iVerb on 17-5-2015.
 */
public class CircularWireConstraint extends Constraint {

    private StandardParticle particle;
    private Vector3f center;
    private double radius;


    public CircularWireConstraint(Vector2f center, StandardParticle particle, double radius) {
        this.center = new Vector3f(center.x, center.y, 0);
        this.particle = particle;
        this.radius = radius;
    }

    public CircularWireConstraint(Vector3f center, StandardParticle particle, double radius) {
        this.center = center;
        this.particle = particle;
        this.radius = radius;
    }

    @Override
    public void updateJ(RealMatrix J, State s) {
        double partialDerivX = (2 * particle.getX(s)) - (2 * center.x);
        double partialDerivY = (2 * particle.getY(s)) - (2 * center.y);
        double partialDerivZ = (2 * particle.getZ(s)) - (2 * center.z);

        updateMatrix(J, particle, partialDerivX, partialDerivY, partialDerivZ);
    }

    @Override
    public void updateJDot(RealMatrix JDot, State s) {
        double partialDerivX = (2 * particle.getVelocityX(s));
        double partialDerivY = (2 * particle.getVelocityY(s));
        double partialDerivZ = (2 * particle.getVelocityZ(s));

        updateMatrix(JDot, particle, partialDerivX, partialDerivY, partialDerivZ);
    }

    @Override
    public void updateC(RealVector C, State s) {
        double c = Math.pow(center.x - particle.getX(s), 2)
                    + Math.pow(center.y - particle.getY(s), 2)
                    + Math.pow(center.z - particle.getZ(s), 2)
                    - Math.pow(radius, 2);

        C.setEntry(getIndex(Indexable.VECTOR_INDEX), c);
    }

    @Override
    protected void draw2D(State state) {
        glLineWidth(2.5f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex2d(particle.getX(state), particle.getY(state));
        glVertex2f(center.x, center.y);
        glEnd();
    }

    @Override
    protected void draw3D(State state) {
        glLineWidth(2.5f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex3d(particle.getX(state), particle.getY(state), particle.getZ(state));
        glVertex3f(center.x, center.y, center.z);
        glEnd();
    }

}
