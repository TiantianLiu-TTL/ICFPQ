package timeEvolvingModel;

import algorithm.CostFunction;
import algorithm.PrintOut;
import datagenerate.DataGen;
import iDModel.GenTopology;
import indoor_entitity.Door;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import simulation.Simulate;
import utilities.Constant;
import utilities.DataGenConstant;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * time-evolving population estimator
 * @author Tiantian Liu
 */

public class TEPM {
    public static int tc;
    public static int ts;
    public static int t;
    public static Partition par;

    public TEPM(int tc, int ts, int t, Partition par) {
        this.tc = tc;
        this.ts = ts;
        this.t = t;
        this.par = par;
    }

    public TEPM(int tc, int t, Partition par) {
        this.tc = tc;
        this.t = t;
        this.par = par;
    }

    public double oneStep(Partition par, int t, int parInterval) {

        int parId = par.getmID();
        double result = 0;
        int tNext = t + parInterval;

        if (par.getPrePop(tNext) == -1) {
            double flowIn = 0;
            double flowOut = 0;
            double currentPop = 0;
            if (t == tc) {
                currentPop = par.getPop(t);
            }
            else {
                currentPop = par.getPrePop(t);
            }

            ArrayList<Integer> doors = par.getmDoors();
            for (int i = 0; i < doors.size(); i++) {
                int doorId = doors.get(i);
                if (DataGenConstant.exitDoors.contains(doorId)) continue;
                int parId2 = -1;
                Door door = IndoorSpace.iDoors.get(doorId);

                if (tNext % door.getTimeInterval() != 0) continue;

                if (door.getmPartitions().get(0) == parId) {
                    parId2 = door.getmPartitions().get(1);
                }
                else {
                    parId2 = door.getmPartitions().get(0);
                }
//                ArrayList<Double> flows = new ArrayList<>();
                double outflow = 0;
                double inflow = 0;
                if (door.getPreFlow(tNext, parId + "-" + parId2) == -1) {
                    outflow = Estimate.estimateFlow(door, parId, parId2, tNext,"possion");
                }
                else {
                    outflow = door.getPreFlow(tNext, parId + "-" + parId2);
                }
                if (door.getPreFlow(tNext, parId2 + "-" + parId) == -1) {
                    inflow = Estimate.estimateFlow(door, parId2, parId, tNext,"possion");
                }
                else {
                    inflow = door.getPreFlow(tNext, parId2 + "-" + parId);
                }
                flowOut += outflow;
                flowIn += inflow;
            }
            result = Merge.mergeFlow(par, tNext, flowIn, flowOut, currentPop);

        } else {
            result = par.getPrePop(tNext);
        }

//        System.out.println("time: " + tNext + " pop: " + result);

        return result;
    }

    public double oneStepNew(Partition par, int tNext, int parInterval) {

        int parId = par.getmID();
        double result = 0;
        int tLast = tNext - parInterval;


        if (par.getPrePop(tNext) == -1) {
            double flowIn = 0;
            double flowOut = 0;
            double currentPop = 0;
            if (tLast == tc) {
                currentPop = par.getPop(tLast);
            }
            else {
                currentPop = oneStepNew(par, tLast, parInterval);
            }

            ArrayList<Integer> doors = par.getmDoors();
            for (int i = 0; i < doors.size(); i++) {
                int doorId = doors.get(i);
                if (DataGenConstant.exitDoors.contains(doorId)) continue;
                int parId2 = -1;
                Door door = IndoorSpace.iDoors.get(doorId);

                if (tNext % door.getTimeInterval() != 0) continue;

                if (door.getmPartitions().get(0) == parId) {
                    parId2 = door.getmPartitions().get(1);
                }
                else {
                    parId2 = door.getmPartitions().get(0);
                }
//                ArrayList<Double> flows = new ArrayList<>();
                double outflow = 0;
                double inflow = 0;
                if (door.getPreFlow(tNext, parId + "-" + parId2) == -1) {
                    outflow = Estimate.estimateFlow(door, parId, parId2, tNext,"possion");
                }
                else {
                    outflow = door.getPreFlow(tNext, parId + "-" + parId2);
                }
                if (door.getPreFlow(tNext, parId2 + "-" + parId) == -1) {
                    Partition par2 = IndoorSpace.iPartitions.get(parId2);
                    oneStepNew(par2, tNext, parInterval);
                    inflow = door.getPreFlow(tNext, parId2 + "-" + parId);
                }
                else {
                    inflow = door.getPreFlow(tNext, parId2 + "-" + parId);
                }
                flowOut += outflow;
                flowIn += inflow;
            }
            result = Merge.mergeFlow(par, tNext, flowIn, flowOut, currentPop);

        } else {
            result = par.getPrePop(tNext);
        }

//        System.out.println("time: " + tNext + " pop: " + result);

        return result;
    }

