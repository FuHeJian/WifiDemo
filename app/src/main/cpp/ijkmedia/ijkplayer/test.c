//
// Created by fhj on 2023/4/14.
//

#include <jni.h>

JNIEXPORT jobject JNICALL
Java_com_example_wifidemo1_JNITest_JNITest_test(JNIEnv *env, jobject thiz) {
    return (*env)->NewGlobalRef(env,thiz);
}
