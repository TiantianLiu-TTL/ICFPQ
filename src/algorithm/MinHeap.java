package algorithm;

import java.util.ArrayList;

/**
 * @author Tiantian Liu
 *
 */
public class MinHeap<T> {

    int size;
    ArrayList<Stamp> heapArray = new ArrayList<Stamp>();
    int heapSize;

    String piority = "pop"; 		// the piority is the population

    MinHeap (String piority) {
        this.piority = piority;
        heapSize = 0;
    }

    private double select1(Stamp s) {
        if (piority.equals("pop")) {
            return s.pop;
        } else if (piority.equals("distance")) {
            return s.D;
        } else {
            System.out.println("something wrong_Heap_select");
            return -1;
        }

    }
    private double select2(Stamp s) {
        if (piority.equals("pop")) {
            return s.D;
        } else if (piority.equals("distance")) {
            return s.pop;
        } else {
            System.out.println("something wrong_Heap_select");
            return -1;
        }

    }



    public Stamp getParent(int index) {
        return heapArray.get((index - 1)/2);
    }

    public void insert(Stamp element) {

        if (heapSize == heapArray.size()) {
            heapArray.add(heapSize, element);
            //heapArray[heapSize] = element;
            heapifyUp(heapSize);
            heapSize++;
        }
        else if (heapSize == heapArray.size() - 1) {
            heapArray.remove(heapArray.size() - 1);
            heapArray.add(heapSize, element);
            //heapArray[heapSize] = element;
            heapifyUp(heapSize);
            heapSize++;
        }
        else {
            System.out.println("something wrong_Heap_insert: " + heapSize + ", " + heapArray.size());
        }

    }

    public String extract_min() {
        return heapArray.get(0).toString();
    }

    public Stamp delete_min() {

        if (heapArray.size() <= 0) System.out.println("something wrong_Heap_delete_min");
        Stamp deleteElement = heapArray.get(0);
        //T deleteElement = heapArray[0];
        int newHeapSize = --heapSize;

        if (newHeapSize < 0) {
            heapArray.remove(0);
            return deleteElement;
        }
        //int newHeapSize = --heapSize;
        Stamp temp = heapArray.get(newHeapSize);
        heapArray.remove(0);
        heapArray.add(0, temp);
//        System.out.println("0 " + heapArray.get(0).doorId + " " + heapArray.get(0).pop + " " + heapArray.get(0).D);
//        System.out.println("1 " + heapArray.get(1).doorId + " " + heapArray.get(1).pop + " " + heapArray.get(1).D);
        heapifyDown(0);
        heapArray.remove(heapSize);
        return deleteElement;
    }

    public Stamp getRightChild(int index) {
        if (heapSize >= ((2 * index) + 1)) {
            return heapArray.get(((2 * index)+ 1));
        }
        return null;
    }

    public Stamp getLeftChild(int index) {
        if (heapSize >= ((2 * index)+ 2)) {
            return heapArray.get(((2 * index) + 2));
        }
        return null;
    }

    public void heapifyUp(int index) {
        Stamp element = heapArray.get(index);
        Stamp temp = null;
        int ind = index;
        while (ind > 0) {
            if (((Comparable) select1(heapArray.get(ind))).compareTo(select1(getParent(ind))) < 0) {
                temp = heapArray.get((ind-1)/2);
                //temp = heapArray[(ind-1)/2];
                Stamp temp1 = heapArray.get(ind);
                heapArray.remove((ind-1)/2);
                heapArray.add((ind-1)/2, temp1);
                //heapArray[(ind-1)/2] = heapArray[ind];
                heapArray.remove(ind);
                heapArray.add(ind, temp);
                //heapArray[ind] = temp;
            } else if (((Comparable) select1(heapArray.get(ind))).compareTo(select1(getParent(ind))) == 0) {
                if (((Comparable) select2(heapArray.get(ind))).compareTo(select2(getParent(ind))) < 0) {
                    temp = heapArray.get((ind-1)/2);
                    //temp = heapArray[(ind-1)/2];
                    Stamp temp1 = heapArray.get(ind);
                    heapArray.remove((ind-1)/2);
                    heapArray.add((ind-1)/2, temp1);
                    //heapArray[(ind-1)/2] = heapArray[ind];
                    heapArray.remove(ind);
                    heapArray.add(ind, temp);
                    //heapArray[ind] = temp;
                }
            }
            ind--;
        }
    }

