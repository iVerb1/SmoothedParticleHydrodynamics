package particle.solve;

import particle.PhysicsSystem;
import particle.State;
import particle.StateDelta;


/**
 * Created by iVerb on 22-5-2015.
 */
public class RungeKutta4Solver extends Solver {

    @Override
    public void solve(PhysicsSystem ps, double h) {
        State state;

        StateDelta k1 = ps.derivEval(ps.currentState).multiplySelf(h);

        state = ps.currentState.addDelta(k1.divideSelf(2));
        StateDelta k2 = ps.derivEval(state).multiplySelf(h);

        state = ps.currentState.addDelta(k2.divideSelf(2));
        StateDelta k3 = ps.derivEval(state).multiplySelf(h);

        state = ps.currentState.addDelta(k3);
        StateDelta k4 = ps.derivEval(state).multiplySelf(h);

        ps.currentState = ps.currentState
                .addDelta(k1.divideSelf(6))
                .addDelta(k2.divideSelf(3))
                .addDelta(k3.divideSelf(3))
                .addDelta(k4.divideSelf(6));

    }
}