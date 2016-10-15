package particle.solve;

import particle.PhysicsSystem;

/**
 * Created by iVerb on 16-5-2015.
 */
public abstract class Solver {

    public abstract void solve(PhysicsSystem ps, double h);

}
