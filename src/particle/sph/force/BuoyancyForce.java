package particle.sph.force;

import org.apache.commons.math3.linear.ArrayRealVector;
import particle.PhysicsSystem;
import particle.State;
import particle.force.Force;
import particle.sph.HydroParticle;
import particle.sph.smoothing.SmoothingKernel;

/**
 * Created by iVerb on 13-6-2015.
 */
public class BuoyancyForce extends Force {

    public PhysicsSystem system;
    public SmoothingKernel kernel;

    public double buoyancy = 0;

    public BuoyancyForce(PhysicsSystem system, SmoothingKernel kernel) {
        this.system = system;
        this.kernel = kernel;
    }

    public BuoyancyForce(PhysicsSystem system, SmoothingKernel kernel, double buoyancy) {
        this.system = system;
        this.kernel = kernel;
        this.buoyancy = buoyancy;
    }

    @Override
    public void apply(State state) {
        for (HydroParticle p : system.objects.hydro) {
            p.addForce(state, new ArrayRealVector(new double[]{0.0, -buoyancy*(p.getDensity(state) - system.REST_DENSITY)*system.GRAVITATIONAL_ACCELERATION, 0.0}));
        }
    }

    @Override
    protected void draw2D(State state) {

    }

    @Override
    protected void draw3D(State state) {

    }
}
