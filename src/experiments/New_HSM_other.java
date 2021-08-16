package experiments;

import algorithm.*;
import datagenerate.HSMDataGenRead;
import iDModel.GenTopology;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import indoor_entitity.Point;
import simulation.Simulate;
import timeEvolvingModel.TEPM;
import utilities.DataGenConstant;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * experiments to test effect of other parameters
 * @author Tiantian Liu
 */
public class New_HSM_other {
    public static int queryTime = 1300;

    public static void run(String outFileTime, String outFileMemory, String outFileAcc, String outFileHit, String queryType) throws IOException {
        String resultTime = "distance" + "\t" + "exactNew" + "\t" + "exact" + "\t" + "inexactS" + "\t" + "inexactST" + "\t" + "baseline" + "\t" + "adapt" + "\n";
        String resultMemo = "distance" + "\t" + "exactNew" + "\t" + "exact" + "\t" + "inexactS" + "\t" + "inexactST" + "\t" + "baseline" + "\t" + "adapt" + "\n";
        String resultAcc = "distance" + "\t" + "exact" + "\t" + "inexactS" + "\t" + "inexactST" + "\t" + "adapt" + "\n";
        String resultHit = "distance" + "\t" + "exact" + "\t" + "inexactS" + "\t" + "inexactST" + "\t" + "adapt" + "\n";

        ArrayList<ArrayList<Point>> pointPairs = new ArrayList<>();
//        ArrayList<Integer> distances = new ArrayList<>(Arrays.asList(900, 1100, 1300, 1500, 1700));
        ArrayList<Integer> distances = new ArrayList<>(Arrays.asList(1100));
        for (int i = 0; i < distances.size(); i++) {
            resultTime += distances.get(i) + "\t";
            resultMemo += distances.get(i) + "\t";
            resultAcc += distances.get(i) + "\t";
            resultHit += distances.get(i) + "\t";

            ArrayList<Long> arrTime1 = new ArrayList<>();
            ArrayList<Long> arrTime2 = new ArrayList<>();
            ArrayList<Long> arrTime3 = new ArrayList<>();
            ArrayList<Long> arrTime4 = new ArrayList<>();
            ArrayList<Long> arrTime5 = new ArrayList<>();
            ArrayList<Long> arrTime6 = new ArrayList<>();

            ArrayList<Long> arrMem1 = new ArrayList<>();
            ArrayList<Long> arrMem2 = new ArrayList<>();
            ArrayList<Long> arrMem3 = new ArrayList<>();
            ArrayList<Long> arrMem4 = new ArrayList<>();
            ArrayList<Long> arrMem5 = new ArrayList<>();
            ArrayList<Long> arrMem6 = new ArrayList<>();

            ArrayList<Double> arrAcc1 = new ArrayList<>();
            ArrayList<Double> arrAcc2 = new ArrayList<>();
            ArrayList<Double> arrAcc3 = new ArrayList<>();
            ArrayList<Double> arrAcc4 = new ArrayList<>();
            ArrayList<Double> arrAcc6 = new ArrayList<>();

            int notHit1 = 0;
            int notHit2 = 0;
            int notHit4 = 0;
            int notHit6 = 0;

            pointPairs = New_HSM_other.getPointPairs(distances.get(i));

            for (int j = 0; j < pointPairs.size(); j++) {
                Point ps = pointPairs.get(j).get(0);
                Point pe = pointPairs.get(j).get(1);

                for (int h = 0; h < 1; h++) {
                    // base
                    String result0 = "";
                    if (queryType.equals("icaspq")) {
                        result0 = LCPQ_base.lcpq_base(ps, pe, queryTime);
                    } else if (queryType.equals("icfpq")) {
                        result0 = AlgoICFPQ_base.icfpq_base(ps, pe, queryTime);
                    } else {
                        System.out.println("something wrong with the query type");
                    }
                    System.out.println("base " + result0);

                    // exact
                    Runtime runtime1 = Runtime.getRuntime();
                    runtime1.gc();
                    String result1 = "";
                    long startMem1 = runtime1.totalMemory() - runtime1.freeMemory();
                    long startTime1 = System.currentTimeMillis();
                    if (queryType.equals("icaspq")) {
                        result1 = LCPQ_exact.lcpq_exact(ps, pe, queryTime, "exactNew");
                    } else if (queryType.equals("icfpq")) {
                        result1 = AlgoICFPQ_exact.icfpq_exact(ps, pe, queryTime, "exactNew");
                    } else {
                        System.out.println("something wrong with the query type");
                    }
                    System.out.println("exactNew " + result1);
                    long endTime1 = System.currentTimeMillis();
                    long endMem1 = runtime1.totalMemory() - runtime1.freeMemory();
                    long mem1 = (endMem1 - startMem1) / 1024 / 1024;
                    long time1 = endTime1 - startTime1;
                    arrTime1.add(time1);
                    arrMem1.add(mem1);
//                    System.out.println("exact memory " + mem1);

                    // inexactS
                    Runtime runtime2 = Runtime.getRuntime();
                    runtime2.gc();
                    String result2 = "";
                    long startMem2 = runtime2.totalMemory() - runtime2.freeMemory();
                    long startTime2 = System.currentTimeMillis();
                    if (queryType.equals("icaspq")) {
                        result2 = LCPQ_inexact.lcpq_inexact(ps, pe, queryTime, "inexactS");
                    } else if (queryType.equals("icfpq")) {
                        result2 = AlgoICFPQ_inexact.icfpq_inexact(ps, pe, queryTime, "inexactS");
                    } else {
                        System.out.println("something wrong with the query type");
                    }
                    System.out.println("inexactS " + result2);
                    long endTime2 = System.currentTimeMillis();
                    long endMem2 = runtime2.totalMemory() - runtime2.freeMemory();
                    long mem2 = (endMem2 - startMem2) / 1024 / 1024;
                    long time2 = endTime2 - startTime2;
                    arrTime2.add(time2);
                    arrMem2.add(mem2);
//                    System.out.println("inexactS memory " + mem2);

                    // exact
                    Runtime runtime3 = Runtime.getRuntime();
                    runtime3.gc();
                    String result3 = "";
                    long startMem3 = runtime3.totalMemory() - runtime3.freeMemory();
                    long startTime3 = System.currentTimeMillis();
                    if (queryType.equals("icaspq")) {
                        result3 = LCPQ_exact.lcpq_exact(ps, pe, queryTime, "exactNew");
                    } else if (queryType.equals("icfpq")) {
                        result3 = AlgoICFPQ_exact.icfpq_exact(ps, pe, queryTime, "exactNew");
                    }
                    System.out.println("exact " + result3);
                    long endTime3 = System.currentTimeMillis();
                    long endMem3 = runtime3.totalMemory() - runtime3.freeMemory();
                    long mem3 = (endMem3 - startMem3) / 1024 / 1024;
                    long time3 = endTime3 - startTime3;
                    arrTime3.add(time3);
                    arrMem3.add(mem3);
////                    System.out.println("inexactT memory " + mem3);

                    // inexactST
                    Runtime runtime4 = Runtime.getRuntime();
                    runtime4.gc();
                    String result4 = "";
                    long startMem4 = runtime4.totalMemory() - runtime4.freeMemory();
                    long startTime4 = System.currentTimeMillis();
                    if (queryType.equals("icaspq")) {
                        result4 = LCPQ_inexact.lcpq_inexact(ps, pe, queryTime, "inexactST");
                    } else if (queryType.equals("icfpq")) {
                        result4 = AlgoICFPQ_inexact.icfpq_inexact(ps, pe, queryTime, "inexactST");
                    } else {
                        System.out.println("something wrong with the query type");
                    }
                    System.out.println("inexactST " + result4);
                    long endTime4 = System.currentTimeMillis();
                    long endMem4 = runtime4.totalMemory() - runtime4.freeMemory();
                    long mem4 = (endMem4 - startMem4) / 1024 / 1024;
                    System.out.println("(endMem4 - startMem4): " + (endMem4 - startMem4) + " mem4: " + mem4);
                    long time4 = endTime4 - startTime4;
                    arrTime4.add(time4);
                    arrMem4.add(mem4);
//                    System.out.println("inexactST memory " + mem4);

                    // baseline
                    Runtime runtime5 = Runtime.getRuntime();
                    runtime4.gc();
                    String result5 = "";
                    long startMem5 = runtime5.totalMemory() - runtime5.freeMemory();
                    long startTime5 = System.currentTimeMillis();
                    if (queryType.equals("icaspq")) {
                        result5 = LCPQ_baseline_gtg.lcpq_baseline_gtg(ps, pe, queryTime, "exact");
                    } else if (queryType.equals("icfpq")) {
                        result5 = AlgoICFPQ_baseline_gtg.icfpq_baseline_gtg(ps, pe, queryTime, "exact");
                    } else {
                        System.out.println("something wrong with the query type");
                    }
                    System.out.println("baseline " + result5);
                    long endTime5 = System.currentTimeMillis();
                    long endMem5 = runtime5.totalMemory() - runtime5.freeMemory();
                    long mem5 = (endMem5 - startMem5) / 1024 / 1024;
                    long time5 = endTime5 - startTime5;
                    arrTime5.add(time5);
                    arrMem5.add(mem5);
//                    System.out.println("inexactST memory " + mem4);

                    // adapt
                    Runtime runtime6 = Runtime.getRuntime();
                    runtime6.gc();
                    String result6 = "";
                    long startMem6 = runtime6.totalMemory() - runtime6.freeMemory();
                    long startTime6 = System.currentTimeMillis();
                    if (queryType.equals("icaspq")) {
                        result6 = LCPQ_baseline_adap.lcpq_adapt(ps, pe, queryTime);
                    } else if (queryType.equals("icfpq")) {
                        result6 = AlgoICFPQ_baseline_adap.icfpq_adapt(ps, pe, queryTime);
                    } else {
                        System.out.println("something wrong with the query type");
                    }
                    System.out.println("baseline " + result6);
                    long endTime6 = System.currentTimeMillis();
                    long endMem6 = runtime6.totalMemory() - runtime6.freeMemory();
                    long mem6 = (endMem6 - startMem6) / 1024 / 1024;
                    long time6 = endTime6 - startTime6;
                    arrTime6.add(time6);
                    arrMem6.add(mem6);

                    if (h == 0) {
                        String[] resultArr0 = result0.split("\t");
                        String[] resultArr1 = result1.split("\t");
                        String[] resultArr2 = result2.split("\t");
                        String[] resultArr4 = result4.split("\t");
                        String[] resultArr6 = result6.split("\t");
                        double value0 = Double.parseDouble(resultArr0[0]);
                        double value1 = Double.parseDouble(resultArr1[0]);
                        double value2 = Double.parseDouble(resultArr2[0]);
//                        double value3 = Double.parseDouble(result3.split("\t")[0]);
                        double value4 = Double.parseDouble(resultArr4[0]);
                        double value6 = Double.parseDouble(resultArr6[0]);
//                        System.out.println(value1 + " " + value2 + " " + value3 + " " + value4);
                        arrAcc1.add(Math.abs(value1 - value0) / value0);
                        arrAcc2.add(Math.abs(value2 - value0) / value0);
//                        arrAcc3.add(Math.abs(value3 - value1) / value1);
                        arrAcc4.add(Math.abs(value4 - value0) / value0);
                        arrAcc6.add(Math.abs(value6 - value0) / value0);
//                        System.out.println("arrAcc2 " + arrAcc2);
//                        System.out.println("arrAcc3 " + arrAcc3);
//                        System.out.println("arrAcc4 " + arrAcc4);
                        int start = 0;
                        if (queryType.equals("icafpq")) {
                            start = 2;
                        } else {
                            start = 1;
                        }

                        if (resultArr0.length != resultArr1.length) {
                            notHit1++;
                        } else {
                            for (int f = start; f < resultArr1.length; f++) {
                                if (!resultArr0[f].equals(resultArr1[f])) {
                                    notHit1++;
                                    break;
                                }
                            }
                        }

                        if (resultArr0.length != resultArr2.length) {
                            notHit2++;
                        } else {
                            for (int f = start; f < resultArr2.length; f++) {
                                if (!resultArr0[f].equals(resultArr2[f])) {
                                    notHit2++;
                                    break;
                                }
                            }
                        }

                        if (resultArr0.length != resultArr4.length) {
                            notHit4++;
                        } else {
                            for (int f = start; f < resultArr4.length; f++) {
                                if (!resultArr0[f].equals(resultArr4[f])) {
                                    notHit4++;
                                    break;
                                }
                            }
                        }

                        if (resultArr0.length != resultArr6.length) {
                            notHit6++;
                        } else {
                            for (int f = start; f < resultArr6.length; f++) {
                                if (!resultArr0[f].equals(resultArr6[f])) {
                                    notHit6++;
                                    break;
                                }
                            }
                        }

                    }
                }
            }
            ArrayList<ArrayList<Long>> arrTimeAll = new ArrayList<>();
            arrTimeAll.add(arrTime1);
            arrTimeAll.add(arrTime3);
            arrTimeAll.add(arrTime2);
            arrTimeAll.add(arrTime4);
            arrTimeAll.add(arrTime5);
            arrTimeAll.add(arrTime6);

            for (int j = 0; j < arrTimeAll.size(); j++) {
                long sum = 0;
                long ave = 0;
                for (int h = 0; h < arrTimeAll.get(j).size(); h++) {
                    sum += arrTimeAll.get(j).get(h);
                }
                ave = sum / arrTimeAll.get(j).size();
                resultTime += ave + "\t";
            }
            resultTime += "\n";

            ArrayList<ArrayList<Long>> arrMemAll = new ArrayList<>();
            arrMemAll.add(arrMem1);
            arrMemAll.add(arrMem3);
            arrMemAll.add(arrMem2);
            arrMemAll.add(arrMem4);
            arrMemAll.add(arrMem5);
            arrMemAll.add(arrMem6);

            for (int j = 0; j < arrMemAll.size(); j++) {
                long sum = 0;
                long ave = 0;
                for (int h = 0; h < arrMemAll.get(j).size(); h++) {
                    sum += arrMemAll.get(j).get(h);
                }
                ave = sum / arrMemAll.get(j).size();
                resultMemo += ave + "\t";
            }
            resultMemo += "\n";

            ArrayList<ArrayList<Double>> arrAccAll = new ArrayList<>();
            arrAccAll.add(arrAcc1);
            arrAccAll.add(arrAcc2);
//            arrAccAll.add(arrAcc3);
            arrAccAll.add(arrAcc4);
            arrAccAll.add(arrAcc6);

            for (int j = 0; j < arrAccAll.size(); j++) {
                double sum = 0;
                double ave = 0;
                for (int h = 0; h < arrAccAll.get(j).size(); h++) {
                    sum += arrAccAll.get(j).get(h);
                }
                ave = sum / arrAccAll.get(j).size();
                resultAcc += ave + "\t";
            }
            resultAcc += "\n";

            resultHit += (100 - notHit1) + "\t" + (100 - notHit2) + "\t" + (100 - notHit4) + "\t" + (100 - notHit6) + "\n";

        }

        FileOutputStream outputTime = new FileOutputStream(outFileTime, true);
        outputTime.write(resultTime.getBytes());
        outputTime.flush();
        outputTime.close();

        FileOutputStream outputMem = new FileOutputStream(outFileMemory, true);
        outputMem.write(resultMemo.getBytes());
        outputMem.flush();
        outputMem.close();

        FileOutputStream outputAcc = new FileOutputStream(outFileAcc, true);
        outputAcc.write(resultAcc.getBytes());
        outputAcc.flush();
        outputAcc.close();

        FileOutputStream outputHit = new FileOutputStream(outFileHit, true);
        outputHit.write(resultHit.getBytes());
        outputHit.flush();
        outputHit.close();
    }

