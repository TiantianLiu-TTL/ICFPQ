package timeEvolvingModel;

import indoor_entitity.Door;
import utilities.DataGenConstant;


/**
 * Flow Estimator
 * @author Tiantian Liu
 */

public class Estimate {

    public static double estimateFlow(Door door, int parId1, int parId2, int t, String method) {
        int originalT = t;

        double result = 0;
        double flowSum1 = 0;
//        double flowSum2 = 0;
//        System.out.println("doorId: " + door.getmID());
        for (int i = 1; i <= DataGenConstant.destributionPara; i++) {
            t = t - door.getTimeInterval();
//            System.out.println("t: " + t);
            double tempFlow1;
            if (t <= TEPM.tc) {
                tempFlow1 = door.getFlow(t, parId1 + "-" + parId2);
                if (tempFlow1 == -1) {
                    System.out.println("something wrong with door's flowInfo in Estimate-estimeteFlow");
                }
            }
            else {
                if (door.getPreFlow(t, parId1 + "-" +parId2) == -1) {
                    i--;
//                    System.out.println("no value in preFlows");
                    continue;
                }
                tempFlow1 = door.getPreFlow(t, parId1 + "-" +parId2);
            }
            flowSum1 += tempFlow1;

        }

        if (method.equals("possion")) {
            result = flowSum1 / DataGenConstant.destributionPara;
        }

        door.addPreFlow(originalT, parId1 + "-" + parId2, result);
//        System.out.println("get from pre flows: " + door.getPreFlow(originalT, parId1 + "-" + parId2));
//        door.addPreFlow(originalT, parId2 + "-" + parId1, flowSum2 / DataGenConstant.destributionPara);
//        System.out.println("doorFlow: " + result);

        return  result;
    }
}
