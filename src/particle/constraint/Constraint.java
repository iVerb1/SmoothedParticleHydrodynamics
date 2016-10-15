package particle.constraint;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import particle.PhysicsSystem;
import particle.StandardParticle;
import particle.State;
import util.Indexable;

import static particle.PhysicsSystem.numDimensions;

/**
 * Created by iVerb on 16-5-2015.
 */
public abstract class Constraint extends Indexable {

    public boolean draw = true;

    public abstract void updateJ(RealMatrix J, State s);

    public abstract void updateJDot(RealMatrix JDot, State s);

    public abstract void updateC(RealVector C, State s);

    public void render(State state) {
        if (draw) {
            if (PhysicsSystem.is2D())
                draw2D(state);
            else if (PhysicsSystem.is3D()) {
                draw3D(state);
            }
            else {
                throw new RuntimeException("Unsupported number of dimensions.");
            }
        }
    }

    protected abstract void draw2D(State state);

    protected abstract void draw3D(State state);

    protected void updateMatrix(RealMatrix m, StandardParticle p, double partialDerivX, double partialDerivY, double partialDerivZ) {
        int index = getIndex(Indexable.VECTOR_INDEX);
        if (numDimensions >= 1)
            m.setEntry(index, p.getXIndex(), partialDerivX);
        if (numDimensions >= 2)
            m.setEntry(index, p.getYIndex(), partialDerivY);
        if (numDimensions >= 3)
            m.setEntry(index, p.getZIndex(), partialDerivZ);
    }

}
