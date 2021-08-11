package com.zjl.mygpuimageproject.renderer

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import com.zjl.mygpuimageproject.OpenGLUtils
import com.zjl.mygpuimageproject.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Project Name: MyGpuImageProject
 * ClassName:    PicRenderer
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年08月09日 18:28
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
open class PicRenderer(context: Context) : GLSurfaceView.Renderer {

    private var mCoordinate = floatArrayOf(
        -1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, -1.0f, 0.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f, 1.0f, 0.0f
    )

    private val mVertexShader = "attribute vec4 aPosition;\n" +
            "attribute vec4 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "  gl_Position = aPosition;\n" +
            "  vTextureCoord = aTextureCoord.xy;\n" +
            "}\n"

    private val mFragmentShader =
        "precision mediump float;\n" +
                "varying vec2 vTextureCoord;\n" +
                "uniform sampler2D sTexture;\n" +
                "void main() {\n" +
                "  vec4 c1 = texture2D(sTexture, vTextureCoord);\n" +
                "  gl_FragColor = vec4(c1.rgb, c1.a);\n" +
                "}\n";

    private var mCoordinateData: FloatBuffer
    private var mTextureID = IntArray(2)
    private var maPositionHandler = 0
    private var maTextureHandler = 0
    private var mProgram = 0
    private val mBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.secret)

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

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glGenTextures(1, mTextureID, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID[0])
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT
        )


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


    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(mProgram)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }
}