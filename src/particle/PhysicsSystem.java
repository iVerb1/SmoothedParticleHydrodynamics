package particle;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.linear.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import particle.constraint.Constraint;
import particle.force.*;
import particle.force.ViscousDragForce;
import particle.interaction.*;
import particle.rigid.RigidBodyForce;
import particle.solve.RungeKutta4Solver;
import particle.solve.Solver;
import particle.sph.HydroParticle;
import particle.sph.smoothing.DefaultSmoothingKernel;
import particle.sph.smoothing.SmoothingKernel;
import picking.PickHandler;
import rendering.Camera;
import util.Indexable;
import util.IndexedMap;
import util.Time;

import java.util.*;

public abstract class PhysicsSystem implements Runnable {

    public static int numDimensions;

    public static boolean is1D() {
        return numDimensions == 1;
    }
    public static boolean is2D() {
        return numDimensions == 2;
    }
    public static boolean is3D() {
        return numDimensions == 3;
    }

    public State currentState;
    public PhysicsObjectManager objects;
    public IndexedMap<Constraint> constraints;
    public LinkedList<Force> forces;

    public double Ks;
    public double Kd;
    public int MAX_ITERATIONS = 99;
    public double DELTA_THRESHOLD = 0.0001;

    public double GRAVITATIONAL_ACCELERATION;
    public double VISCOUS_DRAG_COEFFICIENT;
    public double REST_DENSITY;
    public double CORE_RADIUS;

    public Solver solver;
    protected ConjugateGradient linearSolver;
    protected SmoothingKernel densitySmoothingKernel;
    protected List<Interactor> interactors;

    public Camera cam;
    public double timeStep;
    public boolean variableTimeStep;

    private boolean paused;
    private boolean interrupted;
    private boolean crashed;


    public PhysicsSystem() {
        this.linearSolver = new ConjugateGradient(MAX_ITERATIONS, DELTA_THRESHOLD, false);
        this.interactors = new ArrayList<Interactor>();

        this.resetSystem();
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void run() {
        while (Display.isCreated()) {  }

        try {
            Display.create();
        }
        catch (LWJGLException e) {
            e.printStackTrace();
        }

        initialize();

        boolean varTimeStep = variableTimeStep;
        int c = 0;
        variableTimeStep = false;
        try {
            while (!interrupted) {
                if (c < 4)
                    c++;
                if (c == 3)
                    variableTimeStep = varTimeStep;

                step();
            }
        }
        catch (MaxCountExceededException e) {
            System.out.println("Simulation crashed");
            crashed = true;
        }

        Display.destroy();
    }

    private synchronized void step() {
        Time.tick();

        double delta = getTimeStep();
        if (!paused && delta > 0) {
            solver.solve(this, delta);
        }

        update();
        cam.update();
        cam.render();

        Display.update();
        //Display.sync(60);
    }

    public double getTimeStep() {
        if (variableTimeStep)
            return Time.getDelta();
        else
            return timeStep;
    }

    private void update() {
        while(Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                if (!PickHandler.isPicking()) {
                    if (variableTimeStep)
                        Time.setNextDelta(0);

                    int pickId = cam.pick();

                    PickHandler.startPicking(pickId, Mouse.getEventButton());
                }
            }
            else {
                if (Mouse.getEventButton() != -1 && PickHandler.isPicking()) {
                    PickHandler.stopPicking(Mouse.getEventButton());
                }
            }
        }

        for (Interactor listener : interactors) {
            listener.update();
        }
    }

    private void initialize() {
        cam.initializeOpenGL();
        Time.initialize();

        crashed = false;
        interrupted = false;

        for (Interactor listener : interactors) {
            listener.initialize();
        }
    }

