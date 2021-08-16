package algorithm;

import datagenerate.DataGen;
import iDModel.GenTopology;
import indoor_entitity.*;
import simulation.Simulate;
import timeEvolvingModel.TEPM;
import utilities.Constant;
import utilities.DataGenConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * baseline (GTG) to process ICFPQ
 * @author Tiantian Liu
 */

public class AlgoICFPQ_baseline_gtg {

    private static HashMap<Integer, HashMap<Double, Double>> possibleDensity = new HashMap<>(); // <partitionId, <sampleTime, possibleDensity>>
    private static int updateTime;
    /**
     * find the fastest path between two point
     *
     * @param sPoint
     * @param tPoint
     * @param qTime
     * @return path
     */
    public static String icfpq_baseline_gtg(Point sPoint, Point tPoint, int qTime, String type) {
        CostFunction.initPrePopFlow();
        String result = "";
        double shortestTime = Constant.large;

        int originalSampleTime = CommonFunction.findSampleTime(qTime, DataGenConstant.parInterval);

        Partition sPartition = CommonFunction.locPartition(sPoint);

        Partition tPartition = CommonFunction.locPartition(tPoint);

        if (sPoint.equals(tPoint)) {
            return sPoint.eDist(tPoint) + "\t";
        }

        if (sPartition.getmID() == tPartition.getmID()) {
            return sPoint.eDist(tPoint) + "\t";
        }


        ArrayList<Integer> sdoors = new ArrayList<Integer>();
//        sdoors = sPartition.getConnectivityTier().getP2DLeave();
        sdoors = sPartition.getmDoors();
        ArrayList<Integer> tdoors = new ArrayList<Integer>();
//        edoors = ePartition.getConnectivityTier().getP2DEnter();
        tdoors = tPartition.getmDoors();

        for (int i = 0; i < sdoors.size(); i++) {
            updateTime = originalSampleTime;
            int sDoorId = sdoors.get(i);
            if (DataGenConstant.exitDoors.contains(sDoorId)) continue;
            Door sDoor = IndoorSpace.iDoors.get(sDoorId);

            int v = sPartition.getmID();
            int sampleTime = CommonFunction.findSampleTime(qTime, DataGenConstant.parInterval);
//                    System.out.println("sampleTime " + sampleTime);
            double density = 0;
            if (sampleTime > originalSampleTime) {
                if (type.equals("exact")) {
                    if (sampleTime > updateTime) {
                        TEPM tepm = new TEPM(originalSampleTime, updateTime, sampleTime,sPartition);
                        density = tepm.tepModel(type);
                        updateTime = sampleTime;
                    }
                    else {
                        density = sPartition.getPrePop(sampleTime) / sPartition.getArea();
                    }
                }
                else {
                    TEPM tepm = new TEPM(originalSampleTime, updateTime, sampleTime,sPartition);
                    density = tepm.tepModel(type);
                }
            }
            else {
//                        System.out.println("calDensity");
                density = sPartition.calDensity(sampleTime);
            }
//                    System.out.println("density: " + density);
            double logging = CostFunction.logging(sPartition, sampleTime, density);
//                    System.out.println("logging " + logging);
            double time1 = CostFunction.cost(sPartition, sPoint, sDoor, logging);
//                    System.out.println("time1 " + time1);
            double sTime = qTime + time1;
            int tempUpdateTime = updateTime;

            for (int j = 0; j < tdoors.size(); j++) {
                int tDoorId = tdoors.get(j);
                Door tDoor = IndoorSpace.iDoors.get(tDoorId);
                String result2 = ICFPQ_d2d(sDoorId, tDoorId, sTime, originalSampleTime, tempUpdateTime, type);
                double time2 = Double.parseDouble(result2.split(" ")[0]);
                double t = qTime + time1 + time2;
                sampleTime = CommonFunction.findSampleTime(t, DataGenConstant.parInterval);
                density = 0;
                if (sampleTime > originalSampleTime) {
                    if (type.equals("exact")) {
                        if (sampleTime > updateTime) {
                            TEPM tepm = new TEPM(originalSampleTime, updateTime, sampleTime, tPartition);
                            density = tepm.tepModel(type);
                            updateTime = sampleTime;
                        }
                        else {
                            density = tPartition.getPrePop(sampleTime) / tPartition.getArea();
                        }
                    }
                    else {
                        TEPM tepm = new TEPM(originalSampleTime, updateTime, sampleTime, tPartition);
                        density = tepm.tepModel(type);
                    }
                }
                else {
                    density = tPartition.calDensity(sampleTime);
                }
//
                double logging2 = CostFunction.logging(tPartition, sampleTime, density);
                double time3 = CostFunction.cost(tPartition, tDoor, tPoint, logging2);

                double timeCost = time1 + time2 + time3;

                if (timeCost < shortestTime) {
                    shortestTime = timeCost;
                    result = timeCost + "\t" + result2.split(" ")[1];
                }
            }

        }

        return result;
    }

