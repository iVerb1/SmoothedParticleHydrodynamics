package particle.interaction;

import org.apache.commons.math3.linear.RealVector;
import org.lwjgl.util.vector.Vector3f;
import particle.PhysicsObject;
import particle.PhysicsSystem;
import particle.StandardParticle;
import particle.State;
import particle.force.MoveableSpringForce;
import particle.rigid.RigidBody;
import picking.PickHandler;
import picking.PickListener;
import util.Util;

/**
 * Created by iVerb on 17-6-2015.
 */
public class RigidBodyInteractor implements Interactor, PickListener {

    private RigidBody rigidBody = null;
    private PhysicsSystem system;

    private RealVector previousPosition;

    public RigidBodyInteractor(PhysicsSystem system) {
        this.system = system;
    }

    @Override
    public void update() {
        if (rigidBody != null) {
            State state = system.currentState;
            RealVector newPosition = Util.toRealVector(system.cam.getCoordinatesAtMouse(null));

            rigidBody.staticBody = false;
            rigidBody.setPosition(state, newPosition);
            if (previousPosition != null) {
                RealVector newVelocity = newPosition.subtract(previousPosition).mapDivide(system.getTimeStep());
                rigidBody.setVelocity(state, newVelocity);
            }
            rigidBody.staticBody = true;

            previousPosition = newPosition;
        }
    }

    @Override
    public void initialize() {
        PickHandler.listeners.add(this);
    }

    @Override
    public void reset() {
        if (rigidBody != null) {
            rigidBody.staticBody = false;
            rigidBody = null;
        }
    }

    @Override
    public void dispose() {
        PickHandler.listeners.remove(this);
    }

    @Override
    public void startPicking(int id, int mouseButton) {
        if (id != -1 && mouseButton == 0) {
            PhysicsObject o = system.objects.getPickedObject(id);
            if (o instanceof RigidBody) {
                rigidBody = (RigidBody)(o);
                rigidBody.staticBody = true;
                update();
            }
        }

    }

    @Override
    public void stopPicking(int mouseButton) {
        reset();
    }
}
