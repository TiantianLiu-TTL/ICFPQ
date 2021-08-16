package algorithm;
/**
 *
 */

import java.util.ArrayList;

import utilities.Constant;
import utilities.Functions;

/**
 * @author Tiantian
 *
 */
public class Stamp {
    public int doorId;

    public double D; // distance
    public double pop; // population;
    
    public int life = -2;

    /**
     * Constructor
     *
     */
    public Stamp(int doorId, double dist, double pop) {
        this.doorId = doorId;
        this.D = dist;
        this.pop = pop;
    }

    /**
     * Constructor
     *
     */
    public Stamp(Stamp stamp) {
        this.doorId = stamp.doorId;
        this.D = stamp.D;
        this.pop = stamp.pop;

        this.life = -2;
    }


    /**
     * set doorId
     *
     * @param doorId
     */
    public void setDoorId(int doorId) {
        this.doorId = doorId;
    }

    /**
     * set D
     *
     * @param D
     */
    public void setD(double D) {
        this.D = D;
    }

    public boolean equals(Stamp another) {
        if (this.doorId != another.doorId) return false;
        if (this.pop != another.pop) return false;
        if (this.D != another.D) return false;
        return true;
    }


}

