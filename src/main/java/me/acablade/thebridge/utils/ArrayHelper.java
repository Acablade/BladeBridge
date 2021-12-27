package me.acablade.thebridge.utils;

import java.util.UUID;

public class ArrayHelper {


    public static <T> void add(T[] arr, T item) {
        int i = 0;
        while (arr[i]!=null){
            i++;
        }
        arr[i]=item;
    }

    public static <T> boolean contains(T[] arr, T item){
        for (int i = 0; i < arr.length; i++) {
            if(arr[i]==item) return true;
        }
        return false;
    }

    public static <T> int indexOf(T[] arr, T item){
        int i = 0;
        while (arr[i]!=item&&i<arr.length){
            i++;
        }
        if(i==arr.length){
            return -1;
        }
        return i;
    }

}