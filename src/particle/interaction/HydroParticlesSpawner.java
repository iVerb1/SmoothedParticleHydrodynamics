package particle.interaction;

import org.lwjgl.Sys;
import particle.PhysicsSystem;
import particle.sph.HydroParticle;
import picking.PickHandler;
import picking.PickListener;

/**
 * Created by iVerb on 13-6-2015.
 */
public class HydroParticlesSpawner implements Interactor, PickListener {

    private PhysicsSystem system;

    private int numHydroParticles = 0;
    private boolean addingHydroParticles = false;
    private long timeLastParticleWasAdded;

    public float HYDRO_PARTICLE_SPAWN_INTERVAL = 0.05f;


    public HydroParticlesSpawner(PhysicsSystem system) {
        this.system = system;
    }


    @Override
    public void startPicking(int id, int mouseButton) {
        if (id == -1 && mouseButton == 0) {
            addingHydroParticles = true;
            addHydroParticle();
        }
    }

    @Override
    public void stopPicking(int mouseButton) {
        addingHydroParticles = false;
    }

    @Override
    public void update() {
        if (addingHydroParticles) {
            if (PhysicsSystem.is2D()) {
                long curTime = getCurTime();
                float diff = ((float)(curTime - timeLastParticleWasAdded)) / 1000;
                if (diff > HYDRO_PARTICLE_SPAWN_INTERVAL) {
                    addHydroParticle();
                }
            }
        }
    }

    private void addHydroParticle() {
        HydroParticle p = new HydroParticle(system.cam.getCoordinatesAtMouse(null), 0.02);
        p.addedAtRuntime = true;
        system.objects.addAll(p);
        timeLastParticleWasAdded = getCurTime();
        numHydroParticles++;
        System.out.println("Added " + numHydroParticles + "'th hydro particle.");
    }

    private long getCurTime() {
        return (1000 * Sys.getTime()) / Sys.getTimerResolution();
    }

    @Override
    public void initialize() {
        PickHandler.listeners.add(this);
    }

    @Override
    public void reset() {
        addingHydroParticles = false;
        numHydroParticles = 0;
    }

    @Override
    public void dispose() {
        PickHandler.listeners.remove(this);
    }
}