    public static ArrayList<ArrayList<Point>> getPointPairs(double distance) throws IOException{
        ArrayList<ArrayList<Point>> pointPairs = new ArrayList<>();
        ArrayList<String> point1s = new ArrayList<>();
        ArrayList<String> point2s = new ArrayList<>();
        Path path1 = Paths.get(System.getProperty("user.dir") + "/prepare/" + "hsm_point1s_" + distance + ".txt");
        Scanner scanner1 = new Scanner(path1);

        while (scanner1.hasNextLine()) {
            String line = scanner1.nextLine();
            point1s.add(line);

        }

        Path path2 = Paths.get(System.getProperty("user.dir") + "/prepare/" + "hsm_point2s_" + distance + ".txt");
        Scanner scanner2 = new Scanner(path2);

        while (scanner2.hasNextLine()) {
            String line = scanner2.nextLine();
            point2s.add(line);

        }

        for (int i = 0; i < point1s.size(); i++) {
            String point1Str = point1s.get(i);
            String point2Str = point2s.get(i);
            String[] point1Arr = point1Str.split(",");
            String[] point2Arr = point2Str.split(",");
            Point point1 = new Point(Double.parseDouble(point1Arr[0]), Double.parseDouble(point1Arr[1]), Integer.parseInt(point1Arr[2]));
            Point point2 = new Point(Double.parseDouble(point2Arr[0]), Double.parseDouble(point2Arr[1]), Integer.parseInt(point2Arr[2]));
            pointPairs.add(new ArrayList<>(Arrays.asList(point1, point2)));
        }
        return pointPairs;
    }