    public void heapifyDown(int i) {
        int ind = i;
        if (ind >= 0 && getLeftChild(ind) != null && getRightChild(ind)!= null) {
            if (((Comparable) select1(getRightChild(ind))).compareTo(select1(getLeftChild(ind))) > 0) {
                while (ind < heapSize) {
                    if (getLeftChild(ind) != null &&
                            ((Comparable) select1(heapArray.get(ind))).compareTo(select1(getLeftChild(ind))) > 0) {
                        swapChildParent(((2 * ind)+ 2), ind);
                    } else if (getLeftChild(ind) != null &&
                            ((Comparable) select2(heapArray.get(ind))).compareTo(select2(getLeftChild(ind))) > 0 &&
                            ((Comparable) select1(heapArray.get(ind))).compareTo(select1(getLeftChild(ind))) == 0) {
                        swapChildParent(((2 * ind)+ 2), ind);
                    }

                    if (getRightChild(ind) != null &&
                            ((Comparable) select1(heapArray.get(ind))).compareTo(select1(getRightChild(ind))) > 0) {
                        swapChildParent(((2 * ind)+ 1), ind);
                    } else if (getRightChild(ind) != null &&
                            ((Comparable) select2(heapArray.get(ind))).compareTo(select2(getRightChild(ind))) > 0 &&
                            ((Comparable) select1(heapArray.get(ind))).compareTo(select1(getRightChild(ind))) == 0) {
                        swapChildParent(((2 * ind)+ 1), ind);
                    }
                    ind++;
                }
            } else {
                while (ind < heapSize) {
                    if (getRightChild(ind) != null &&
                            ((Comparable) select1(heapArray.get(ind))).compareTo(select1(getRightChild(ind))) > 0) {
                        swapChildParent(((2 * ind)+ 1), ind);
//                        System.out.println("swap0 " + heapArray.get((2 * ind)+ 1).doorId + " " + heapArray.get(ind).doorId);
                    } else if (getRightChild(ind) != null &&
                            ((Comparable) select2(heapArray.get(ind))).compareTo(select2(getRightChild(ind))) > 0
                            && ((Comparable) select1(heapArray.get(ind))).compareTo(select1(getRightChild(ind))) == 0) {
                        swapChildParent(((2 * ind)+ 1), ind);
//                        System.out.println("swap1 " + heapArray.get((2 * ind)+ 1).doorId + " " + heapArray.get(ind).doorId);
                    }

                    if (getLeftChild(ind) != null &&
                            ((Comparable) select1(heapArray.get(ind))).compareTo(select1(getLeftChild(ind))) > 0) {
                        swapChildParent(((2 * ind)+ 2), ind);
//                        System.out.println("swap2 " + heapArray.get((2 * ind)+ 2).doorId + " " + heapArray.get(ind).doorId);
                    } else if (getLeftChild(ind) != null &&
                            ((Comparable) select2(heapArray.get(ind))).compareTo(select2(getLeftChild(ind))) > 0 &&
                            ((Comparable) select1(heapArray.get(ind))).compareTo(select1(getLeftChild(ind))) == 0) {
                        swapChildParent(((2 * ind)+ 2), ind);
//                        System.out.println("swap3 " + heapArray.get((2 * ind)+ 2).doorId + " " + heapArray.get(ind).doorId);
                    }
                    ind++;
                }
            }
        }
    }

    public void swapChildParent(int index1, int index2) {
        Stamp temp = heapArray.get(index1);
        //T temp = heapArray[index1];
        Stamp temp1 = heapArray.get(index2);
        heapArray.remove(index1);
        heapArray.add(index1, temp1);
        //heapArray[index1] = heapArray[index2];
        heapArray.remove(index2);
        heapArray.add(index2, temp);
        //heapArray[index2] = temp;
    }


