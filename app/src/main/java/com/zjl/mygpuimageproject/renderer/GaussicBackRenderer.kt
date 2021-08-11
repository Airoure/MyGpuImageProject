package com.zjl.mygpuimageproject.renderer

import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.Surface
import com.zjl.mygpuimageproject.OpenGLUtils
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Project Name: MyGpuImageProject
 * ClassName:    GaussicBackRenderer
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年08月10日 15:05
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
open class GaussicBackRenderer : GLSurfaceView.Renderer{

    private var mCoordinate = floatArrayOf(
        -1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, -1.0f, 0.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f, 1.0f, 0.0f
    )

    private var mCoordinateData: FloatBuffer
    private var maPositionHandler = 0
    private var maTextureHandler = 0
    private var mProgram = 0

    private var mSurfaceTexture: SurfaceTexture? = null

    private var mTextureId = IntArray(2)

    init {
        mCoordinateData = ByteBuffer.allocateDirect(mCoordinate.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mCoordinateData.put(mCoordinate).position(0)

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mProgram = OpenGLUtils.createProgram(mVertexShader, mFragmentShader)
        maPositionHandler = GLES20.glGetAttribLocation(mProgram, "aPosition")
        maTextureHandler = GLES20.glGetAttribLocation(mProgram, "aTextureCoord")

        GLES20.glGenTextures(1, mTextureId, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureId[0])
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE
        )

        mCoordinateData.position(0)
        GLES20.glVertexAttribPointer(
            maPositionHandler,
            3,
            GLES20.GL_FLOAT,
            false,
            20,
            mCoordinateData
        )
        GLES20.glEnableVertexAttribArray(maPositionHandler)

        mCoordinateData.position(3)
        GLES20.glVertexAttribPointer(
            maTextureHandler,
            2,
            GLES20.GL_FLOAT,
            false,
            20,
            mCoordinateData
        )
        GLES20.glEnableVertexAttribArray(maTextureHandler)


        mSurfaceTexture = SurfaceTexture(mTextureId[0])
        val videoSurface = Surface(mSurfaceTexture)

        IjkMediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setSurface(videoSurface)
            dataSource = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"
            prepareAsync()
            setOnPreparedListener {
                start()
            }
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glUseProgram(mProgram)

        mSurfaceTexture?.updateTexImage()


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    private var mVertexShader = "attribute vec4 aPosition;\n" +
            "attribute vec4 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "  gl_Position = aPosition;\n" +
            "  vTextureCoord = aTextureCoord.xy;\n" +
            "}\n"

    private val radius = 5.0f

    private var blurTypeString = "vec2(1.0,0.0)"

    private var mFragmentShader = "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform sampler2D sTexture;\n" + //samplerExternalOES 和 sampler2D有什么区别 ---需要二次渲染的要用samplerExternalOES
            "const float resolution=1024.0;\n" +
            "const float radius = " + radius + ";\n" +
            "vec2 dir =" + blurTypeString + "; //若为x模糊，可传入（1.0,0.0）  y模糊  （0.0,1.0）\n" +
            "\n" +
            "void main() {\n" +
            "    //this will be our RGBA sum\n" +
            "    vec4 sum = vec4(0.0);\n" +
            "    \n" +
            "    //our original texcoord for this fragment\n" +
            "    vec2 tc = vTextureCoord;\n" +
            "    \n" +
            "    //the amount to blur, i.e. how far off center to sample from \n" +
            "    //1.0 -> blur by one pixel\n" +
            "    //2.0 -> blur by two pixels, etc.\n" +
            "    float blur = radius/resolution; \n" +
            "    \n" +
            "    //the direction of our blur\n" +
            "    //(1.0, 0.0) -> x-axis blur\n" +
            "    //(0.0, 1.0) -> y-axis blur\n" +
            "    float hstep = dir.x;\n" +
            "    float vstep = dir.y;\n" +
            "    \n" +
            "    \n" +
            "    //apply blurring, using a 9-tap filter with predefined gaussian weights\n" +
            "    \n" +
            "    sum += texture2D(sTexture, vec2(tc.x - 4.0*blur*hstep, tc.y - 4.0*blur*vstep)) * 0.0162162162;\n" +
            "    sum += texture2D(sTexture, vec2(tc.x - 3.0*blur*hstep, tc.y - 3.0*blur*vstep)) * 0.0540540541;\n" +
            "    sum += texture2D(sTexture, vec2(tc.x - 2.0*blur*hstep, tc.y - 2.0*blur*vstep)) * 0.1216216216;\n" +
            "    sum += texture2D(sTexture, vec2(tc.x - 1.0*blur*hstep, tc.y - 1.0*blur*vstep)) * 0.1945945946;\n" +
            "    \n" +
            "    sum += texture2D(sTexture, vec2(tc.x, tc.y)) * 0.2270270270;\n" +
            "    \n" +
            "    sum += texture2D(sTexture, vec2(tc.x + 1.0*blur*hstep, tc.y + 1.0*blur*vstep)) * 0.1945945946;\n" +
            "    sum += texture2D(sTexture, vec2(tc.x + 2.0*blur*hstep, tc.y + 2.0*blur*vstep)) * 0.1216216216;\n" +
            "    sum += texture2D(sTexture, vec2(tc.x + 3.0*blur*hstep, tc.y + 3.0*blur*vstep)) * 0.0540540541;\n" +
            "    sum += texture2D(sTexture, vec2(tc.x + 4.0*blur*hstep, tc.y + 4.0*blur*vstep)) * 0.0162162162;\n" +
            "\n" +
            "    vec4 cc= texture2D(sTexture,vTextureCoord );\n" +
            "\n" +
            "    //discard alpha for our simple demo, multiply by vertex color and return\n" +
            "    gl_FragColor =vec4(sum.rgb, cc.a);\n" +
            "}"


}