package particle.force;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.lwjgl.util.vector.Vector3f;
import particle.PhysicsObject;
import particle.PhysicsObjectManager;
import particle.State;

/**
 * Created by iVerb on 27-5-2015.
 */
public class ConstantForce extends Force {

    private PhysicsObjectManager objects;
    private Vector3f force;

    public ConstantForce(Vector3f force, PhysicsObjectManager objects) {
        this.objects = objects;
        this.force = force;
    }

    public void setForce(Vector3f force){
        this.force = force;
    }

    @Override
    public void apply(State state) {
        for (PhysicsObject p : objects) {
            p.addForce(state, new ArrayRealVector(new double[]{force.x, force.y, force.z}));
        }
    }

    @Override
    protected void draw2D(State currentState) {

    }

    @Override
    protected void draw3D(State state) {

    }

}
