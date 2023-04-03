package com.example.wifidemo1.Executors;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author: fuhejian
 * @date: 2023/3/30
 */
public class ExecutorsUtil {

    public static Executor IO = Executors.newFixedThreadPool(2);

}
