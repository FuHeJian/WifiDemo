package com.example.wifidemo1.activity.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * com.example.wifidemo1.activity.impl
 */
public class test {

    static {
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                    }
                }
        );
        thread.start();
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        executorService.submit(new Runnable() {
                @Override
                public void run() {

                }
        });
        System.out.println();
    }

}
