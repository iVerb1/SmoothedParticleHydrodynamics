package particle.sph.force;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import particle.PhysicsSystem;
import particle.State;
import particle.force.Force;
import particle.sph.HydroParticle;
import particle.sph.smoothing.SmoothingKernel;

/**
 * Created by iVerb on 13-6-2015.
 */
public class ViscosityForce extends Force {

    public PhysicsSystem system;
    public SmoothingKernel kernel;

    public double viscosity = 1;

    public ViscosityForce(PhysicsSystem system, SmoothingKernel kernel) {
        this.system = system;
        this.kernel = kernel;
    }

    public ViscosityForce(PhysicsSystem system, SmoothingKernel kernel, double viscosity) {
        this.system = system;
        this.kernel = kernel;
        this.viscosity = viscosity;
    }

    @Override
    public void apply(State state) {
        for (HydroParticle pi : system.objects.hydro) {
            RealVector sum = new ArrayRealVector(3, 0);
            for (HydroParticle pj : system.objects.hydro) {
                RealVector x = pj.getVelocity(state).subtract(pi.getVelocity(state)).mapDivide(pj.getDensity(state)).mapMultiply(pj.getMass());
                sum = sum.add(x.mapMultiply(kernel.evalLaplacian(pi.getPosition(state).subtract(pj.getPosition(state)))));
            }
            pi.addForce(state, sum.mapMultiply(viscosity));
        }
    }

    @Override
    protected void draw2D(State state) {

    }

    @Override
    protected void draw3D(State state) {

    }
}
