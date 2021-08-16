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
 * baseline (gtg) to process LCPQ
 * @author Tiantian Liu
 */

public class LCPQ_baseline_gtg {

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
    public static String lcpq_baseline_gtg(Point sPoint, Point tPoint, int qTime, String type) {
        CostFunction.initPrePopFlow();
        String result = "";
        double lestObjects = Constant.large;

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
//
            double dist1 = CommonFunction.distv(sPoint, sDoor);
            double objectNum1 = CostFunction.costLCPQ(sPartition, sPoint, sDoor, density);
//
            double sTime = qTime + dist1 / DataGenConstant.traveling_speed;

            int tempUpdateTime = updateTime;

            for (int j = 0; j < tdoors.size(); j++) {
                int tDoorId = tdoors.get(j);
                Door tDoor = IndoorSpace.iDoors.get(tDoorId);
                String result2 = lcpq_d2d(sDoorId, tDoorId, sTime, originalSampleTime, tempUpdateTime, type);
                if (result2.split(" ").length < 3) continue;
                double objectNum2 = Double.parseDouble(result2.split(" ")[0]);
                double dist2 = Double.parseDouble(result2.split(" ")[1]);
                double t = qTime + (dist1 + dist2) / DataGenConstant.traveling_speed;
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
                double objectNum3 = CostFunction.costLCPQ(tPartition, tDoor, tPoint, density);

                double objectCon = objectNum1 + objectNum2 + objectNum3;

                if (objectCon < lestObjects) {
                    lestObjects = objectCon;
                    result = objectCon + "\t" + result2.split(" ")[2];
                }
            }

        }

        return result;
    }

    public static String lcpq_d2d(int doorId1, int doorId2, double sTime, int originalSampleTime, int tempUpdatetime, String type) {
        String result = "";
        updateTime = tempUpdatetime;
        int size = IndoorSpace.iDoors.size();
        BinaryHeap<Double> H = new BinaryHeap<Double>(size);
        double[] objectNum = new double[size];
        double[] dist = new double[size];
        ArrayList<ArrayList<Integer>> prev = new ArrayList<>();
        boolean[] visited = new boolean[size];		// mark door as visited

        for (int i = 0; i < size; i++) {
            int doorID = IndoorSpace.iDoors.get(i).getmID();
            if(doorID != i) System.out.println("something wrong_Helper_d2dDist");
            if (doorID == doorId1) {
                objectNum[i] = 0;
                dist[i] = 0;
            }
            else {
                objectNum[i] = Constant.large;
                dist[i] = Constant.large;
            }

            // enheap
            H.insert(objectNum[i], doorID);

            prev.add(null);
        }

//        System.out.println("Heap is ready");
//        int h = 0;
        while(H.heapSize > 0) {
//            System.out.println("h" + h++);
            String[] str = H.delete_min().split(",");
            int di = Integer.parseInt(str[1]);
//            System.out.println("di: " + di);
            double objectNum_di = Double.parseDouble(str[0]);
            if (objectNum_di == Constant.large) {
                return Double.toString(Constant.large) + "\t";

            }


//			System.out.println("dequeue <" + di + ", " + dist_di + ">");

            visited[di] = true;
//			System.out.println("d" + di + " is newly visited");
            if (di == doorId2) {
//				System.out.println("d2dDist_ di = " + di + " de = " + de);
                result += CommonFunction.getPath(prev, doorId1, doorId2);
                return objectNum_di + " " + dist[di] + " " + result;
            }

            Door door = IndoorSpace.iDoors.get(di);
            double t = dist[di] / DataGenConstant.traveling_speed + sTime;
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
//                double logging = CostFunction.logging(partition, sampleTime, density);

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

                    double d2dObject = CostFunction.costLCPQ(partition, di, dj, density);

                    if ((objectNum[di] + d2dObject) < objectNum[dj]) {
                        double oldDj = objectNum[dj];
                        objectNum[dj] = objectNum[di] + d2dObject;
                        H.updateNode(oldDj, dj, objectNum[dj], dj);
                        prev.set(dj, new ArrayList<>(Arrays.asList(v, di)));
                        dist[dj] = dist[di] + partition.getdistMatrix().getDistance(di, dj);
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

        String result = LCPQ_baseline_gtg.lcpq_baseline_gtg(new Point (1299.0,1127.0,1), new Point (113.0,153.0,2), 500, "exactNew");
        System.out.println(result);
    }
}



