package particle.interaction;

import particle.PhysicsSystem;
import particle.rigid.primitive.Sphere;
import picking.PickHandler;
import picking.PickListener;

/**
 * Created by iVerb on 16-6-2015.
 */
public class RigidBodySpawner implements Interactor, PickListener {

    private PhysicsSystem system;

    public RigidBodySpawner(PhysicsSystem system) {
        this.system = system;
    }

    @Override
    public void update() {

    }

    @Override
    public void initialize() {
        PickHandler.listeners.add(this);
    }

    @Override
    public void reset() {

    }

    @Override
    public void dispose() {
        PickHandler.listeners.remove(this);
    }

    @Override
    public void startPicking(int id, int mouseButton) {
        if (id == -1 && mouseButton == 1) {
            if (PhysicsSystem.is2D()) {
                Sphere p = new Sphere(system.cam.getCoordinatesAtMouse(null), 0.2);
                p.addedAtRuntime = true;
                system.objects.addAll(p);
            }
        }
    }

    @Override
    public void stopPicking(int mouseButton) {

    }
}
