package com.company;

public class MethodSpeed {
    private static final int iterations = 100000;
    public double[] result;
    public int iter;
    public double[][] A;
    public double[] b;

    MethodSpeed(double[][] A, double[] b){
        this.A = A;
        this.b = b;
    }

    public double solveSpeedLU(){
        iter = 0;
        double start = System.currentTimeMillis();
        while(iter < iterations){
            result = LU.lu(A, b);
            iter++;
        }
        return (System.currentTimeMillis() - start)/iterations;
    }

    public double solveSpeedGauss(){
        iter = 0;
        double start = System.currentTimeMillis();
        while(iter < iterations){
            result = Gauss.gaus(A, b);
            iter++;
        }
        return (System.currentTimeMillis() - start)/iterations;
    }

    public double solveSpeedSeidel(double eps){
        iter = 0;
        double result_time = 0;
        double start = System.currentTimeMillis();
        while(iter < iterations){
            result = Seidel.findSolution(A, b, eps);
            iter++;
        }
        result_time = (System.currentTimeMillis() - start)/iterations;
        if(Double.isNaN(result[0]) || Double.isInfinite(result[0]))
            result[0] = Double.NaN;
        return result_time;
    }

    public double solveDisperancy(){
        double[] u_vector = new double[A.length];
        for (int i = 0; i < A.length; ++i) {
            double actual_b_i = 0.0;   // результат перемножения i-строки
            // исходной матрицы на вектор x
            for (int j = 0; j < A.length; ++j)
                actual_b_i += A[i][j] * result[j];
            // i-й элемент вектора невязки
            u_vector[i] = b[i] - actual_b_i;
        }
        double epsilon = 0;
        for (double eps: u_vector){
        epsilon += eps;
        }
        return epsilon/u_vector.length;
    }
}
