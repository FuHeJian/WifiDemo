package com.example.wifidemo1.customview;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author: fuhejian
 * @date: 2023/3/29
 */
public class MyGlSurfaceView2 extends GLSurfaceView {
    public MyGlSurfaceView2(Context context) {
        this(context,null);
    }

    public MyGlSurfaceView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        setRenderer(new SimpleRenderer());
    }

    private static class SimpleRenderer implements Renderer {
        private float[] vertexPoints = new float[]{
                0.0f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };
        private FloatBuffer vertexBuffer;
        /**
         * 顶点着色器
         */
        private String vertextShader =
                "#version 300 es\n" +
                        "layout (location = 0) in vec4 vPosition;\n" +
                        "void main() {\n" +
                        "     gl_Position  = vPosition;\n" +
                        "     \n" +
                        "}";

        /**
         * 片段着色器
         */
        private String fragmentShader =
                "#version 300 es\n" +
                        "precision mediump float;\n" +
                        "out vec4 fragColor;\n" +
                        "void main() {\n" +
                        "     fragColor = vec4(1.0,1.0,1.0,1.0);\n" +
                        "}";


        public SimpleRenderer() {
            //分配内存空间,每个浮点型占4字节空间
            vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            //传入指定的坐标数据
            vertexBuffer.put(vertexPoints);
            vertexBuffer.position(0);
        }


        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            GLES30.glClearColor(0f, 0f, 0f, 0f);
            final int vertexShaderId = compileShader(GLES30.GL_VERTEX_SHADER, vertextShader);
            final int fragmentShaderId = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShader);
            GLES30.glUseProgram(linkProgram(vertexShaderId, fragmentShaderId));
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int width, int height) {
            GLES30.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
            //准备坐标数据
            GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, vertexBuffer);
            //启用顶点的句柄
            GLES30.glEnableVertexAttribArray(0);
            //绘制三个点
//            GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, 0, 3);

            //绘制直线
//        GLES30.glDrawArrays(GLES30.GL_LINE_STRIP, 0, 2);
//        GLES30.glLineWidth(10);

            //绘制三角形
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);
            //禁止顶点数组的句柄
            GLES30.glDisableVertexAttribArray(0);
        }


        /**
         * 编译
         */
        private static int compileShader(int type, String shaderCode) {
            //创建一个着色器
            final int shaderId = GLES30.glCreateShader(type);
            if (shaderId != 0) {
                //加载到着色器
                GLES30.glShaderSource(shaderId, shaderCode);
                //编译着色器
                GLES30.glCompileShader(shaderId);
                //检测状态
                final int[] compileStatus = new int[1];
                GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
                if (compileStatus[0] == 0) {
                    String logInfo = GLES30.glGetShaderInfoLog(shaderId);
                    System.err.println(logInfo);
                    //创建失败
                    GLES30.glDeleteShader(shaderId);
                    return 0;
                }
                return shaderId;
            } else {
                //创建失败
                return 0;
            }
        }

        /**
         * 链接
         */
        public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
            final int programId = GLES30.glCreateProgram();
            if (programId != 0) {
                //将顶点着色器加入到程序
                GLES30.glAttachShader(programId, vertexShaderId);
                //将片元着色器加入到程序中
                GLES30.glAttachShader(programId, fragmentShaderId);
                //链接着色器程序
                GLES30.glLinkProgram(programId);
                final int[] linkStatus = new int[1];
                GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0);
                if (linkStatus[0] == 0) {
                    String logInfo = GLES30.glGetProgramInfoLog(programId);
                    System.err.println(logInfo);
                    GLES30.glDeleteProgram(programId);
                    return 0;
                }
                return programId;
            } else {
                //创建失败
                return 0;
            }
        }

    }
}
