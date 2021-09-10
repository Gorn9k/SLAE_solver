package com.company;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static JSONObject line;

    public static void main(String[] args) throws Exception {
        ServerSocket servsock = new ServerSocket(13267);
        while (true) {
            System.out.println("Waiting...");
            Socket sock = servsock.accept();
            System.out.println("Accepted connection : " + sock);
            ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
            try {
                line = new JSONObject((String) ois.readObject());
            } catch (Throwable e){
                line = new JSONObject();
                line.put("result", "Error");
                ObjectOutputStream oss = new ObjectOutputStream(sock.getOutputStream());
                oss.writeObject(line.toString());
                continue;
            }
            ObjectOutputStream oss = new ObjectOutputStream(sock.getOutputStream());
            method();
            oss.writeObject(line.toString());
            ois.close();
            oss.close();
            sock.close();
        }
    }

    private static void method() {
        try {
            double[][] A = new double[line.getInt("razmer")][line.getInt("razmer")];
            double[] B = new double[line.getInt("razmer")];
            JSONArray jsonArrayA, jsonArrayB;
            jsonArrayA = (JSONArray) line.get("masA");
            jsonArrayB = (JSONArray) line.get("masB");
            for (int i = 0; i < line.getInt("razmer"); i++) {
                for (int j = 0; j < line.getInt("razmer"); j++) {
                    A[i][j] = Double.parseDouble(jsonArrayA.getJSONArray(i).get(j).toString());
                }
                B[i] = Double.parseDouble(jsonArrayB.get(i).toString());
            }
            if (line.get("id").equals("LU метод")) {
                line.put("result", LU.lu(A, B));
            }
            if (line.get("id").equals("Метод Гаусса")) {
                line.put("result", Gauss.gaus(A, B));
            }
            if (line.get("id").equals("Метод Зейделя (метод Гаусса-Зейделя)")) {
                double[] result = Seidel.findSolution(A, B, line.getDouble("eps"));
                if (Double.isNaN(result[0])) {
                    line.put("result", "Error_NanOrInfinity");
                } else {
                    line.put("result", result);
                }
            }
            if (line.get("id").equals("comparison")) {
                comparison(A, B);
            }
        }
        catch (Throwable e) {
            line.put("result", "Error");
        }
    }

    private static void comparison(double[][] A, double[] B){
        double[][] A1 = new double[line.getInt("razmer")][line.getInt("razmer")];
        double[] B1 = new double[line.getInt("razmer")];
        JSONArray jsonArrayA1, jsonArrayB1;
        jsonArrayA1 = (JSONArray) line.get("masA");
        jsonArrayB1 = (JSONArray) line.get("masB");
        for (int i = 0; i < line.getInt("razmer"); i++) {
            for (int j = 0; j < line.getInt("razmer"); j++) {
                A1[i][j] = Double.parseDouble(jsonArrayA1.getJSONArray(i).get(j).toString());
            }
            B1[i] = Double.parseDouble(jsonArrayB1.get(i).toString());
        }
        MethodSpeed methodSpeedLU = new MethodSpeed(A, B);
        MethodSpeed methodSpeedGauss = new MethodSpeed(A1, B1);
        MethodSpeed methodSpeedSeidel = new MethodSpeed(A, B);
        double[] time = new double[3];
        time[0] = methodSpeedLU.solveSpeedLU();
        double epsLU = methodSpeedLU.solveDisperancy();
        time[1] = methodSpeedGauss.solveSpeedGauss();
        methodSpeedGauss.A = A;
        methodSpeedGauss.b = B;
        double epsG = methodSpeedGauss.solveDisperancy();
        double epsSeidel = Math.abs(epsLU + epsG) / 2;
        if(epsSeidel == 0) epsSeidel = Double.MIN_VALUE;
        time[2] = methodSpeedSeidel.solveSpeedSeidel(epsSeidel);
        if (Double.isNaN(methodSpeedSeidel.result[0])) {
            line.put("time_result", "Error_NanOrInfinity");
        } else {
            line.put("time_result", time);
        }
    }
}