    public void oneStepAllPars(int t, int parInterval) {
        int tNext = t + parInterval;

        for (int i = 0; i < IndoorSpace.iDoors.size(); i++) {
            int doorId = i;
            if (DataGenConstant.exitDoors.contains(i)) continue;
            Door door = IndoorSpace.iDoors.get(doorId);
            if (tNext % door.getTimeInterval() != 0) continue;
//            System.out.println("doorId: " + doorId);
            Estimate.estimateFlow(door, door.getmPartitions().get(0), door.getmPartitions().get(1), tNext,"possion");
            Estimate.estimateFlow(door, door.getmPartitions().get(1), door.getmPartitions().get(0), tNext,"possion");
        }

        for (int j = 0; j < IndoorSpace.iPartitions.size(); j++) {
            int parId = j;
            Partition par = IndoorSpace.iPartitions.get(parId);

            double flowIn = 0;
            double flowOut = 0;
            double currentPop = 0;
            if (t == tc) {
                currentPop = par.getPop(t);
            }
            else {
                currentPop = par.getPrePop(t);
            }

            ArrayList<Integer> doors = par.getmDoors();
            for (int i = 0; i < doors.size(); i++) {
                int doorId = doors.get(i);
                if (DataGenConstant.exitDoors.contains(doorId)) continue;
                int parId2 = -1;
                Door door = IndoorSpace.iDoors.get(doorId);

                if (tNext % door.getTimeInterval() != 0) continue;

                if (door.getmPartitions().get(0) == parId) {
                    parId2 = door.getmPartitions().get(1);
                }
                else {
                    parId2 = door.getmPartitions().get(0);
                }
                flowOut += door.getPreFlow(tNext, parId + "-" + parId2);
                flowIn += door.getPreFlow(tNext, parId2 + "-" + parId);
            }
            Merge.mergeFlow(par, tNext, flowIn, flowOut, currentPop);
        }

//        System.out.println("all updated");
    }

    public ArrayList<Double> calVolatility(Partition par, int time) {
        ArrayList<Double> result = new ArrayList<Double>();
        int t = time;
        double[] flows = new double[DataGenConstant.volatilityPara];
        double sum = 0;
        for (int i = 0; i < DataGenConstant.volatilityPara; i++) {
            t -= i * DataGenConstant.parInterval;
            if (par.getFlowsInOut(t) == null) {
                continue;
            }
            flows[i] = par.getFlowsInOut(t).get(0) + par.getFlowsInOut(t).get(1);
            sum += flows[i];
        }
        double ave = sum / DataGenConstant.volatilityPara;
        result.add(ave);
        result.add(Math.sqrt(Sample_Variance(flows, ave)));
        return result;
    }

