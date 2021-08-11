package com.zjl.mygpuimageproject.texturerenderer

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import com.zjl.mygpuimageproject.OpenGLUtils
import com.zjl.mygpuimageproject.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Project Name: MyGpuImageProject
 * ClassName:    TexturePicRenderer
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年08月10日 16:54
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
open class TexturePicRenderer(context: Context) : BackVideoRenderer() {

    private var mCoordinate = floatArrayOf(
        -1.0f, -1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, -1.0f, 0.0f, 1.0f, 1.0f,
        -1.0f, 1.0f, 0.0f, 0.0f, 0.0f,
        1.0f, 1.0f, 0.0f, 1.0f, 0.0f
    )

    private val mVertexShader = "uniform mat4 uMVPMatrix;\n" +
            "uniform mat4 uSTMatrix;\n" +
            "attribute vec4 aPosition;\n" +
            "attribute vec4 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "  gl_Position = (uMVPMatrix * aPosition);\n" +
            "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
            "}\n";

    private val mFragmentShader =
        "#extension GL_OES_EGL_image_external : require\n"+
        "precision mediump float;\n"+
        "varying vec2 vTextureCoord;\n"+
        "uniform sampler2D sTexture;\n"+
        "void main() {\n"+
        "  vec4 c1 = texture2D(sTexture, vTextureCoord);\n"+
        "  gl_FragColor = vec4(c1.rgb, c1.a * 0.9);\n"+
        "}\n"

    private var mCoordinateData: FloatBuffer
    private var mTextureID = IntArray(2)
    private var maPositionHandler = 0
    private var maTextureHandler = 0
    private var muMVPMatrixHandler = 0
    private var muSTMatrixHandler = 0
    private var mProgram = 0
    private val mBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.secret)
    private var mMVPMatrix = FloatArray(16)
    private var mSTMatrix = FloatArray(16)

    init {
        mCoordinateData = ByteBuffer.allocateDirect(mCoordinate.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mCoordinateData.put(mCoordinate).position(0)
        Matrix.setIdentityM(mSTMatrix, 0)
        Matrix.setIdentityM(mMVPMatrix, 0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        super.onSurfaceCreated(gl, config)
        mProgram = OpenGLUtils.createProgram(mVertexShader, mFragmentShader)

        maPositionHandler = GLES20.glGetAttribLocation(mProgram, "aPosition")
        maTextureHandler = GLES20.glGetAttribLocation(mProgram, "aTextureCoord")
        muMVPMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        muSTMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uSTMatrix")


        mCoordinateData.position(0)
        GLES20.glVertexAttribPointer(maPositionHandler, 3, GLES20.GL_FLOAT, false, 20, mCoordinateData)
        GLES20.glEnableVertexAttribArray(maPositionHandler)

        mCoordinateData.position(3)
        GLES20.glVertexAttribPointer(maTextureHandler, 2, GLES20.GL_FLOAT, false, 20, mCoordinateData)
        GLES20.glEnableVertexAttribArray(maTextureHandler)


        GLES20.glGenTextures(1, mTextureID, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureID[0])
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

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        super.onDrawFrame(gl)
        GLES20.glUseProgram(mProgram)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0)
        GLES20.glUniformMatrix4fv(muMVPMatrixHandler, 1, false, mMVPMatrix, 0)
        GLES20.glUniformMatrix4fv(muSTMatrixHandler, 1, false, mSTMatrix, 0)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glFinish()
    }
}