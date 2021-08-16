package algorithm;

import datagenerate.HSMDataGenRead;
import iDModel.GenTopology;
import indoor_entitity.*;
import simulation.Simulate;
import utilities.DataGenConstant;
import utilities.RoomType;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * common functions
 * @author Tiantian Liu
 */

public class CommonFunction {


    /**
     * find the latest sample time
     * @param t
     * @param interval
     * @return
     */
    public static int findSampleTime(double t, int interval) {
        int sampleTime = (((int)t - DataGenConstant.startTime) / interval) * interval + DataGenConstant.startTime;
        return sampleTime;
    }

    /**
     * find latest sample time points
     * @param num
     * @param sampleTime
     * @return
     */
    public static ArrayList<Double> findLastSampleTime(int num, double sampleTime) {
        ArrayList<Double> lastSampleTimeArr = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            double lastSampleTime = sampleTime - 1 * (i + 1);
            BigDecimal b1   =   new   BigDecimal(lastSampleTime);
            lastSampleTime =   b1.setScale(4,   BigDecimal.ROUND_HALF_EVEN).doubleValue();
            lastSampleTimeArr.add(lastSampleTime);

        }
        return lastSampleTimeArr;
    }

    /**
     * find the common door of two partitions
     * @param parId1
     * @param parId2
     * @return
     */
    public static int findCommonDoor(int parId1, int parId2) {
        int result = -1;
        Partition par1 = IndoorSpace.iPartitions.get(parId1);
        Partition par2 = IndoorSpace.iPartitions.get(parId2);
        ArrayList<Integer> doors1 = par1.getmDoors();
        ArrayList<Integer> doors2 = par2.getmDoors();
        for (int i = 0; i < doors1.size(); i++) {
            for (int j = 0; j < doors2.size(); j++) {
                if (doors1.get(i) == doors2.get(j)) {
                    return doors1.get(i);
                }
            }
        }
        return  result;
    }

    /**
     * calculate dist v
     *
     * @param p1
     * @param p2
     * @return distance
     */
    public static double distv(Point p1, Point p2) {
        return p1.eDist(p2);
    }

    /**
     * locate a partition according to location string
     *
     * @param point
     * @return partition
     */
    public static Partition locPartition(Point point) {
        int partitionId = -1;

        int floor = point.getmFloor();
        ArrayList<Integer> pars = IndoorSpace.iFloors.get(floor).getmPartitions();
        for (int i = 0; i < pars.size(); i++) {
            Partition par = IndoorSpace.iPartitions.get(pars.get(i));
            if (point.getX() >= par.getX1() && point.getX() <= par.getX2() && point.getY() >= par.getY1() && point.getY() <= par.getY2()) {
                partitionId = par.getmID();
                return IndoorSpace.iPartitions.get(partitionId);
            }
        }

        return IndoorSpace.iPartitions.get(partitionId);
    }


    /**
     * @param prev
     * @param ds
     * @param de
     * @return a string path
     */
    public static String getPath(ArrayList<ArrayList<Integer>> prev, int ds, int de) {
        String result = de + "";

//		System.out.println("ds = " + ds + " de = " + de + " " + prev[de].par + " " + prev[de].door);
        int currp = prev.get(de).get(0);
        int currd = prev.get(de).get(1);

        while(currd != ds) {
            result = currd + "\t" + result;
//			System.out.println("current: " + currp + ", " + currd + " next: " + prev[currd].toString());
            currp = prev.get(currd).get(0);
            currd = prev.get(currd).get(1);

        }

        result = currd + "\t" + result;

        return result;
    }

    public static void setQType() {
        for (int j = 0; j < IndoorSpace.iPartitions.size(); j++) {
            Partition par = IndoorSpace.iPartitions.get(j);
            par.setqType(RoomType.RANDOM);
            if (DataGenConstant.dataset.equals("syn")) {
                for (int i = 0; i < DataGenConstant.queuePars.size(); i++) {
                    if (par.getmID() % IndoorSpace.iNumberParsPerFloor == DataGenConstant.queuePars.get(i)) {
                        par.setqType(RoomType.QUEUE);
                        break;
                    }
                }
            }
            else if (DataGenConstant.dataset.equals("hsm")) {
                for (int i = 0; i < DataGenConstant.queuePars.size(); i++) {
                    if (par.getmID() % IndoorSpace.iFloors.get(0).getmPartitions().size() == DataGenConstant.queuePars.get(i)) {
                        par.setqType(RoomType.QUEUE);
                        break;
                    }
                }
            }
        }
    }

    public static void main(String arg[]) throws IOException {
        PrintOut printOut = new PrintOut();
        DataGenConstant.init("hsm");
        HSMDataGenRead dateGenRead = new HSMDataGenRead();
        dateGenRead.dataGen("hsm");

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();

        Simulate.simulate_read("hsm", DataGenConstant.parInterval);
        System.out.println("read finished");

        CommonFunction.setQType();

        for (int i = 0; i < IndoorSpace.iPartitions.size(); i++) {
            Partition par = IndoorSpace.iPartitions.get(i);
            System.out.println("qType: " + par.getqType());
        }
    }

}
