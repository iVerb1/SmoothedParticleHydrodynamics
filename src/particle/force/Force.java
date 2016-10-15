package particle.force;

import particle.PhysicsSystem;
import particle.State;

/**
 * Created by iVerb on 16-5-2015.
 */
public abstract class Force {

    public boolean draw = true;

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

    public abstract void apply(State state);

    protected abstract void draw2D(State state);

    protected abstract void draw3D(State state);

}
