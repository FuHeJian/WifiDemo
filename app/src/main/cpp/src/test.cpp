#include <jni.h>


extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_wifidemo1_JNITest_JNITest_test(JNIEnv *env, jobject thiz) {
    //sig方法签名：没有参数也要写'()',long等基本类型末尾不用写';',引用类型签名前缀加'L',末尾加';'
    //用V表示void型
    //https://juejin.cn/post/6844903881520971790#heading-8
    jmethodID method = env->GetMethodID(env->GetObjectClass(thiz),"t","()J");
    return env->CallLongMethod(thiz,method);
}