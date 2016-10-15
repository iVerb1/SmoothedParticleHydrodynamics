package particle.force;

import particle.PhysicsObject;
import particle.PhysicsObjectManager;
import particle.PhysicsSystem;
import particle.State;
import particle.sph.HydroParticle;

/**
 * Created by iVerb on 16-5-2015.
 */
public class ViscousDragForce extends Force {

    private PhysicsSystem system;

    public ViscousDragForce(PhysicsSystem system) {
        this.system = system;
    }

    @Override
    public void apply(State state) {
        for (PhysicsObject p : system.objects) {
            if (!(p instanceof HydroParticle)) {
                p.addForce(state, p.getVelocity(state).mapMultiplyToSelf(-system.VISCOUS_DRAG_COEFFICIENT));
            }
        }
    }

    @Override
    protected void draw2D(State currentState) {

    }

    @Override
    protected void draw3D(State state) {

    }

}
