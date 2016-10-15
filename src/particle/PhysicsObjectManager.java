package particle;

import particle.rigid.RigidBody;
import particle.sph.HydroParticle;
import util.IndexedMap;

import java.util.*;

import static util.Indexable.PICKING_INDEX;
import static util.Indexable.VECTOR_INDEX;

/**
 * Created by iVerb on 17-6-2015.
 */
public class PhysicsObjectManager implements Iterable<PhysicsObject> {

    private final PhysicsSystem physicsSystem;

    private IndexedMap<PhysicsObject> objects;
    public IndexedMap<StandardParticle> standard;
    public IndexedMap<HydroParticle> hydro;
    public IndexedMap<RigidBody> rigid;


    public PhysicsObjectManager(PhysicsSystem physicsSystem) {
        this.physicsSystem = physicsSystem;

        objects = new IndexedMap<PhysicsObject>(PICKING_INDEX);
        standard = new IndexedMap<StandardParticle>(VECTOR_INDEX);
        hydro = new IndexedMap<HydroParticle>(VECTOR_INDEX);
        rigid = new IndexedMap<RigidBody>(VECTOR_INDEX);
    }

    public void addAll(PhysicsObject... objects) {
        for (PhysicsObject o : objects) {
            if (o instanceof StandardParticle) {
                standard.add((StandardParticle) o);
            }
            else if (o instanceof HydroParticle) {
                hydro.add((HydroParticle) o);
            }
            else if (o instanceof RigidBody) {
                rigid.add((RigidBody) o);
            }
            else
                throw new IllegalStateException("Unsupported type inherited of ParticleSystemObject");

        }

        this.objects.addAll(objects);
        physicsSystem.currentState.extend(objects);
    }

    public void addAll(Collection<PhysicsObject> objects) {
        addAll(objects.toArray(new PhysicsObject[objects.size()]));
    }

    public void clear() {
        objects.clear();
        standard.clear();
        hydro.clear();
        rigid.clear();
    }

    public int size() {
        return objects.size();
    }

    public PhysicsObject getPickedObject(int id) {
        return objects.get(id);
    }

    @Override
    public Iterator<PhysicsObject> iterator() {
        return objects.values().iterator();
    }
}