    public static String ICFPQ_d2d(int doorId1, int doorId2, double sTime, int originalSampleTime, int tempUpdatetime, String type) {
        String result = "";
        updateTime = tempUpdatetime;
        int size = IndoorSpace.iDoors.size();
        BinaryHeap<Double> H = new BinaryHeap<Double>(size);
        double[] time = new double[size];		//stores the current shortest path distance from source ds to a door de
        ArrayList<ArrayList<Integer>> prev = new ArrayList<>();
        boolean[] visited = new boolean[size];		// mark door as visited

        for (int i = 0; i < size; i++) {
            int doorID = IndoorSpace.iDoors.get(i).getmID();
            if(doorID != i) System.out.println("something wrong_Helper_d2dDist");
            if (doorID == doorId1) {
                time[i] = 0;
            }
            else {
                time[i] = Constant.large;
            }

            // enheap
            H.insert(time[i], doorID);

            prev.add(null);
        }

//        System.out.println("Heap is ready");
//        int h = 0;
        while(H.heapSize > 0) {
//            System.out.println("h" + h++);
            String[] str = H.delete_min().split(",");
            int di = Integer.parseInt(str[1]);
//            System.out.println("di: " + di);
            double time_di = Double.parseDouble(str[0]);
            if (time_di == Constant.large) {
                return Double.toString(Constant.large) + "\t";

            }


//			System.out.println("dequeue <" + di + ", " + dist_di + ">");

            visited[di] = true;
//			System.out.println("d" + di + " is newly visited");
            if (di == doorId2) {
//				System.out.println("d2dDist_ di = " + di + " de = " + de);
                result += CommonFunction.getPath(prev, doorId1, doorId2);
                return time_di + " " + result;
            }

            Door door = IndoorSpace.iDoors.get(di);
            double t = time[di] + sTime;
            int sampleTime = CommonFunction.findSampleTime(t, DataGenConstant.parInterval);

            ArrayList<Integer> parts = new ArrayList<Integer>();		// list of leavable partitions
//                parts = door.getD2PLeave();
            parts = door.getmPartitions();

            int partSize = parts.size();
            for (int i = 0; i < partSize; i ++) {
                ArrayList<Integer> doorTemp = new ArrayList<Integer>();
                int v = parts.get(i);		// partition id
//                    if (prev.get(di).get(0) == v) continue;
                Partition partition = IndoorSpace.iPartitions.get(v);
                double density = 0;
                if (sampleTime > originalSampleTime) {
                    if (type.equals("exact")) {
                        if (sampleTime > updateTime) {
                            TEPM tepm = new TEPM(originalSampleTime, updateTime, sampleTime, partition);
                            density = tepm.tepModel(type);
                            updateTime = sampleTime;
                        }
                        else {
                            density = partition.getPrePop(sampleTime) / partition.getArea();
                        }
                    }
                    else {
                        TEPM tepm = new TEPM(originalSampleTime, updateTime, sampleTime, partition);
                        density = tepm.tepModel(type);
                    }
                }
                else {
                    density = partition.calDensity(sampleTime);
                }
                double logging = CostFunction.logging(partition, sampleTime, density);

                doorTemp = partition.getmDoors();
                ArrayList<Integer> doors = new ArrayList<Integer>();		// list of unvisited leavable doors
                // remove the visited doors
                int doorTempSize = doorTemp.size();
                for (int j = 0; j < doorTempSize; j ++) {
                    int index = doorTemp.get(j);
                    if (index == di) continue;
                    if (DataGenConstant.exitDoors.contains(index)) continue;
//					System.out.println("index = " + index + " " + !visited[index]);
                    if (!visited[index]) {
                        doors.add(index);
                    }
                }
                for (int j = 0; j < doors.size(); j++) {
                    int dj = doors.get(j);


                    if (visited[dj]) System.out.println("something wrong_Helper_d2dDist2");
//					System.out.println("for d" + di + " and d" + dj);

                    double d2dTime = CostFunction.cost(partition, di, dj, logging);

                    if ((time[di] + d2dTime) < time[dj]) {
                        double oldDj = time[dj];
                        time[dj] = time[di] + d2dTime;
                        H.updateNode(oldDj, dj, time[dj], dj);
                        prev.set(dj, new ArrayList<>(Arrays.asList(v, di)));
                    }
                }

            }


        }

        return result;
    }


    public static void main(String arg[]) throws IOException {
        DataGen dataGen = new DataGen();
        dataGen.genAllData(DataGenConstant.dataType, DataGenConstant.divisionType);

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();

        Simulate.simulate_read("syn", 5);

        System.out.println("read finished");

        String result = AlgoICFPQ_baseline_gtg.icfpq_baseline_gtg(new Point (1299.0,1127.0,1), new Point (113.0,153.0,2), 500, "exactNew");
        System.out.println(result);
    }
}


