package com.geekbrains.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyArrays {

    public static void main(String[] args) {
        MyArrays myArrays = new MyArrays();
        Integer[] myInt;
        try {
            myInt = myArrays.GetAllAfterLast4(new Integer[]{1, 4, 3, 5, 4, 7, 2});
            System.out.println(Arrays.toString(myInt));
        } catch (RuntimeException e) {
            System.out.println(e);
        }
        Boolean checkResult = myArrays.CheckArray1And4(new Integer[] {2,3,5,4,3,2});
        System.out.println(checkResult);
    }

    public Integer[] GetAllAfterLast4(Integer[] array)
    throws RuntimeException {
        List<Integer> myArray = Arrays.asList(array);
        int last4 = myArray.lastIndexOf(4);
        if (last4 == -1) {
            throw new RuntimeException("В массиве нет ни одной 4-ки!");
        }
        List<Integer> newArrayList = myArray.subList(last4+1, myArray.size());
        return newArrayList.toArray(new Integer[0]);
    }

    public Boolean CheckArray1And4(Integer[] array) {
        List<Integer> myArray = Arrays.asList(array);
        ArrayList<Integer> valuesToCheck = new ArrayList<>(2);
        valuesToCheck.add(1);
        valuesToCheck.add(4);
        return myArray.containsAll(valuesToCheck);
    }

}
