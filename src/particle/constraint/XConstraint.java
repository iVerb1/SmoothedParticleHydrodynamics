package particle.constraint;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import particle.StandardParticle;
import particle.State;
import util.Indexable;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by iVerb on 22-5-2015.
 */
public class XConstraint extends Constraint {

    private StandardParticle particle;
    private double xStart;
    private double ySlope;
    private double zSlope;

    public XConstraint(StandardParticle particle, double xStart, double ySlope, double zSlope) {
        this.particle = particle;
        this.xStart = xStart;
        this.ySlope = ySlope;
        this.zSlope = zSlope;
    }

    @Override
    public void updateJ(RealMatrix J, State s) {
        updateMatrix(J, particle, 1, -ySlope, -zSlope);
    }

    @Override
    public void updateJDot(RealMatrix JDot, State s) {
        //no need. All relevant partial derivatives are 0.
    }

    @Override
    public void updateC(RealVector C, State s) {
        double c = particle.getX(s) - (xStart + ySlope*particle.getY(s) + zSlope*particle.getZ(s));

        C.setEntry(getIndex(Indexable.VECTOR_INDEX), c);
    }

    @Override
    protected void draw2D(State state) {
        glLineWidth(2.5f);
        glColor3f(1.0f, 1.0f, 1.0f);
        glBegin(GL_LINES);
        glVertex2d((xStart - (ySlope*50)), -50);
        glVertex2d((xStart + (ySlope*50)), 50);
        glEnd();
    }

    @Override
    protected void draw3D(State state) {

    }
}
