package algorithm;

import indoor_entitity.Door;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import indoor_entitity.Point;
import utilities.DataGenConstant;
import utilities.RoomType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * calculate the speed according to the crowd's type and density
 * @author Tiantian Liu
 */

public class CostFunction {
    public static double logging(Partition par, int t, double density) {
        double result;
//        Boolean isQueue = false;
//
//        if (DataGenConstant.dataset.equals("syn")) {
//            for (int i = 0; i < DataGenConstant.queuePars.size(); i++) {
//                if (par.getmID() % IndoorSpace.iNumberParsPerFloor == DataGenConstant.queuePars.get(i)) {
//                    isQueue = true;
//                    break;
//                }
//            }
//        }
//        else if (DataGenConstant.dataset.equals("hsm")) {
//            for (int i = 0; i < DataGenConstant.queuePars.size(); i++) {
//                if (par.getmID() % IndoorSpace.iFloors.get(0).getmPartitions().size() == DataGenConstant.queuePars.get(i)) {
//                    isQueue = true;
//                    break;
//                }
//            }
//        }


        if (par.getqType() == RoomType.QUEUE) {
            result = 1 + Math.pow(Math.E, density / par.getMaxPop());
//            System.out.println("isQueue");
        }
        else {
            result = 1 + Math.pow(Math.E, Math.pow(density / par.getMaxPop(), 2));
//            System.out.println("notQueue");
        }

        return result;
    }

    public static double cost(Partition par, Point point1, Point point2, double logging) {
        double result = CommonFunction.distv(point1, point2) / DataGenConstant.traveling_speed * logging;
        return result;
    }

    public static double cost(Partition par, int doorId1, int doorId2, double logging) {
        double result = par.getdistMatrix().getDistance(doorId1, doorId2) / DataGenConstant.traveling_speed * logging;
        return result;
    }

    public static double costLCPQ(Partition par, Point point1, Point point2, double density) {
        double result;

        if (par.getqType() == RoomType.RANDOM) {
            result = density * CommonFunction.distv(point1, point2) * 1;
//            result = density * par.getArea();
        }
        else {
            double dist;
            double distX = Math.abs(par.getX2() - par.getX1());
            double distY = Math.abs(par.getY2() - par.getY1());
            if (distX >= distY) {
                dist = distX;
            }
            else {
                dist = distY;
            }
            result = density * par.getArea() * (2.0 / dist);
//            result = 2;
        }
        return result;
    }

    public static double costLCPQ(Partition par, int doorId1, int doorId2, double density) {
        double result;
        if (par.getqType() == RoomType.RANDOM) {
            result = density * par.getdistMatrix().getDistance(doorId1, doorId2) * 1;
//            result = density * par.getArea();
        }
        else {
            double dist;
            double distX = Math.abs(par.getX2() - par.getX1());
            double distY = Math.abs(par.getY2() - par.getY1());
            if (distX >= distY) {
                dist = distX;
            }
            else {
                dist = distY;
            }
            result = density * par.getArea() * (2.0 / dist);
//            result = 2;
        }
        return result;
    }

    public static void initPopFlow() {
        for (int i = 0; i < IndoorSpace.iPartitions.size(); i++) {
            int parId = i;
            Partition par = IndoorSpace.iPartitions.get(parId);
            par.setPop(new HashMap<Integer, Double>());
            par.setFlowsInOut(new HashMap<>());
        }
        for (int i = 0; i < IndoorSpace.iDoors.size(); i++) {
            int doorId = i;
            Door door = IndoorSpace.iDoors.get(doorId);
            door.setFlows(new HashMap<Integer, HashMap<String, Double>>());
        }
    }

    public static void initPrePopFlow() {
        for (int i = 0; i < IndoorSpace.iPartitions.size(); i++) {
            int parId = i;
            Partition par = IndoorSpace.iPartitions.get(parId);
            par.clearPrePop();
            par.clearPreFlowsInOut();
        }
        for (int i = 0; i < IndoorSpace.iDoors.size(); i++) {
            int doorId = i;
            Door door = IndoorSpace.iDoors.get(doorId);
            door.clearPreFlows();
        }
    }
}
