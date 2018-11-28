package com.logger;

import java.nio.file.Paths;

/**
 * Created by Zynoz on 13.07.2017.
 */
public class Main {

    private static LogLib logLib = new LogLib(Paths.get("E:\\BlackSun\\Logger\\src\\com\\logger"), "test.txt", AppendType.CONSOLE, "dd.MM HH:mm:ss.SSS");

    public static void main(String[] args) {
        test();
    }

    private static void test() {
        System.out.println("start");
        double time = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            try {
                logLib.e("test");
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("End: " + (System.currentTimeMillis() - time));
    }
}