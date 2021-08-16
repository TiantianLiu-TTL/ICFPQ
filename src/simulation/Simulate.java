package simulation;

import datagenerate.DataGen;
import iDModel.GenTopology;
import indoor_entitity.Door;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import possion.Possion;
import utilities.Constant;
import utilities.DataGenConstant;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Flow simulation
 * @Author Tiantian Liu
 */
public class Simulate {
    public static String flowInfo = System.getProperty("user.dir") + "/Flow_Pop/flowInfo_";
    public static String popInfo = System.getProperty("user.dir") + "/Flow_Pop/popInfo_";
    public static void simulate_save(String dataset, int parInterval) {
        initialize();
        calPop();
        save(dataset, parInterval);
    }

    public static void simulate_read(String dataset, int parInterval) throws IOException{
        Path path1 = Paths.get(flowInfo + dataset + "_" + parInterval + "_objects_" + DataGenConstant.objects + ".txt");
        Scanner scanner1 = new Scanner(path1);
        int h = 0;
        while (scanner1.hasNextLine()) {
//            System.out.println(h++);
            String line = scanner1.nextLine();
            String [] tempArr = line.split("\t");
            int doorId = Integer.parseInt(tempArr[0]);
            if (doorId > IndoorSpace.iDoors.size() - 1) break;
            int interval = Integer.parseInt(tempArr[1]);
            Door door = IndoorSpace.iDoors.get(doorId);
            door.setTimeInterval(interval);
            for(int i = 2; i < tempArr.length; i++) {
                String [] flowArr = tempArr[i].split(",");
                int t = Integer.parseInt(flowArr[0]);
                String s1 = flowArr[1];
                double flow1 = Double.parseDouble(flowArr[2]);
                String s2 = flowArr[3];
                double flow2 = Double.parseDouble(flowArr[4]);
                door.setFlow(t, s1, flow1);
                door.setFlow(t, s2, flow2);
            }
        }
        System.out.println("flow read finished");

        Path path2 = Paths.get(popInfo  + dataset + "_" + parInterval + "_objects_" + DataGenConstant.objects + ".txt");
        Scanner scanner2 = new Scanner(path2);

        while (scanner2.hasNextLine()) {
            String line = scanner2.nextLine();
            String [] tempArr = line.split("\t");
            int parId = Integer.parseInt(tempArr[0]);
            if (parId > IndoorSpace.iPartitions.size() - 1) break;
            Partition par = IndoorSpace.iPartitions.get(parId);
            for (int i = 1; i < tempArr.length; i++) {
                String [] popArr = tempArr[i].split(",");
                int t = Integer.parseInt(popArr[0]);
                double pop = Double.parseDouble(popArr[1]);
                double flowIn = Double.parseDouble(popArr[2]);
                double flowOut = Double.parseDouble(popArr[3]);
                par.addPop(t, pop);
                par.addFlowsInOut(t, flowIn, flowOut);

            }
        }
        System.out.println("pop read finished");


    }