    public void print() {
        if (heapSize == 0) {
            System.out.println("Heap is empty");
        } else {
            for (int i = 0; i < heapSize; i++) {
                System.out.println(heapArray.get(i).toString());
            }
        }
    }

    public ArrayList<Stamp> getAll() {
        return  heapArray;
    }

    public int getHeapSize() {
        return heapSize;
    }

    public void updateNode(Stamp element, Stamp elementNew) {
//        System.out.println("from " + element.toString() +  " to " + elementNew.toString());

        for (int i = 0; i < heapSize; i ++) {
            if (heapArray.get(i).equals(element)) {
                Stamp deleteElement = heapArray.get(i);
                //T deleteElement = heapArray[i];
                int newHeapSize = --heapSize;
                Stamp temp = heapArray.get(newHeapSize);
                heapArray.remove(i);
                heapArray.add(i, temp);
                //heapArray[i] = heapArray[newHeapSize];
                heapifyDown(i);
                break;
            }
        }

        insert(elementNew);
    }

    public boolean exists(Stamp s) {
        boolean result = false;

        for (int i = 0; i < heapSize; i ++) {
            if (heapArray.get(i).equals(s)) {
                result = true;
                break;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        Stamp s1 = new Stamp(0, 12, 32);
        Stamp s2 = new Stamp(1, 14, 22);
        Stamp s3 = new Stamp(2, 10, 22);
        Stamp s4 = new Stamp(3, 18, 13);
        Stamp s5 = new Stamp(4, 11, 22);

        MinHeap<Stamp> H = new MinHeap<>("pop");


        H.insert(s3);
        H.insert(s4);
        H.insert(s1);
        H.insert(s2);
        H.insert(s5);

        ArrayList<Stamp> arr = H.heapArray;
        for (int i = 0; i < arr.size(); i++) {
            Stamp ss = arr.get(i);
            System.out.println(ss.doorId + " " + ss.pop + " " + ss.D);
        }

        H.updateNode(s5, new Stamp(6, 10, 10));

        System.out.println();
        arr = H.heapArray;
        for (int i = 0; i < arr.size(); i++) {
            Stamp ss = arr.get(i);
            System.out.println(ss.doorId + " " + ss.pop + " " + ss.D);
        }

        System.out.println();
        Stamp out1 = H.delete_min();
        System.out.println(out1.doorId + " " + out1.pop + " " + out1.D);

        System.out.println();
        arr = H.heapArray;
        for (int i = 0; i < arr.size(); i++) {
            Stamp ss = arr.get(i);
            System.out.println(ss.doorId + " " + ss.pop + " " + ss.D);
        }
        System.out.println();

        Stamp out2 = H.delete_min();
        System.out.println(out2.doorId + " " + out2.pop + " " + out2.D);

        System.out.println();
        arr = H.heapArray;
        for (int i = 0; i < arr.size(); i++) {
            Stamp ss = arr.get(i);
            System.out.println(ss.doorId + " " + ss.pop + " " + ss.D);
        }
        System.out.println();

        Stamp out3 = H.delete_min();
        System.out.println(out3.doorId + " " + out3.pop + " " + out3.D);

        System.out.println();
        arr = H.heapArray;
        for (int i = 0; i < arr.size(); i++) {
            Stamp ss = arr.get(i);
            System.out.println(ss.doorId + " " + ss.pop + " " + ss.D);
        }
        System.out.println();

        Stamp out4 = H.delete_min();
        System.out.println(out4.doorId + " " + out4.pop + " " + out4.D);

        System.out.println();
        arr = H.heapArray;
        for (int i = 0; i < arr.size(); i++) {
            Stamp ss = arr.get(i);
            System.out.println(ss.doorId + " " + ss.pop + " " + ss.D);
        }
        System.out.println();

        Stamp out5 = H.delete_min();
        System.out.println(out5.doorId + " " + out5.pop + " " + out5.D);

        System.out.println();
        arr = H.heapArray;
        for (int i = 0; i < arr.size(); i++) {
            Stamp ss = arr.get(i);
            System.out.println(ss.doorId + " " + ss.pop + " " + ss.D);
        }
    }

}




