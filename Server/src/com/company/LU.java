package com.company;

public class LU {
    public static double[] lu(double[][] A, double[] b) {
        double sum = 0;
        double[][] l = new double[A.length][A.length];
        double[][] u = new double[A.length][A.length];
        double[] x = new double[A.length];
        double[] y = new double[A.length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A.length; j++) {
                l[i][j] = 0;
                u[i][j] = 0;
            }
            x[i] = 0;
            y[i] = 0;
            u[i][i] = 1;
        }
        for (int i = 0; i < A.length; i++)
            for (int j = 0; j < A.length; j++) {
                sum = 0;
                if (i >= j) {
                    for (int s = 0; s <= j - 1; s++)
                        sum += (l[i][s]) * (u[s][j]);
                    l[i][j] = A[i][j] - sum;
                } else {
                    for (int s = 0; s <= i - 1; s++)
                        sum += l[i][s] * u[s][j];
                    u[i][j] = (A[i][j] - sum) / l[i][i];
                }
            }
        y[0] = b[0] / l[0][0];
        for (int i = 1; i < A.length; i++){
            sum = 0;
            for (int j = 0; j < i; j++)
                sum += l[i][j] * y[j];
            y[i] = (b[i] - sum) / l[i][i];
        }
        x[A.length-1] = y[A.length-1];
        for (int i = A.length - 2; i >= 0; i--){
            sum = 0;
            for (int j = A.length - 1; j >= i + 1; j--)
                sum += x[j] * u[i][j];
            x[i] = y[i] - sum;
        }
        return x;
    }
}
