package particle.force;

import org.apache.commons.math3.linear.ArrayRealVector;
import particle.PhysicsSystem;
import particle.PhysicsObject;
import particle.State;

/**
 * Created by iVerb on 16-5-2015.
 */
public class GravitationalForce extends Force {

    private PhysicsSystem system;

    public GravitationalForce(PhysicsSystem system) {
        this.system = system;
    }

    @Override
    public void apply(State state) {
        for (PhysicsObject p : system.objects) {
            p.addForce(state, new ArrayRealVector(new double[]{0.0, -(p.getDensity(state) * system.GRAVITATIONAL_ACCELERATION), 0.0}));
        }
    }

    @Override
    protected void draw2D(State currentState) {

    }

    @Override
    protected void draw3D(State state) {

    }

}
