package com.zjl.mygpuimageproject

import android.content.res.Resources
import android.opengl.GLES20
import android.util.TypedValue

/**
 * Project Name: MyGpuImageProject
 * ClassName:    OpenGLUtils
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年08月10日 9:34
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
object OpenGLUtils {
    fun loadShader(type: Int, source: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader,source)
        GLES20.glCompileShader(shader)
        return shader
    }

    fun createProgram(vertexSource: String, fragmentSource: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexSource)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentSource)
        val program = GLES20.glCreateProgram()
        if (program != 0) {
            GLES20.glAttachShader(program,vertexShader)
            GLES20.glAttachShader(program,fragmentShader)
            GLES20.glLinkProgram(program)
        }
        return program
    }

    fun dp2px(dp: Float,resources: Resources): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            resources.displayMetrics
        )
    }
}