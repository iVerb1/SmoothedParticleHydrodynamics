package particle.solve;

import particle.PhysicsSystem;
import particle.State;
import particle.StateDelta;

/**
 * Created by iVerb on 19-5-2015.
 */
public class MidPointSolver extends Solver {

    @Override
    public void solve(PhysicsSystem ps, double h) {
        StateDelta delta = ps.derivEval(ps.currentState).multiplySelf(h);

        State state = ps.currentState.addDelta(delta.divideSelf(2));
        StateDelta fMid = ps.derivEval(state);

        ps.currentState = ps.currentState.addDelta(fMid.multiplySelf(h));
    }
}
