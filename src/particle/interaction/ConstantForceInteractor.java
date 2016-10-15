package particle.interaction;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import particle.PhysicsSystem;
import particle.force.ConstantForce;

/**
 * Created by iVerb on 13-6-2015.
 */
public class ConstantForceInteractor implements Interactor {

    private PhysicsSystem system;

    private ConstantForce windForce;
    private boolean applyingWindForce = false;

    public float WIND_FORCE = 7;
    public float WIND_FORCE_TURBO = 30;


    public ConstantForceInteractor(PhysicsSystem system) {
        this.system = system;
    }

    @Override
    public void update() {
        float mult = Keyboard.isKeyDown(Keyboard.KEY_UP) ? WIND_FORCE_TURBO : WIND_FORCE;

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT) && !applyingWindForce) {
            applyingWindForce = true;
            Vector3f force = system.cam.getRightVector().normalise(null);
            windForce = new ConstantForce(new Vector3f(force.x * mult, force.y * mult, force.z * mult), system.objects);
            system.forces.addFirst(windForce);
        }
        else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT) && !applyingWindForce) {
            applyingWindForce = true;
            Vector3f force = system.cam.getRightVector().normalise(null);
            windForce = new ConstantForce(new Vector3f(-force.x * mult, -force.y * mult, -force.z * mult), system.objects);
            system.forces.addFirst(windForce);
        }
        else if (applyingWindForce){
            reset();
        }
    }

    @Override
    public void initialize() {

    }

    @Override
    public void reset() {
        applyingWindForce = false;
        if (windForce != null) {
            system.forces.remove(windForce);
        }
    }

    @Override
    public void dispose() {

    }
}
