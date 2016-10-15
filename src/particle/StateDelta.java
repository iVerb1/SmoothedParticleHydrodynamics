package particle;

import org.apache.commons.math3.linear.RealVector;

/**
 * Created by iVerb on 18-5-2015.
 */
public class StateDelta {

    public RealVector dSPositions;
    public RealVector dSVelocities;
    public RealVector dHPositions;
    public RealVector dHVelocities;
    public RealVector dRBPositions;
    public RealVector dRBVelocities;


    public StateDelta(RealVector dSPositions, RealVector dSVelocities, RealVector dHPositions, RealVector dHVelocities, RealVector dRBPositions, RealVector dRBVelocities) {
        this.dSPositions = dSPositions;
        this.dSVelocities = dSVelocities;
        this.dHPositions = dHPositions;
        this.dHVelocities = dHVelocities;
        this.dRBPositions = dRBPositions;
        this.dRBVelocities = dRBVelocities;
    }

    public StateDelta multiplySelf(double d) {
        dSPositions.mapMultiplyToSelf(d);
        dSVelocities.mapMultiplyToSelf(d);
        dHPositions.mapMultiplyToSelf(d);
        dHVelocities.mapMultiplyToSelf(d);
        dRBPositions.mapMultiplyToSelf(d);
        dRBVelocities.mapMultiplyToSelf(d);
        return this;
    }

    public StateDelta divideSelf(double d) {
        dSPositions.mapDivideToSelf(d);
        dSVelocities.mapDivideToSelf(d);
        dHPositions.mapDivideToSelf(d);
        dHVelocities.mapDivideToSelf(d);
        dRBPositions.mapDivideToSelf(d);
        dRBVelocities.mapDivideToSelf(d);
        return this;
    }

}
