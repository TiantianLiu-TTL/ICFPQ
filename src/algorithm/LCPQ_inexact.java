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
 * two approximate algorithms to process LCPQ
 * @author Tiantian Liu
 */

public class LCPQ_inexact {


    /**
     * find the fastest path between two point
     *
     * @param sPoint
     * @param tPoint
     * @param qTime
     * @param type
     * @return path
     */
    public static String lcpq_inexact(Point sPoint, Point tPoint, int qTime, String type) {
        ArrayList<Integer> updateTimes = new ArrayList<>();
        CostFunction.initPrePopFlow();
        String result = "";

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

        int size = IndoorSpace.iDoors.size() + 2;
        BinaryHeap<Double> H = new BinaryHeap<Double>(size);
        double[] objectNum = new double[size];
        double[] dist = new double[size];
        ArrayList<ArrayList<Integer>> prev = new ArrayList<>();
        boolean[] visited = new boolean[size];		// mark door as visited

        for (int i = 0; i < size - 2; i++) {
            int doorID = IndoorSpace.iDoors.get(i).getmID();
            if(doorID != i) System.out.println("something wrong_Helper_d2dDist");
            objectNum[i] = Constant.large;
            dist[i] = Constant.large;

            // enheap
            H.insert(objectNum[i], doorID);

            prev.add(null);
        }

        int ps = size - 2;
        objectNum[ps] = 0;
        dist[ps] = 0;
        H.insert(objectNum[ps], ps); // start point
        prev.add(null);

        int pt = size - 1;
        objectNum[pt] = Constant.large;
        dist[pt] = Constant.large;
        H.insert(objectNum[pt], pt); // end point
        prev.add(null);

//        System.out.println("Heap is ready");
        int h = 0;
        while(H.heapSize > 0) {
//            System.out.println(h++);
            String[] str = H.delete_min().split(",");
            int di = Integer.parseInt(str[1]);
//            System.out.println(di);
            double time_di = Double.parseDouble(str[0]);
            if (time_di == Constant.large) {
                return Double.toString(Constant.large) + "\t";

            }


//			System.out.println("dequeue <" + di + ", " + dist_di + ">");

            visited[di] = true;
//			System.out.println("d" + di + " is newly visited");
            if (di == pt) {
//				System.out.println("d2dDist_ di = " + di + " de = " + de);
                result += CommonFunction.getPath(prev, ps, pt);
                return time_di + "\t" + result;
            }
            int flag = 0;

            for (int i = 0; i < tdoors.size(); i++) {
                if (di == tdoors.get(i)) {
                    flag = 1;
                    Door door = IndoorSpace.iDoors.get(di);
                    double t = dist[di] / DataGenConstant.traveling_speed + qTime;
                    int sampleTime = CommonFunction.findSampleTime(t, DataGenConstant.parInterval);
                    double density = 0;
                    if (sampleTime > originalSampleTime) {
                        if (type.equals("inexactT") && updateTimes.contains(sampleTime)) {
                            density = tPartition.getPrePop(sampleTime) / tPartition.getArea();
                        }
                        else {
                            TEPM tepm = new TEPM(originalSampleTime, sampleTime, tPartition);
                            density = tepm.tepModel(type);
                            updateTimes.add(sampleTime);
                        }
                    }
                    else {
                        density = tPartition.calDensity(sampleTime);
                    }
//
//                    double logging = CostFunction.logging(tPartition, sampleTime, density);
                    double objectNum2 = CostFunction.costLCPQ(tPartition, door, tPoint, density);

                    if ((objectNum[di] + objectNum2) < objectNum[pt]) {
                        double oldDj = objectNum[pt];
                        objectNum[pt] = objectNum[di] + objectNum2;
                        H.updateNode(oldDj, pt, objectNum[pt], pt);
                        prev.set(pt, new ArrayList<>(Arrays.asList(tPartition.getmID(), di)));
                        dist[pt] = dist[di] + CommonFunction.distv(door, tPoint);
                    }
                }
            }
            if (flag == 1) {
                continue;
            }


            if (di != ps) {
                Door door = IndoorSpace.iDoors.get(di);
                double t = dist[di] / DataGenConstant.traveling_speed + qTime;
                int sampleTime = CommonFunction.findSampleTime(t, DataGenConstant.parInterval);

                ArrayList<Integer> parts = new ArrayList<Integer>();		// list of leavable partitions
//                parts = door.getD2PLeave();
                parts = door.getmPartitions();

                int partSize = parts.size();
                for (int i = 0; i < partSize; i ++) {
                    ArrayList<Integer> doorTemp = new ArrayList<Integer>();
                    int v = parts.get(i);		// partition id
                    if (prev.get(di).get(0) == v) continue;
                    Partition partition = IndoorSpace.iPartitions.get(v);
                    double density = 0;
                    if (sampleTime > originalSampleTime) {
                        if (type.equals("inexactT") && updateTimes.contains(sampleTime)) {
                            density = partition.getPrePop(sampleTime) / partition.getArea();
                        }
                        else {
                            TEPM tepm = new TEPM(originalSampleTime, sampleTime, partition);
                            density = tepm.tepModel(type);
                            updateTimes.add(sampleTime);
                        }
//                        TEPM tepm = new TEPM(originalSampleTime, sampleTime, partition);
//                        density = tepm.tepModel(type);
                    }
                    else {
                        density = partition.calDensity(sampleTime);
                    }
//                    double logging = CostFunction.logging(partition, sampleTime, density);

                    doorTemp = partition.getmDoors();
                    ArrayList<Integer> doors = new ArrayList<Integer>();		// list of unvisited leavable doors
                    // remove the visited doors
                    int doorTempSize = doorTemp.size();
                    for (int j = 0; j < doorTempSize; j ++) {
                        int index = doorTemp.get(j);
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

                        double d2dObjectNum = CostFunction.costLCPQ(partition, di, dj, density);

                        if ((objectNum[di] + d2dObjectNum) < objectNum[dj]) {
                            double oldDj = objectNum[dj];
                            objectNum[dj] = objectNum[di] + d2dObjectNum;
                            H.updateNode(oldDj, dj, objectNum[dj], dj);
                            prev.set(dj, new ArrayList<>(Arrays.asList(v, di)));
                            dist[dj] = dist[di] + partition.getdistMatrix().getDistance(di, dj);
                        }
                    }

                }
            }
            else {
                for (int j = 0; j < sdoors.size(); j++) {
                    int dj = sdoors.get(j);
//                    System.out.println("dj " + dj);
                    if (DataGenConstant.exitDoors.contains(dj)) continue;
                    Door doorj = IndoorSpace.iDoors.get(dj);
                    if (visited[dj]) System.out.println("something wrong_Helper_d2dDist2");
//					System.out.println("for d" + di + " and d" + dj);
                    int v = sPartition.getmID();
                    int sampleTime = CommonFunction.findSampleTime(qTime, DataGenConstant.parInterval);
//                    System.out.println("sampleTime " + sampleTime);
                    double density = 0;
                    if (sampleTime > originalSampleTime) {
                        if (type.equals("inexactT") && updateTimes.contains(sampleTime)) {
                            density = sPartition.getPrePop(sampleTime) / sPartition.getArea();
                        }
                        else {
                            TEPM tepm = new TEPM(originalSampleTime, sampleTime, sPartition);
                            density = tepm.tepModel(type);
                            updateTimes.add(sampleTime);
                        }
//                        TEPM tepm = new TEPM(originalSampleTime, sampleTime, sPartition);
//                        density = tepm.tepModel(type);
                    }
                    else {
//                        System.out.println("calDensity");
                        density = sPartition.calDensity(sampleTime);
                    }
//                    System.out.println("density: " + density);
//                    double logging = CostFunction.logging(sPartition, sampleTime, density);
//                    System.out.println("logging " + logging);
                    double objectNum1 = CostFunction.costLCPQ(sPartition, sPoint, doorj, density);
//                    System.out.println("time1 " + time1);
                    if ((objectNum[di] + objectNum1) < objectNum[dj]) {
                        double oldDj = objectNum[dj];
                        objectNum[dj] = objectNum[di] + objectNum1;
                        H.updateNode(oldDj, dj, objectNum[dj], dj);
                        prev.set(dj, new ArrayList<>(Arrays.asList(v, di)));
                        dist[dj] = dist[di] + CommonFunction.distv(sPoint, doorj);
                    }
                }

            }

        }

        return result;
    }



//    /**
//     * calculate possible density of a partition
//     * @param parId
//     * @param sampleTime
//     * @param originalSampleTime
//     * @return
//     */
//    public static double calPossibleDensity(int parId, double sampleTime, double originalSampleTime) {
//        double result = 0;
//        Partition par = IndoorSpace.iPartitions.get(parId);
//        ArrayList<Integer> objects = getObjectSet(parId, sampleTime, originalSampleTime);
//        for (int objectId: objects) {
//            IndoorObject object = IndoorSpace.iObject.get(objectId);
//            IndoorUR indoorUR = new IndoorUR(object, sampleTime, originalSampleTime, 1);
//            double presentPossi = indoorUR.getUnionPart(par);
//            result += presentPossi;
//        }
//        return result;
//    }

//    /**
//     * get objects in buffer region
//     * @param parId
//     * @param sampleTime
//     * @param originalSampleTime
//     * @return
//     */
//    public static ArrayList<Integer> getObjectSet(int parId, double sampleTime, double originalSampleTime) {
//        ArrayList<Integer> objects = new ArrayList<>();
//        Partition par = IndoorSpace.iPartitions.get(parId);
//        double maxMovingDist = (sampleTime - originalSampleTime) * DataGenConstant.traveling_speed;
//        IndoorBufferRegion indoorBufferRegion = new IndoorBufferRegion(par, maxMovingDist);
//        ArrayList<Partition> fullyCoveredPars = indoorBufferRegion.getFullyCoveredPar();
//        ArrayList<ArrayList<Double>> overlappedPars = indoorBufferRegion.getOverlappedPar();
//        for (Partition fullPar: fullyCoveredPars) {
//            objects.addAll(fullPar.getSampleObjects(originalSampleTime));
//        }
//        for (ArrayList<Double> overlapParInfor: overlappedPars) {
//            int nextDoorId = (int)(double)overlapParInfor.get(0);
//            int nextParId = (int)(double)overlapParInfor.get(1);
//            double remainDist = overlapParInfor.get(2);
//            Door nextDoor = IndoorSpace.iDoors.get(nextDoorId);
//            Partition nextPar = IndoorSpace.iPartitions.get(nextParId);
//            ArrayList<Integer> canObjects = nextPar.getSampleObjects(originalSampleTime);
//            for (int canObjectId: canObjects) {
//                IndoorObject canObject = IndoorSpace.iObject.get(canObjectId);
//                if (canObject.eDist(nextDoor) <= remainDist) {
//                    objects.add(canObjectId);
//                }
//            }
//        }
//        objects.addAll(par.getSampleObjects(originalSampleTime));
//
//        return objects;
//    }

    public static void main(String arg[]) throws IOException {
        DataGen dataGen = new DataGen();
        dataGen.genAllData(DataGenConstant.dataType, DataGenConstant.divisionType);

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();

        Simulate.simulate_read("syn", 5);

        System.out.println("read finished");

        String result = AlgoICFPQ_inexact.icfpq_inexact(new Point (3, 3, 0), new Point (450, 450, 0), 500, "nonExactS");
        System.out.println(result);
    }
}


