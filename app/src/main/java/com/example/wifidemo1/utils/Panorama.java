package com.example.wifidemo1.utils;

public class Panorama {

    static {
        System.loadLibrary("panorama");
    }

    public static native long initfromyaml(String prefix,String warp,int thread_num,long available_memory);
    public static native int pushimg(long handle,int thread_num);
    public static native int blend(long handle,String ouput,boolean trim);

    /**
     *初始化全景合成对象，成功返回对象引用指针 失败返回-1.如果失败不能调用后续步骤
     * @param img_num 图片张数
     * @param merge 合成类型1=水平 2=九宫格
     * @param st_level 合成等级0=只提取特征 1=计算相机矩阵 2=执行所有运算并合成全景图片
     * @param warp_type 拼接类型 plane=平面 cylindrical=柱形 spherical=球形
     * @param output 合成图片输出路径
     * @return
     */
    public static native long initpanorama(int img_num,int merge,int st_level,String warp_type,String output);

    /**
     *
     * @param handle stitching obj handle
     * @param img_file image path
     * @param index start from 0
     * @return 0=success other failed
     */
    public static native int addimg(long handle,String img_file,int index);

    /**
     *
     * @param handle stitching obj handle
     * @return 0=success other failed
     */
    public static native int stitching(long handle);



    public static native long initfromyamlHDR(String prefix, int thread_num);

    public static native int blendHDR(long handle);

    public static native long initfromyamlFocus(String prefix, int num_threads, int ek, long available_memory);

    public static native int blendFocus(long handle);

    public static native long initStarStack(String output);

    public static native int addStarStack(long handle, String addImage, boolean isEnd);

}
