package particle.interaction;

import particle.PhysicsSystem;
import particle.PhysicsObject;
import particle.StandardParticle;
import particle.force.MoveableSpringForce;
import picking.PickHandler;
import picking.PickListener;

/**
 * Created by iVerb on 13-6-2015.
 */
public class StandardParticleInteractor implements Interactor, PickListener {

    private PhysicsSystem system;

    public double MOUSESPRING_SPRING_CONSTANT = 300;
    public double MOUSESPRING_DAMPING_CONSTANT = 1;

    private MoveableSpringForce mouseForce;
    private boolean applyingMouseForce = false;

    public StandardParticleInteractor(PhysicsSystem system) {
        this.system = system;
    }

    @Override
    public void startPicking(int id, int mouseButton) {
        if (id != -1 && mouseButton == 0) {
            PhysicsObject o = system.objects.getPickedObject(id);
            if (o instanceof StandardParticle) {
                mouseForce = new MoveableSpringForce(o, system.cam.getCoordinatesAtMouse(o), MOUSESPRING_SPRING_CONSTANT, MOUSESPRING_DAMPING_CONSTANT);
                system.forces.addFirst(mouseForce);
                applyingMouseForce = true;
            }
        }
    }

    @Override
    public void stopPicking(int mouseButton) {
        system.forces.remove(mouseForce);
        mouseForce = null;
        applyingMouseForce = false;
    }

    @Override
    public void update() {
        if (applyingMouseForce) {
            mouseForce.setPosition(system.cam.getCoordinatesAtMouse(mouseForce.getParticleSystemObject()));
        }
    }

    @Override
    public void initialize() {
        PickHandler.listeners.add(this);
    }

    @Override
    public void reset() {
        applyingMouseForce = false;
        if (mouseForce != null) {
            system.forces.remove(mouseForce);
        }
    }

    @Override
    public void dispose() {
        PickHandler.listeners.remove(this);
    }
}
