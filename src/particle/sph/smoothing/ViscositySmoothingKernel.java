package particle.sph.smoothing;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import particle.PhysicsSystem;
import util.Util;

/**
 * Created by iVerb on 13-6-2015.
 */
public class ViscositySmoothingKernel extends SmoothingKernel {

    public ViscositySmoothingKernel(PhysicsSystem system) {
        super(system);
    }

    @Override
    public double eval(RealVector v) {
        double r = Util.length(v);
        double h = system.CORE_RADIUS;

        if (r > 0 && r <= h) {
            double x = -(Math.pow(r, 3) / (2*Math.pow(h, 3))) + (Math.pow(r, 2) / Math.pow(h, 2)) + (h / (2 * r)) - 1;
            return (15.0 / (2 * Math.PI * Math.pow(h, 3))) * x;
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
            return v.mapMultiply((15.0 / (2 * Math.PI * Math.pow(h, 3))) * (-((3*r) / (2*Math.pow(h, 3))) + (2 / Math.pow(h, 2)) - (h / (2 * Math.pow(r, 3)))));
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
            return (45.0 / (Math.PI*Math.pow(h, 6)) * (h - r)) ;
        }
        else {
            return 0;
        }
    }
}
