package particle;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import particle.rigid.RigidBody;
import particle.sph.HydroParticle;

/**
 * Created by iVerb on 18-5-2015.
 */
public class State {

    public RealVector sPositions;
    public RealVector sVelocities;
    public RealVector sForces;

    public RealVector hPositions;
    public RealVector hVelocities;
    public RealVector hDensities;
    public RealVector hForces;

    public RealVector rbPositions;
    public RealVector rbVelocities;
    public RealVector rbForces;


    public State(int numStandardParticles, int numFluidParticles, int numRigidBodies) {
        this.sPositions = new ArrayRealVector(0, numStandardParticles * PhysicsSystem.numDimensions);
        this.sVelocities = new ArrayRealVector(0, numStandardParticles * PhysicsSystem.numDimensions);
        this.hPositions = new ArrayRealVector(0, numFluidParticles * PhysicsSystem.numDimensions);
        this.hVelocities = new ArrayRealVector(0, numFluidParticles * PhysicsSystem.numDimensions);
        this.rbPositions = new ArrayRealVector(0, numRigidBodies * PhysicsSystem.numDimensions);
        this.rbVelocities = new ArrayRealVector(0, numRigidBodies * PhysicsSystem.numDimensions);
    }

    public State(RealVector sPositions, RealVector sVelocities, RealVector hPositions, RealVector hVelocities, RealVector rbPositions, RealVector rbVelocities) {
        this.sPositions = sPositions;
        this.sVelocities = sVelocities;
        this.hPositions = hPositions;
        this.hVelocities = hVelocities;
        this.rbPositions = rbPositions;
        this.rbVelocities = rbVelocities;
    }

    public State addDelta(StateDelta d) {
        return new State(sPositions.add(d.dSPositions), sVelocities.add(d.dSVelocities),
                hPositions.add(d.dHPositions), hVelocities.add(d.dHVelocities),
                rbPositions.add(d.dRBPositions), rbVelocities.add(d.dRBVelocities)
        );
    }

    public void extend(PhysicsObject... objects) {

        int numExtraStandardParticles = 0;
        int numExtraHydroParticles = 0;
        int numExtraRigidBodies = 0;

        for (PhysicsObject o : objects) {
            if (o instanceof StandardParticle)
                numExtraStandardParticles++;
            else if (o instanceof HydroParticle)
                numExtraHydroParticles++;
            else if (o instanceof RigidBody)
                numExtraRigidBodies++;
            else
                throw new IllegalStateException("Unsupported type inherited of ParticleSystemObject");
        }

        sPositions = sPositions.append(new ArrayRealVector(numExtraStandardParticles * PhysicsSystem.numDimensions));
        sVelocities = sVelocities.append(new ArrayRealVector(numExtraStandardParticles * PhysicsSystem.numDimensions));
        hPositions = hPositions.append(new ArrayRealVector(numExtraHydroParticles * PhysicsSystem.numDimensions));
        hVelocities = hVelocities.append(new ArrayRealVector(numExtraHydroParticles * PhysicsSystem.numDimensions));
        rbPositions = rbPositions.append(new ArrayRealVector(numExtraRigidBodies * PhysicsSystem.numDimensions));
        rbVelocities = rbVelocities.append(new ArrayRealVector(numExtraRigidBodies * PhysicsSystem.numDimensions));

        for (PhysicsObject o : objects) {
            o.reset(this);
        }
    }

    public void clearForces() {
        sForces = new ArrayRealVector(sPositions.getDimension());
        hForces = new ArrayRealVector(hPositions.getDimension());
        rbForces = new ArrayRealVector(rbPositions.getDimension());
    }
}
