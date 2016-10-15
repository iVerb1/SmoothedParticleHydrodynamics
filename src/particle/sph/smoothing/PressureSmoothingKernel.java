package particle.sph.smoothing;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import particle.PhysicsSystem;
import util.Util;

/**
 * Created by iVerb on 13-6-2015.
 */
public class PressureSmoothingKernel extends SmoothingKernel {

    public PressureSmoothingKernel(PhysicsSystem system) {
        super(system);
    }

    @Override
    public double eval(RealVector v) {
        double r = Util.length(v);
        double h = system.CORE_RADIUS;

        if (r >= 0 && r <= system.CORE_RADIUS) {
            return (15.0 / (Math.PI*Math.pow(h, 6))) * Math.pow(h - r, 3);
        }
        else {
            return 0;
        }
    }

    @Override
    public RealVector evalGradient(RealVector v) {
        double r = Util.length(v);
        double h = system.CORE_RADIUS;

        if (r > 0 && r <= h) {
            return Util.normalise(v).mapMultiply(-(45.0 / (Math.PI * Math.pow(h, 6))) * Math.pow(h - r, 2));
        }
        else {
            return new ArrayRealVector(3, 0);
        }
    }

    @Override
    public double evalLaplacian(RealVector v) {
        double r = Util.length(v);
        double h = system.CORE_RADIUS;

        if (r > 0 && r <= h) {
            return -((90.0 / (Math.PI * Math.pow(h, 6))) * ((h - r) * (h - 2 * r))) / r;
        }
        else {
            return 0;
        }
    }
}
