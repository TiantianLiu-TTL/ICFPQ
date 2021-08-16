package timeEvolvingModel;

import indoor_entitity.Door;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import utilities.DataGenConstant;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * merge flow for a partition
 * @author Tiantian Liu
 */

public class Merge {

    public static double mergeFlow(Partition par, int tNext, double flowIn, double flowOut, double currentPop) {
        double result = 0;

        int parId = par.getmID();

        if (currentPop >= flowOut) {
            result = currentPop - flowOut + flowIn;
        }
        else {
//            System.out.println("revise");
            double newFlowOut = currentPop;
            result = flowIn;

            double margin = flowOut - newFlowOut;
            ArrayList<Integer> doors = par.getmDoors();
            double aveMargin = margin / flowOut;

            for (int i = 0; i < doors.size(); i++) {
                int doorId = doors.get(i);
                if (DataGenConstant.exitDoors.contains(doorId)) continue;
                Door door = IndoorSpace.iDoors.get(doorId);

                if (tNext % door.getTimeInterval() != 0) continue;

                int parId2 = -1;
                if (door.getmPartitions().get(0) == parId) {
                    parId2 = door.getmPartitions().get(1);
                }
                else {
                    parId2 = door.getmPartitions().get(0);
                }
                double oldFlow = door.getPreFlow(tNext, parId + "-" +parId2);
                double newFlow = oldFlow - oldFlow * aveMargin;

                door.addPreFlow(tNext, parId + "-" + parId2, newFlow);

                Partition par2 = IndoorSpace.iPartitions.get(parId2);
                if (par2.getPrePop(tNext) != -1) {
                    par2.addPrePop(tNext, par2.getPrePop(tNext) - oldFlow * aveMargin);
                }

            }
        }

        par.addPrePop(tNext, result);

        return result;
    }
}
