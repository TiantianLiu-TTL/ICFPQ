package algorithm;

import datagenerate.DataGen;
import datagenerate.HSMDataGenRead;
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
 * baseline (adapt) to process LCPQ
 * @author Tiantian Liu
 */

public class LCPQ_baseline_adap {
    /**
     * find the fastest path between two point
     *
     * @param sPoint
     * @param tPoint
     * @param qTime
     * @return path
     */

    private static int revisitTimes = 0;
    public static String lcpq_adapt(Point sPoint, Point tPoint, int qTime) {
        String result = "";
        int sPointId = IndoorSpace.iDoors.size();
        int tPointId = IndoorSpace.iDoors.size() + 1;
        double objectNum = 0;
        double timeCost = 0;
        String temp = lcpq(sPoint, tPoint, qTime, true, sPointId, -1);
        String[] tempArr = temp.split("\t");
        Partition partition = CommonFunction.locPartition(sPoint);

        int doorId = Integer.parseInt(tempArr[2]);
        Door door = IndoorSpace.iDoors.get(doorId);
        int sampleTime = CommonFunction.findSampleTime(qTime, DataGenConstant.parInterval);
        double density = partition.calDensity(sampleTime);
        objectNum = CostFunction.costLCPQ(partition, sPoint, door, density);
        timeCost = CommonFunction.distv(sPoint, door) / DataGenConstant.traveling_speed;
        double t = qTime + timeCost;
        result += sPointId + "\t" + doorId;
        int parId = -1;

        while (doorId != tPointId) {


            int doorIdOld = doorId;
            Door doorOld = IndoorSpace.iDoors.get(doorIdOld);

            temp = lcpq(door, tPoint, t, false, doorId, parId);
//            System.out.println(temp);
            tempArr = temp.split("\t");
            doorId = Integer.parseInt(tempArr[2]);


//            System.out.println("doorIdOld: " + doorIdOld + " doorId: " + doorId);
            if (doorId == tPointId) {
                partition = CommonFunction.locPartition(tPoint);
                sampleTime = CommonFunction.findSampleTime(t, DataGenConstant.parInterval);
                density = partition.calDensity(sampleTime);
                objectNum += CostFunction.costLCPQ(partition, doorOld, tPoint, density);
                double timeCostTemp = CommonFunction.distv(doorOld, tPoint) / DataGenConstant.traveling_speed;
                timeCost += timeCostTemp;
                t = qTime + timeCost;
                result += "\t" + doorId;
//                System.out.println(objectNum + "\t" + result);
                return objectNum + "\t" + result;
            }
            door = IndoorSpace.iDoors.get(doorId);
            ArrayList<Integer> pars = door.getmPartitions();
//            System.out.println("pars: " + pars);
            ArrayList<Integer> parsOld = doorOld.getmPartitions();
//            System.out.println("parsOld: " + parsOld);
            parId = -1;
            for (int i = 0; i < pars.size(); i++) {
                int par1 = pars.get(i);
//                System.out.println("par1: " + par1);
                for (int j = 0; j < parsOld.size(); j++) {
                    int par2 = parsOld.get(j);
//                    System.out.println("par2: " + par2);
                    if (par1 == par2) {
                        parId = par1;
//                        System.out.println("equal" );
                    }
                }
            }
            if (parId == -1) {
                System.out.println("something wrong with parId");
            }

            sampleTime = CommonFunction.findSampleTime(t, DataGenConstant.parInterval);
            partition = IndoorSpace.iPartitions.get(parId);
            density = partition.calDensity(sampleTime);
            objectNum += CostFunction.costLCPQ(partition, doorOld, door, density);
            double timeCostTemp = partition.getdistMatrix().getDistance(doorIdOld, doorId) / DataGenConstant.traveling_speed;
            timeCost += timeCostTemp;
            t = qTime + timeCost;

            result += "\t" + doorId;

        }

//        result = objectNum + "\t" + result;

        return result;
    }
    public static String lcpq(Point sPoint, Point tPoint, double qTime, boolean isPoint, int sDoorId, int preParId) {
        CostFunction.initPrePopFlow();
        String result = "";
        int sPointId = IndoorSpace.iDoors.size();
        int tPointId = IndoorSpace.iDoors.size() + 1;
        int sampleTime = CommonFunction.findSampleTime(qTime, DataGenConstant.parInterval);

        if (sPoint.equals(tPoint)) {
            return sPoint.eDist(tPoint) + "\t" + sPointId + "\t" + tPointId;
        }
//        int originalSampleTime = CommonFunction.findSampleTime(qTime, DataGenConstant.parInterval);

        ArrayList<Partition> sPartitions = new ArrayList<>();
        Partition tPartition = CommonFunction.locPartition(tPoint);

        if (isPoint) {
            sPartitions.add(CommonFunction.locPartition(sPoint));
            if (CommonFunction.locPartition(sPoint).getmID() == tPartition.getmID()) {
                return sPoint.eDist(tPoint) + "\t" + sPointId + "\t" + tPointId;
            }
        }
        else {
            Door sDoor = IndoorSpace.iDoors.get(sDoorId);
            ArrayList<Integer> sPars = sDoor.getmPartitions();
            for (int i = 0; i < sPars.size(); i++) {
                if (sPars.get(i) == preParId) revisitTimes++;
                if (sPars.get(i) == preParId && revisitTimes > 100) continue;
                sPartitions.add(IndoorSpace.iPartitions.get(sPars.get(i)));
                if (IndoorSpace.iPartitions.get(sPars.get(i)).getmID() == tPartition.getmID()) {
                    return sPoint.eDist(tPoint) + "\t" + sPointId + "\t" + tPointId;
                }

            }
        }
//        Partition sPartition = CommonFunction.locPartition(sPoint);




        ArrayList<Integer> sdoors = new ArrayList<Integer>();
//        sdoors = sPartition.getConnectivityTier().getP2DLeave();
        for (int i = 0; i < sPartitions.size(); i++) {
            sdoors.addAll(sPartitions.get(i).getmDoors());
        }
        ArrayList<Integer> tdoors = new ArrayList<Integer>();
//        edoors = ePartition.getConnectivityTier().getP2DEnter();
        tdoors = tPartition.getmDoors();

        int size = IndoorSpace.iDoors.size() + 2;
        BinaryHeap<Double> H = new BinaryHeap<Double>(size);
        double[] objectNum = new double[size];		//stores the current shortest path distance from source ds to a door de
        ArrayList<ArrayList<Integer>> prev = new ArrayList<>();
        boolean[] visited = new boolean[size];		// mark door as visited

        for (int i = 0; i < size - 2; i++) {
            int doorID = IndoorSpace.iDoors.get(i).getmID();
            if(doorID != i) System.out.println("something wrong_Helper_d2dDist");
            objectNum[i] = Constant.large;

            // enheap
            H.insert(objectNum[i], doorID);

            prev.add(null);
        }

        int ps = size - 2;
        objectNum[ps] = 0;
        H.insert(objectNum[ps], ps); // start point
        prev.add(null);

        int pt = size - 1;
        objectNum[pt] = Constant.large;
        H.insert(objectNum[pt], pt); // end point
        prev.add(null);

//        System.out.println("Heap is ready");
        int h = 0;
        while(H.heapSize > 0) {
//            System.out.println(h++);
            String[] str = H.delete_min().split(",");
            int di = Integer.parseInt(str[1]);
//            System.out.println(di);
            double objectNum_di = Double.parseDouble(str[0]);
            if (objectNum_di == Constant.large) {
                return Double.toString(Constant.large) + "\t";

            }


//			System.out.println("dequeue <" + di + ", " + dist_di + ">");

            visited[di] = true;
//			System.out.println("d" + di + " is newly visited");
            if (di == pt) {
//				System.out.println("d2dDist_ di = " + di + " de = " + de);
                result += CommonFunction.getPath(prev, ps, pt);
                return objectNum_di + "\t" + result;
            }
            int flag = 0;

            for (int i = 0; i < tdoors.size(); i++) {
                if (di == tdoors.get(i)) {
                    flag = 1;
                    Door door = IndoorSpace.iDoors.get(di);
//
                    double density = tPartition.calDensity(sampleTime);
                    double objectNum2 = CostFunction.costLCPQ(tPartition, door, tPoint, density);

                    if ((objectNum[di] + objectNum2) < objectNum[pt]) {
                        double oldDj = objectNum[pt];
                        objectNum[pt] = objectNum[di] + objectNum2;
                        H.updateNode(oldDj, pt, objectNum[pt], pt);
                        prev.set(pt, new ArrayList<>(Arrays.asList(tPartition.getmID(), di)));
                    }
                }
            }
            if (flag == 1) {
                continue;
            }


            if (di != ps) {
                Door door = IndoorSpace.iDoors.get(di);

                ArrayList<Integer> parts = new ArrayList<Integer>();		// list of leavable partitions
//                parts = door.getD2PLeave();
                parts = door.getmPartitions();

                int partSize = parts.size();
                for (int i = 0; i < partSize; i ++) {
                    ArrayList<Integer> doorTemp = new ArrayList<Integer>();
                    int v = parts.get(i);		// partition id
                    if (prev.get(di).get(0) == v) continue;
                    Partition partition = IndoorSpace.iPartitions.get(v);
                    double density = partition.calDensity(sampleTime);

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
                        }
                    }

                }
            }
            else {
                for (int j = 0; j < sdoors.size(); j++) {
                    int dj = sdoors.get(j);
                    if (dj == sDoorId) continue;
//                    System.out.println("dj " + dj);
                    if (DataGenConstant.exitDoors.contains(dj)) continue;
                    Door doorj = IndoorSpace.iDoors.get(dj);
                    if (visited[dj]) System.out.println("something wrong_Helper_d2dDist2");
//					System.out.println("for d" + di + " and d" + dj);
                    for (int f = 0; f < sPartitions.size(); f++) {
                        Partition sPartition = sPartitions.get(f);
                        int v = sPartition.getmID();
                        //                    System.out.println("sampleTime " + sampleTime);
                        double density = sPartition.calDensity(sampleTime);
                        //                    System.out.println("density: " + density);
                        double objectNum1 = CostFunction.costLCPQ(sPartition, sPoint, doorj, density);
                        //                    System.out.println("time1 " + time1);
                        if ((objectNum[di] + objectNum1) < objectNum[dj]) {
                            double oldDj = objectNum[dj];
                            objectNum[dj] = objectNum[di] + objectNum1;
                            H.updateNode(oldDj, dj, objectNum[dj], dj);
                            prev.set(dj, new ArrayList<>(Arrays.asList(v, di)));
                        }
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

        Simulate.simulate_read("syn", DataGenConstant.parInterval);
        System.out.println("read finished");
        PrintOut printOut = new PrintOut();

        CommonFunction.setQType();

        for (int i = 0; i < IndoorSpace.iPartitions.size(); i++) {
            Partition parTemp = IndoorSpace.iPartitions.get(i);
            TEPM tepm = new TEPM(1000, DataGenConstant.endTime, parTemp);
            tepm.preCalVo();
//            double ave = parTemp.getPreVolitality().get(0);
//            double vol = parTemp.getPreVolitality().get(1);

//            System.out.println("parId " + i + " ave " + ave + " vol " + vol);
        }


        String result = LCPQ_baseline_adap.lcpq_adapt(new Point(496.0,620.0,1), new Point(1310.0,1287.0,2), 1000);
        System.out.println(result);
    }

}