    public double Sample_Variance(double[] data, double ave) {
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance = variance + (Math.pow((data[i] - ave), 2));
        }
        variance = variance / (data.length-1);
        return variance;
    }

    public int mapInterval(double volatility) {
        int result = 0;
        double pVolatility = Math.abs(volatility);
//        if (pVolatility <= 1) {
//            result = (int)Constant.large;
//        }
//        else {
//            result = (int)(log(pVolatility - 1, 0.5) + 2) * DataGenConstant.parInterval;
//        }

        return result;
    }

    public double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }


    public double temModelExact() {
        int tTemp = ts;
        double result = 0;
        while (tTemp < t) {
            oneStepAllPars(tTemp, DataGenConstant.parInterval);
            tTemp += DataGenConstant.parInterval;
        }
//        System.out.println("pop: " + par.getPrePop(t) + " area " + par.getArea());
        result = par.getPrePop(t) / par.getArea();
        return result;

    }

    public double temModelExactNew() {
        double result = 0;
        oneStepNew(par, t, DataGenConstant.parInterval);
        result = par.getPrePop(t) / par.getArea();
        return result;

    }

    public double temModelInExactST() {
        int tTemp = tc;
        double result = 0;
        double pop = 0;
        int parInterval = DataGenConstant.parInterval;
        ArrayList<Double> values = par.getPreVolitality();
        double pVolatility = Math.abs(values.get(1));
        if (pVolatility < DataGenConstant.volatilityTheta) {
            pop = par.getPop(tc) + ((t - tc) / parInterval) * values.get(0);
            result = pop / par.getArea();
//            parInterval = (t - tc) * parInterval;
        }
//        System.out.println("parInterval: " + parInterval);
//        if (parInterval >= (int)Constant.large) {
//            result = par.getPop(tc) / par.getArea();
//        }
        else {
            while (tTemp < t) {
                pop = oneStep(par, tTemp, parInterval);
                tTemp += parInterval;
            }
            result = pop / par.getArea();
        }
        return result;
    }


    public double temModelInExactS() {
        int tTemp = tc;
        double result = 0;
        double pop = 0;
        while (tTemp < t) {
            pop = oneStep(par, tTemp, DataGenConstant.parInterval);
            tTemp += DataGenConstant.parInterval;
        }
//        System.out.println("pop: " + pop + " area " + par.getArea());
        result = pop / par.getArea();
        return result;
    }

    public void preCalVo() {
        ArrayList<Double> values = calVolatility(par, tc);
        par.setPreVolitality(values);
    }

    public double tepModel(String s) {
        double result;
        if (s.equals("exact")) {
            result = temModelExact();
//            System.out.println(par.getPrePop(t));
        }
        else if (s.equals("exactNew")) {
            result = temModelExactNew();
//            System.out.println(par.getPrePop(t));
        }
        else if (s.equals("inexactS")) {
            result = temModelInExactS();
        }
        else if (s.equals("inexactST")){
            result = temModelInExactST();
        }
        else {
            result = -1;
            System.out.println("something wrong with the tepModel----TEPM-tepModel");
        }
//        System.out.println("result: " + result);
        return result;
    }

    public static void main(String arg[]) throws IOException {
        PrintOut printOut = new PrintOut();
        DataGen dataGen = new DataGen();
        dataGen.genAllData(DataGenConstant.dataType, DataGenConstant.divisionType);

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();

        Simulate.simulate_read("syn", DataGenConstant.parInterval);

        System.out.println("read finished");


        TEPM tepm = new TEPM(520, 520, 1700, IndoorSpace.iPartitions.get(300));
        double result = tepm.tepModel("exact");
        System.out.println(IndoorSpace.iPartitions.get(300).getPrePop(550));
        System.out.println(IndoorSpace.iPartitions.get(300).getPrePop(1000));
        System.out.println(IndoorSpace.iPartitions.get(300).getPrePop(1700));
        System.out.println(result);
        System.out.println(IndoorSpace.iPartitions.get(300).getPop(550));
        System.out.println(IndoorSpace.iPartitions.get(300).getPop(1000));
        System.out.println(IndoorSpace.iPartitions.get(300).getPop(1700));

        System.out.println();
        CostFunction.initPrePopFlow();
        TEPM tepm1 = new TEPM(520, 520, 1700, IndoorSpace.iPartitions.get(300));
        double result1 = tepm1.tepModel("exactNew");
        System.out.println(IndoorSpace.iPartitions.get(300).getPrePop(550));
        System.out.println(IndoorSpace.iPartitions.get(300).getPrePop(1000));
        System.out.println(result1);
    }
}
