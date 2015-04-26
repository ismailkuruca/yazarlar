package com.tangobyte.yazarlar.utils;

public class ambitifatih {
    static int val = 0;
    
    public static void main(String[] args) {
        int[][] arr = new int[5][5];
        
        int size = 5;
        int a = 0, b = 0, i = 0, j = 0, x = 1;
        int tot = 0;
        for(i = 0; ; i += x) {
            if(tot == 24) break;
            for(j = 0; ; j += x) {
                if(j == size) {
                    a = i;
                    i = j;
                    b = j;
                    j = a;
                    break;
                }
                arr[i][j] = val++;
                System.err.print(i + " " + j + " " + val + "\n");
               
            }
            if(i == 0) {
                a = i;
                i = j;
                b = j;
                j = a;
                x *= -1;
            }
        }
        
        
        printArray(arr);
    }
    
    static void printArray(int[][] arr) {
        for(int a[] : arr) {
            for(int i : a) {
                System.out.print(i + "\t");
            }
            System.out.println();
        }
        
    }

}