    public static Boolean save(String dataset, int parInterval) {
        System.out.println();
        System.out.println("save--------------------------");
        try {
            FileWriter fwFlowInfo = new FileWriter(flowInfo + dataset + "_" + parInterval + "_objects_" + DataGenConstant.objects + ".txt");
            Iterator<Door> itrDoor = IndoorSpace.iDoors.iterator();
            String result = "";
            while (itrDoor.hasNext()) {
                Door door = itrDoor.next();
                if (DataGenConstant.exitDoors.contains(door.getmID())) continue;
                System.out.println("doorId " + door.getmID());
                int parId1 = door.getmPartitions().get(0);
                int parId2 = door.getmPartitions().get(1);
                result += door.getmID() + "\t" + door.getTimeInterval() + "\t";
                HashMap<Integer, HashMap<String, Double>> flows = door.getFlows();
                int t = DataGenConstant.startTime + door.getTimeInterval();
                while(t <= DataGenConstant.endTime) {
                    String s1 = parId1 + "-" + parId2;
                    String s2 = parId2 + "-" + parId1;
                    System.out.println("t: " + t);
                    System.out.println("s1: " + s1);
                    System.out.println("s2: " + s2);
                    System.out.println("flows.get(t).get(s1)" + flows.get(t).get(s1));
                    System.out.println("flows.get(t).get(s2)" + flows.get(t).get(s1));
                    result += t + "," + s1 + "," + flows.get(t).get(s1) + "," + s2 + "," + flows.get(t).get(s2) + "\t";

                    t += door.getTimeInterval();
                }
                result += "\n";

            }
            fwFlowInfo.write(result);
            fwFlowInfo.flush();
            fwFlowInfo.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        try {
            FileWriter fwPopInfo = new FileWriter(popInfo + dataset + "_" + parInterval + "_objects_" + DataGenConstant.objects + ".txt");
            Iterator<Partition> itrPar = IndoorSpace.iPartitions.iterator();
            String result = "";
            while (itrPar.hasNext()) {
                Partition par = itrPar.next();
                result += par.getmID() + "\t";
                HashMap<Integer, Double> pop = par.getPop();
                HashMap<Integer, ArrayList<Double>> flowsInOut = par.getFlowsInOut();
                int t = DataGenConstant.startTime;
                flowsInOut.put(t, new ArrayList<>(Arrays.asList((double)0, (double)0)));
                while(t <= DataGenConstant.endTime) {
                    result += t + "," + pop.get(t) + "," + flowsInOut.get(t).get(0) + "," + flowsInOut.get(t).get(1) + "\t";

                    t += DataGenConstant.parInterval;
                }
                result += "\n";

            }
            fwPopInfo.write(result);
            fwPopInfo.flush();
            fwPopInfo.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void initialize() {
        for (int i = 0; i < IndoorSpace.iPartitions.size(); i++) {
            Partition par = IndoorSpace.iPartitions.get(i);
            int iniPop = (int)(Math.random() * DataGenConstant.objects) + 50;
            par.addPop(DataGenConstant.startTime, iniPop);
        }

        for (int i = 0; i < IndoorSpace.iDoors.size(); i++) {
            Door door = IndoorSpace.iDoors.get(i);
            if (DataGenConstant.exitDoors.contains(door.getmID())) continue;
            int iniLamda1 = (int)(Math.random() * 2) + 1;
            int iniLamda2 = (int)(Math.random() * 2) + 1;
            int parId1 = door.getmPartitions().get(0);
            int parId2 = door.getmPartitions().get(1);
            door.setLamda(parId1 + "-" + parId2, iniLamda1);
            door.setLamda(parId2 + "-" + parId1, iniLamda2);
            int interval = DataGenConstant.doorInterval.get((int)(Math.random() * DataGenConstant.doorInterval.size()));
            door.setTimeInterval(interval);
        }
    }

    public static void calPop() {
        int t = DataGenConstant.startTime + DataGenConstant.parInterval;
        while (t <= DataGenConstant.endTime) {
            System.out.println("t: " + t);
            for (int i = 0; i < IndoorSpace.iPartitions.size(); i++) {
                int parId = i;
                System.out.println("parId: " + parId);
                Partition par = IndoorSpace.iPartitions.get(i);
                double currentPop = par.getPop(t - DataGenConstant.parInterval);
                double flowsIn = 0;
                double flowsOut = 0;
                ArrayList<Integer> doors = par.getmDoors();
                System.out.println("doors " + doors);
                for (int j = 0; j < doors.size(); j++) {
                    int doorId = doors.get(j);
                    if (DataGenConstant.exitDoors.contains(doorId)) continue;
                    System.out.println("doorId: " + doorId);
                    Door door = IndoorSpace.iDoors.get(doorId);
                    double doorInterval = door.getTimeInterval();
                    System.out.println("doorInterval: " + doorInterval);
                    if (t % doorInterval == 0) {
                        String sIn;
                        String sOut;
                        if (door.getmPartitions().get(0) == parId) {
                            sIn = door.getmPartitions().get(1) + "-" + door.getmPartitions().get(0);
                            sOut = door.getmPartitions().get(0) + "-" + door.getmPartitions().get(1);
                        }
                        else {
                            sIn = door.getmPartitions().get(0) + "-" + door.getmPartitions().get(1);
                            sOut = door.getmPartitions().get(1) + "-" + door.getmPartitions().get(0);
                        }
                        System.out.println("sIn: " + sIn);
                        System.out.println("sOut: " + sOut);
                        if (door.getFlow(t,sOut) == -1) {
                            double fOut = 0;
                            if ((int)(Math.random() * 10) == 2) {
                                fOut = simulateFlow(door, sOut);
                            }

                            flowsOut += fOut;
                            door.setFlow(t, sOut, fOut);
                            System.out.println("door.getFlow(t, sOut) == -1");
                        }
                        else {
                            double fOut = door.getFlow(t, sOut);
                            flowsOut += fOut;
                            System.out.println("door.getFlow(t, sOut): " + fOut);
                        }

                        if (door.getFlow(t,sIn) == -1) {
                            double fIn = 0;
                            if ((int)(Math.random() * 10) == 2) {
                                fIn = simulateFlow(door, sIn);
                            }
                            flowsIn += fIn;
                            door.setFlow(t, sIn, fIn);
                            System.out.println("door.getFlow(t, sIn) == -1");
                        }
                        else {
                            double fIn = door.getFlow(t, sIn);
                            flowsIn += fIn;
                            System.out.println("door.getFlow(t, sIn): " + fIn);
                        }
                    }
                }
                if (flowsOut > currentPop) {
                    System.out.println("flowsOut > currentPop");
                    for (int j = 0; j < doors.size(); j++) {
                        int doorId = doors.get(j);
                        if (DataGenConstant.exitDoors.contains(doorId)) continue;
                        Door door = IndoorSpace.iDoors.get(doorId);
                        double doorInterval = door.getTimeInterval();
                        if (t % doorInterval == 0) {
                            String sOut;
                            int parId2;
                            if (door.getmPartitions().get(0) == parId) {
                                sOut = door.getmPartitions().get(0) + "-" + door.getmPartitions().get(1);
                                parId2 = door.getmPartitions().get(1);

                            }
                            else {
                                sOut = door.getmPartitions().get(1) + "-" + door.getmPartitions().get(0);
                                parId2 = door.getmPartitions().get(0);
                            }
                            double oldFOut = door.getFlow(t, sOut);
                            double newFOut = oldFOut * (1 - (flowsOut - currentPop) / flowsOut);
                            door.setFlow(t, sOut, newFOut);

                            Partition par2 = IndoorSpace.iPartitions.get(parId2);
                            if (par2.getPop(t) != -1) {
                                par2.addPop(t, par2.getPop(t) - (oldFOut - newFOut));
                            }
                        }
                    }
                    flowsOut = currentPop;
                }

                double newPop = flowsIn + currentPop - flowsOut;
                par.addPop(t, newPop);
                par.addFlowsInOut(t, flowsIn, flowsOut);
                System.out.println();
            }

            t += DataGenConstant.parInterval;
        }
    }

    public static double simulateFlow(Door door, String s) {
        double lamda = door.getLamda(s);
        double flow = Possion.getPossionVariable(lamda);
        return flow;
    }

    public static void main(String arg[]) throws IOException{
        DataGen dataGen = new DataGen();
        dataGen.genAllData(DataGenConstant.dataType, DataGenConstant.divisionType);

        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();

        Simulate.simulate_save("syn", DataGenConstant.parInterval);


    }

}
