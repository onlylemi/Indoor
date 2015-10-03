package com.onlylemi.cameraandroute;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by only乐秘 on 2015-09-27.
 */
public class RouteGLSurface extends GLSurfaceView implements GLSurfaceView.Renderer {


    public RouteGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);


    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glDisable(GL10.GL_DITHER); //关闭抗抖动
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); //设置系统对透视进行修正
        gl.glClearColor(0, 0, 0, 1); //设置清屏所用的颜色
        gl.glShadeModel(GL10.GL_SMOOTH); //设置阴影平滑模式
        gl.glEnable(GL10.GL_DEPTH_TEST); //启动深度测试
        gl.glDepthFunc(GL10.GL_LEQUAL); //设置深度测试的类型
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height); //设置3d视窗的大小及位置
        gl.glMatrixMode(GL10.GL_PROJECTION); //将当前矩阵模式设置为投影模式
        gl.glLoadIdentity(); //初始话单位矩阵
        float ratio = width / height; //计算透视视窗的宽度、高度比
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10); //设置透视视窗的空间大小
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); //清除屏幕缓存和深度缓存
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY); //弃用顶点坐标数据
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY); //弃用顶点颜色数据
        gl.glMatrixMode(GL10.GL_MODELVIEW); //设置当前矩阵模式为模型视图



        gl.glLoadIdentity(); //重置当前的模型视图矩阵
    }
}
