package particle.sph.smoothing;

import org.apache.commons.math3.linear.RealVector;
import particle.PhysicsSystem;

/**
 * Created by iVerb on 13-6-2015.
 */
public abstract class SmoothingKernel {

    protected PhysicsSystem system;

    public SmoothingKernel(PhysicsSystem system) {
        this.system = system;
    }

    public abstract double eval(RealVector v);

    public abstract RealVector evalGradient(RealVector v);

    public abstract double evalLaplacian(RealVector v);

}
