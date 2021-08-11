package com.zjl.mygpuimageproject.texturerenderer

import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.Matrix
import android.view.Surface
import com.zjl.mygpuimageproject.GLTextureView
import com.zjl.mygpuimageproject.OpenGLUtils
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Project Name: MyGpuImageProject
 * ClassName:    BackVideoRenderer
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年08月10日 16:50
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
open class BackVideoRenderer : GLTextureView.Renderer {

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

    private var mMVPMatrix = FloatArray(16)
    private var mSTMatrix = FloatArray(16)

    private var muMVPMatrixHandler = 0
    private var muSTMatrixHandler = 0

    private var mTextureId = IntArray(2)

    private var mVertexShader = "uniform mat4 uMVPMatrix;\n" +
            "uniform mat4 uSTMatrix;\n" +
            "attribute vec4 aPosition;\n" +
            "attribute vec4 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "  gl_Position = (uMVPMatrix * aPosition);\n" +
            "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
            "}\n"

    private var mFragmentShader = "#extension GL_OES_EGL_image_external : require\n"+
        "precision mediump float;\n"+
        "varying vec2 vTextureCoord;\n"+
        "uniform samplerExternalOES sTexture;\n" +
        "void main() {\n"+
        "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n"+
        "}\n";


    init {
        mCoordinateData = ByteBuffer.allocateDirect(mCoordinate.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mCoordinateData.put(mCoordinate).position(0)
        Matrix.setIdentityM(mSTMatrix, 0)
        Matrix.setIdentityM(mMVPMatrix, 0)

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        mProgram = OpenGLUtils.createProgram(mVertexShader, mFragmentShader)
        maPositionHandler = GLES20.glGetAttribLocation(mProgram, "aPosition")
        maTextureHandler = GLES20.glGetAttribLocation(mProgram, "aTextureCoord")
        muMVPMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        muSTMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uSTMatrix")

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

        GLES20.glGenTextures(2, mTextureId, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,mTextureId[0])
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE
        )


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
        mSurfaceTexture?.updateTexImage()
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(mProgram)

        GLES20.glUniformMatrix4fv(muSTMatrixHandler, 1, false, mSTMatrix, 0)
        GLES20.glUniformMatrix4fv(muMVPMatrixHandler, 1, false, mMVPMatrix, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glFinish()

    }
}