package particle;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.glu.Disk;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import util.Indexable;
import util.Util;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glTranslated;
import static particle.PhysicsSystem.*;
import static particle.PhysicsSystem.numDimensions;

/**
 * Created by iVerb on 17-6-2015.
 */
public abstract class PhysicsObject extends Indexable {

    private int xIndex = -1;
    private int yIndex = -1;
    private int zIndex = -1;

    public Vector3f initialPosition;
    private double mass;
    public boolean addedAtRuntime;

    public boolean doDraw;

    public PhysicsObject(Vector3f initialPosition, double mass) {
        this.initialPosition = initialPosition;
        this.mass = mass;

        this.addedAtRuntime = false;

        this.doDraw = true;
    }

    public PhysicsObject(Vector3f initialPosition) {
        this(initialPosition, 1.0);
    }

    public PhysicsObject(Vector2f initialPosition, double mass) {
        this(new Vector3f(initialPosition.x, initialPosition.y, 0f), mass);
    }

    public PhysicsObject(Vector2f initialPosition) {
        this(initialPosition, 1.0);
    }

    @Override
    public void setIndex(int indexIdentifier, int index) {
        super.setIndex(indexIdentifier, index);

        if (indexIdentifier == Indexable.VECTOR_INDEX) {
            if (is1D()) {
                this.xIndex = index;
            } else if (is2D()) {
                this.xIndex = 2 * index;
                this.yIndex = (2 * index) + 1;
            } else if (is3D()) {
                this.xIndex = 3 * index;
                this.yIndex = (3 * index) + 1;
                this.zIndex = (3 * index) + 2;
            } else
                throw new IllegalStateException("More than three dimensions!");
        }
    }

    public void reset(State currentState) {
        setPosition(currentState, initialPosition);
        setVelocity(currentState, new Vector3f(0f, 0f, 0f));
    }

    public int getXIndex() {
        return xIndex;
    }

    public int getYIndex() {
        return yIndex;
    }

    public int getZIndex() {
        return zIndex;
    }

    private RealVector getSubVector(RealVector v) {
        if (is1D())
            return new ArrayRealVector(new double[] {v.getEntry(getXIndex()), 0, 0});
        if (is2D())
            return new ArrayRealVector(new double[] {v.getEntry(getXIndex()), v.getEntry(getYIndex()), 0});
        if (is3D())
            return new ArrayRealVector(new double[] {v.getEntry(getXIndex()), v.getEntry(getYIndex()), v.getEntry(getZIndex())});

        throw new IllegalStateException("More than three dimensions!");
    }

    public RealVector getPosition(State state) {
        return getSubVector(getPositionVector(state));
    }

    public void setPosition(State state, Vector3f position) {
        setPosition(state, Util.toRealVector(position));
    }

    public void setPosition(State state, RealVector position) {
        RealVector v = getPositionVector(state);
        if (numDimensions >= 1)
            v.setEntry(getXIndex(), position.getEntry(0));
        if (numDimensions >= 2)
            v.setEntry(getYIndex(), position.getEntry(1));
        if (numDimensions >= 3)
            v.setEntry(getZIndex(), position.getEntry(2));
    }

    public double getX(State state) {
        if (numDimensions >= 1)
            return getPositionVector(state).getEntry(getXIndex());
        else
            return 0.0;
    }

    public double getY(State state) {
        if (numDimensions >= 2)
            return getPositionVector(state).getEntry(getYIndex());
        else
            return 0.0;
    }

    public double getZ(State state) {
        if (numDimensions >= 3)
            return getPositionVector(state).getEntry(getZIndex());
        else
            return 0.0;
    }

    public RealVector getVelocity(State state) {
        return getSubVector(getVelocityVector(state));
    }

    public void setVelocity(State state, Vector3f velocity) {
        setVelocity(state, Util.toRealVector(velocity));
    }

    public void setVelocity(State state, RealVector velocity) {
        RealVector v = getVelocityVector(state);
        if (numDimensions >= 1)
            v.setEntry(getXIndex(), velocity.getEntry(0));
        if (numDimensions >= 2)
            v.setEntry(getYIndex(), velocity.getEntry(1));
        if (numDimensions >= 3)
            v.setEntry(getZIndex(), velocity.getEntry(2));
    }

    public double getVelocityX(State state) {
        if (numDimensions >= 0)
            return getVelocityVector(state).getEntry(getXIndex());
        else
            return 0;
    }

    public double getVelocityY(State state) {
        if (numDimensions >= 2)
            return getVelocityVector(state).getEntry(getYIndex());
        else
            return 0.0;
    }

    public double getVelocityZ(State state) {
        if (numDimensions >= 3)
            return getVelocityVector(state).getEntry(getZIndex());
        else
            return 0.0;
    }

    public double getMass() {
        return mass;
    }

    public void divideForce(State state, double d) {
        RealVector Q = getForceVector(state);
        if (numDimensions >= 1)
            Q.setEntry(getXIndex(), Q.getEntry(getXIndex()) / d);
        if (numDimensions >= 2)
            Q.setEntry(getYIndex(), Q.getEntry(getYIndex()) / d);
        if (numDimensions >= 3)
            Q.setEntry(getZIndex(), Q.getEntry(getZIndex()) / d);
    }

    public RealVector getForce(State state) {
        return getSubVector(getForceVector(state));
    }

    public void setForce(State state, RealVector force) {
        RealVector Q = getForceVector(state);
        if (numDimensions > 0)
            Q.setEntry(getXIndex(), force.getEntry(0));
        if (numDimensions > 1)
            Q.setEntry(getYIndex(), force.getEntry(1));
        if (numDimensions > 2)
            Q.setEntry(getZIndex(), force.getEntry(2));
    }

    public void addForce(State state, RealVector force) {
        setForce(state, getForce(state).add(force));
    }

    public void divideEntries(RealMatrix m, double d) {
        for (int i = 0; i < m.getRowDimension(); i++) {
            if (numDimensions > 0)
                m.multiplyEntry(i, getXIndex(), 1.0 / d);
            if (numDimensions > 1)
                m.multiplyEntry(i, getYIndex(), 1.0 / d);
            if (numDimensions > 2)
                m.multiplyEntry(i, getZIndex(), 1.0 / d);
        }
    }

    protected abstract RealVector getPositionVector(State state);

    protected abstract RealVector getVelocityVector(State state);

    protected abstract RealVector getForceVector(State state);

    public abstract double getDensity(State state);


    public void render(State state) {
        if (doDraw) {
            if (PhysicsSystem.is2D())
                draw2D(state);
            else if (PhysicsSystem.is3D()) {
                draw3D(state);
            } else {
                throw new RuntimeException("Unsupported number of dimensions.");
            }
        }
    }

    protected abstract void draw2D(State state);

    protected abstract void draw3D(State state);
}
