package particle.rigid;

import org.apache.commons.math3.linear.RealVector;
import particle.PhysicsObject;
import particle.PhysicsSystem;
import particle.State;
import particle.force.Force;
import particle.rigid.primitive.Sphere;
import particle.sph.HydroParticle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by iVerb on 17-6-2015.
 */
public class RigidBodyForce extends Force {

    private PhysicsSystem system;

    public RigidBodyForce(PhysicsSystem system) {
        this.system = system;
    }


    @Override
    public void apply(State state) {

        HandledCollisionsMap handledCollisions = new HandledCollisionsMap();

        for (RigidBody rigidBody : system.objects.rigid) {

            for (PhysicsObject object : system.objects) {

                if (!object.equals(rigidBody)) {

                    RealVector objectPosition = object.getPosition(state);
                    Set<RealVector> penetrationPoints =  new HashSet<RealVector>();

                    if (object instanceof RigidBody) {
                        RigidBody rigidBody1 = (RigidBody) object;

                        if (!handledCollisions.contains(rigidBody, rigidBody1)) {
                            penetrationPoints = rigidBody.getPenetrationPoints(state, rigidBody1);

                            if (penetrationPoints == null) {
                                penetrationPoints = rigidBody1.getPenetrationPoints(state, rigidBody);

                                if (penetrationPoints == null) {
                                    throw new IllegalStateException("Collision between two rigid body types cannot be handled");
                                }
                                else {
                                    handledCollisions.putPair(rigidBody1, rigidBody);
                                    object = rigidBody;
                                    rigidBody = rigidBody1;
                                }
                            }
                            else {
                                handledCollisions.putPair(rigidBody, rigidBody1);
                            }
                        }
                    }
                    else if (rigidBody.checkPenetration(state, objectPosition)) {
                            penetrationPoints.add(objectPosition);
                    }

                    for (RealVector penetrationPoint : penetrationPoints) {
                        RealVector normal = rigidBody.getNormalInwards(state, penetrationPoint);
                        RealVector rbOldVel = rigidBody.getVelocity(state);
                        RealVector oOldVel = object.getVelocity(state);
                        double penetrationDepth = rigidBody.getPenetrationDepth(state, penetrationPoint);

                        object.setPosition(state, objectPosition.add(normal.mapMultiply(-penetrationDepth)));

                        RealVector oAddedVel = normal.mapMultiply(normal.dotProduct(rbOldVel.subtract(oOldVel))).mapMultiply(Math.min(1, rigidBody.getMass() / object.getMass()));
                        RealVector oNewVel = oOldVel;
                        if (oAddedVel.dotProduct(normal) < 0)
                            oNewVel = oNewVel.add(oAddedVel);

                        object.setVelocity(state, oNewVel);

                        normal = normal.mapMultiply(-1);
                        RealVector rbAddedVelocity = normal.mapMultiply(normal.dotProduct(oOldVel.subtract(rbOldVel))).mapMultiply(Math.min(1, object.getMass() / rigidBody.getMass()));
                        RealVector rbNewVel = rbOldVel;
                        if (rbAddedVelocity.dotProduct(normal) < 0)
                            rbNewVel = rbNewVel.add(rbAddedVelocity);

                        rigidBody.setVelocity(state, rbNewVel);
                    }
                }
            }
        }

    }

    @Override
    protected void draw2D(State state) {

    }

    @Override
    protected void draw3D(State state) {

    }

    class HandledCollisionsMap extends HashMap<RigidBody, Set<RigidBody>>  {

        public void putPair(RigidBody s, RigidBody t) {
            if (!containsKey(s))
                put(s, new HashSet<RigidBody>());

            get(s).add(t);

            if (!containsKey(t))
                put(t, new HashSet<RigidBody>());

            get(t).add(s);
        }

        public boolean contains(RigidBody t1, RigidBody t2) {
            if (keySet().contains(t1)) {
                return get(t1).contains(t2);
            }
            else {
                return false;
            }
        }
    }


}
