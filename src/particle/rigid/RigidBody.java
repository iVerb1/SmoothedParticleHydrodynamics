package particle.rigid;

import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import particle.PhysicsSystem;
import particle.PhysicsObject;
import particle.State;

import java.util.Set;

/**
 * Created by iVerb on 17-6-2015.
 */
public abstract class RigidBody extends PhysicsObject {

    public boolean staticBody  = false;

    public RigidBody(Vector3f initialPosition, double mass) {
        super(initialPosition, mass);
    }

    public RigidBody(Vector3f initialPosition) {
        super(initialPosition);
    }

    public RigidBody(Vector2f initialPosition, double mass) {
        super(initialPosition, mass);
    }

    public RigidBody(Vector2f initialPosition) {
        super(initialPosition);
    }

    @Override
    public RealVector getPositionVector(State state) {
        return state.rbPositions;
    }

    @Override
    public RealVector getVelocityVector(State state) {
        return state.rbVelocities;
    }

    @Override
    protected RealVector getForceVector(State state) {
        return state.rbForces;
    }

    @Override
    public double getDensity(State state) {
        return getMass();
    }

    @Override
    public void setPosition(State state, RealVector position) {
        if (!staticBody) {
            super.setPosition(state, position);
        }
    }

    @Override
    public void setVelocity(State state, RealVector velocity) {
        if (!staticBody) {
            super.setVelocity(state, velocity);
        }
    }

    @Override
    public void addForce(State state, RealVector force) {
        if (!staticBody) {
            super.addForce(state, force);
        }
    }

    public abstract boolean checkPenetration(State state, RealVector point);

    public abstract RealVector getContactPoint(State state, RealVector point);

    public abstract RealVector getNormalInwards(State state, RealVector penetrationPoint);

    public abstract double getPenetrationDepth(State state, RealVector penetrationPoint);

    public abstract Set<RealVector> getPenetrationPoints(State state, RigidBody rigidBody);

}
