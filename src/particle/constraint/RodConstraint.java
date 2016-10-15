package particle.constraint;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import particle.StandardParticle;
import particle.State;
import util.Indexable;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by iVerb on 19-5-2015.
 */
public class RodConstraint extends Constraint {

    StandardParticle p1;
    StandardParticle p2;
    double distance;

    public RodConstraint(StandardParticle p1, StandardParticle p2, double distance) {
        this.p1 = p1;
        this.p2 = p2;
        this.distance = distance;
    }

    @Override
    public void updateJ(RealMatrix J, State s) {
        double p1DerivX = (2 * p1.getX(s)) - (2 * p2.getX(s));
        double p1DerivY = (2 * p1.getY(s)) - (2 * p2.getY(s));
        double p1DerivZ = (2 * p1.getZ(s)) - (2 * p2.getZ(s));

        double p2DerivX = (2 * p2.getX(s)) - (2 * p1.getX(s));
        double p2DerivY = (2 * p2.getY(s)) - (2 * p1.getY(s));
        double p2DerivZ = (2 * p2.getZ(s)) - (2 * p1.getZ(s));


        updateMatrix(J, p1, p1DerivX, p1DerivY, p1DerivZ);
        updateMatrix(J, p2, p2DerivX, p2DerivY, p2DerivZ);
    }

    @Override
    public void updateJDot(RealMatrix JDot, State s) {
        double p1DerivX = (2 * p1.getVelocityX(s)) - (2 * p2.getVelocityX(s));
        double p1DerivY = (2 * p1.getVelocityY(s)) - (2 * p2.getVelocityY(s));
        double p1DerivZ = (2 * p1.getVelocityZ(s)) - (2 * p2.getVelocityZ(s));

        double p2DerivX = (2 * p2.getVelocityX(s)) - (2 * p1.getVelocityX(s));
        double p2DerivY = (2 * p2.getVelocityY(s)) - (2 * p1.getVelocityY(s));
        double p2DerivZ = (2 * p2.getVelocityZ(s)) - (2 * p1.getVelocityZ(s));

        updateMatrix(JDot, p1, p1DerivX, p1DerivY, p1DerivZ);
        updateMatrix(JDot, p2, p2DerivX, p2DerivY, p2DerivZ);
    }

    @Override
    public void updateC(RealVector C, State s) {
        double c = Math.pow(p1.getX(s) - p2.getX(s), 2)
                + Math.pow(p1.getY(s) - p2.getY(s), 2)
                + Math.pow(p1.getZ(s) - p2.getZ(s), 2)
                - Math.pow(distance, 2);

        C.setEntry(getIndex(Indexable.VECTOR_INDEX), c);
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
