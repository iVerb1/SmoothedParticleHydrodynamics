package particle.constraint;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import particle.StandardParticle;
import particle.State;
import util.Indexable;


/**
 * Created by iVerb on 22-5-2015.
 */
public class ZConstraint extends Constraint {

    private StandardParticle particle;
    private double zStart;
    private double xSlope;
    private double ySlope;

    public ZConstraint(StandardParticle particle, double zStart, double xSlope, double ySlope) {
        this.particle = particle;
        this.zStart = zStart;
        this.xSlope = xSlope;
        this.ySlope = ySlope;
    }


    @Override
    public void updateJ(RealMatrix J, State s) {
        updateMatrix(J, particle, -xSlope, -ySlope, 1);
    }

    @Override
    public void updateJDot(RealMatrix JDot, State s) {
        //no need. All relevant partial derivatives are 0.
    }

    @Override
    public void updateC(RealVector C, State s) {
        double c = particle.getZ(s) - (zStart + xSlope*particle.getX(s) + ySlope*particle.getY(s));

        C.setEntry(getIndex(Indexable.VECTOR_INDEX), c);
    }

    @Override
    protected void draw2D(State state) {

    }

    @Override
    protected void draw3D(State state) {

    }
}