    public void resetSystem() {
        resetCamera();

        variableTimeStep = true;
        timeStep = 0.02;
        Ks = 0.5;
        Kd = 0.5;

        GRAVITATIONAL_ACCELERATION = 9.81;
        VISCOUS_DRAG_COEFFICIENT = 0.8;
        REST_DENSITY = 0.5;
        CORE_RADIUS = 0.5;

        currentState = new State(0, 0, 0);
        objects = new PhysicsObjectManager(this);
        constraints = new IndexedMap<Constraint>(Indexable.VECTOR_INDEX);
        forces = new LinkedList<Force>();
        solver = new RungeKutta4Solver();
        densitySmoothingKernel = new DefaultSmoothingKernel(this);

        for (Interactor interactor : interactors) {
            interactor.dispose();
        }

        interactors.clear();
        interactors.add(new StandardParticleInteractor(this));
        interactors.add(new RigidBodyInteractor(this));
        interactors.add(new ConstantForceInteractor(this));
    }

    public synchronized void restart() {
        Set<PhysicsObject> original = new HashSet<PhysicsObject>();
        for (PhysicsObject p : objects) {
            if (!p.addedAtRuntime)
                original.add(p);
        }

        objects.clear();
        currentState = new State(0, 0, 0);
        objects.addAll(original);

        for (Interactor interactor : interactors) {
            interactor.reset();
        }
    }

    protected abstract void resetCamera();

    public void interrupt() {
        interrupted = true;
    }

    public boolean hasCrashed() {
        return crashed;
    }

    public void togglePause() {
        paused = !paused;
    }

    public void toggleVariableTimeStep() {
        variableTimeStep = !variableTimeStep;
    }

    public StateDelta derivEval(State state) {

        //compute densities
        state.hDensities = new ArrayRealVector(objects.hydro.size());
        for (HydroParticle pi : objects.hydro) {
            double sum = 0;
            for (HydroParticle pj : objects.hydro) {
                sum += (pj.getMass() * densitySmoothingKernel.eval(pi.getPosition(state).subtract(pj.getPosition(state))));
            }
            pi.setDensity(state, sum);
        }

        //clear forces
        state.clearForces();

        //apply forces
        for (Force f : forces) {
            f.apply(state);
        }

        int standardParticlesDim = objects.standard.size() * numDimensions;
        int numConstraints = constraints.size();

        if (constraints.size() > 0) {
            RealMatrix J = new Array2DRowRealMatrix(numConstraints, standardParticlesDim);
            RealMatrix JDot = new Array2DRowRealMatrix(numConstraints, standardParticlesDim);
            RealVector C = new ArrayRealVector(numConstraints);
            RealVector CDot = new ArrayRealVector(numConstraints);

            //obtain C, CDot, J, and JDot
            for (Constraint c : constraints.values()) {
                c.updateJ(J, state);
                c.updateJDot(JDot, state);
                c.updateC(C, state);
                CDot = J.operate(state.sVelocities);
            }

            RealMatrix JTranspose = J.transpose();

            RealMatrix JW = J.copy();
            for (StandardParticle p : objects.standard) {
                p.divideEntries(JW, p.getDensity(state));
            }

            RealVector RHS1 = JDot.operate(state.sVelocities).mapMultiplyToSelf(-1);
            RealVector RHS2 = JW.operate(state.sForces);
            RealVector RHS3 = C.mapMultiplyToSelf(Ks);
            RealVector RHS4 = CDot.mapMultiplyToSelf(Kd);
            RealVector RHS = RHS1.subtract(RHS2).subtract(RHS3).subtract(RHS4);
            RealMatrix LHS = JW.multiply(JTranspose);

            //solving lambda
            RealVector lambda = new ArrayRealVector(numConstraints, 0.0);
            lambda = linearSolver.solveInPlace((Array2DRowRealMatrix) LHS, null, RHS, lambda);
            RealVector constraintForces = JTranspose.operate(lambda);
            state.sForces = state.sForces.add(constraintForces);
        }

        for (PhysicsObject p : objects) {
            p.divideForce(state, p.getDensity(state));
        }

        return new StateDelta(state.sVelocities.copy(), state.sForces, state.hVelocities.copy(), state.hForces, state.rbVelocities.copy(), state.rbForces);
    }

    protected void addStandardForces() {
        forces.add(new GravitationalForce(this));
        forces.add(new ViscousDragForce(this));
        forces.addLast(new RigidBodyForce(this));
    }

}