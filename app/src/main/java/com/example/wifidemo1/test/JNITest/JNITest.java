package com.example.wifidemo1.test.JNITest;

/**
 * @author: fuhejian
 * @date: 2023/3/28
 */
public class JNITest {

    public JNITest(){
        System.loadLibrary("histogram-lib");
        long a = test();
        System.out.println(a);
    }

    native long test();

    public long t(){
        return 11;
    }

}