    public static void main(String arg[]) throws IOException {
        PrintOut printOut = new PrintOut();
//        DataGenConstant.init("newHsm");
        HSMDataGenRead dateGenRead = new HSMDataGenRead();
        dateGenRead.dataGen("newHsm");

        System.out.println("genTopology start");
        GenTopology genTopology = new GenTopology();
        genTopology.genTopology();

        System.out.println("simulate start");
        Simulate.simulate_read("newHsm", DataGenConstant.parInterval);
        System.out.println("read finished");

        CommonFunction.setQType();

//        int count = 0;
        for (int i = 0; i < IndoorSpace.iPartitions.size(); i++) {
            Partition parTemp = IndoorSpace.iPartitions.get(i);
            TEPM tepm = new TEPM(queryTime, DataGenConstant.endTime, parTemp);
            tepm.preCalVo();
//            double ave = parTemp.getPreVolitality().get(0);
//            double vol = parTemp.getPreVolitality().get(1);
//            if (vol < 3) {
//                count++;
//            }
//            System.out.println("parId " + i + " ave " + ave + " vol " + vol);
        }
//        System.out.println("count: " + count);


        // distance
//        String queryType = "icfpq";
        String queryType = "icaspq";
        String outFileTime = System.getProperty("user.dir") + "/result/" + "newHSM_default_time_" + queryType + ".csv";
        String outFileMemory = System.getProperty("user.dir") + "/result/" + "newHSM_default_memory_" + queryType + ".csv";
        String outFileAcc = System.getProperty("user.dir") + "/result/" + "newHSM_default_acc_" + queryType + ".csv";
        String outFileHit = System.getProperty("user.dir") + "/result/" + "newHSM_default_hit_" + queryType + ".csv";
        New_HSM_other.run(outFileTime, outFileMemory, outFileAcc, outFileHit, queryType);

        // interval

        // floor
    }

}

