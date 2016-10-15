package particle.solve;

import particle.PhysicsSystem;
import particle.StateDelta;

/**
 * Created by iVerb on 16-5-2015.
 */
public class EulerSolver extends Solver {

    public void solve(PhysicsSystem ps, double h) {
        StateDelta delta = ps.derivEval(ps.currentState);
        ps.currentState = ps.currentState.addDelta(delta.multiplySelf(h));
    }

}

