package com.zjl.gpuimage.library

import android.opengl.GLES20
import android.util.Log

/**
 * Project Name: MyGpuImageProject
 * ClassName:    OpenGLUtils
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年08月09日 9:38
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
const val LOG_TAG = "ZJL_OPENGL"
object OpenGLUtils {

    fun loadShader(shaderType: Int, shaderSource: String): Int {
        val compiled = IntArray(1)
        val shader = GLES20.glCreateShader(shaderType)

        GLES20.glShaderSource(shader, shaderSource)
        GLES20.glCompileShader(shader)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
            Log.d(LOG_TAG,"Load Shader Error")
            return 0
        }
        return shader
    }

    fun loadProgram(vetexShaderSource: String, fragmentShaderSource: String): Int {
        val link = IntArray(1)
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vetexShaderSource)
        if (vertexShader == 0) {
            Log.e(LOG_TAG, "VertexShader Error")
            return 0
        }
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource)
        if (fragmentShader == 0) {
            Log.e(LOG_TAG, "fragmentShader Error")
            return 0
        }
        val programId = GLES20.glCreateProgram()
        GLES20.glAttachShader(programId, vertexShader)
        GLES20.glAttachShader(programId, fragmentShader)

        GLES20.glLinkProgram(programId)
        GLES20.glGetShaderiv(programId, GLES20.GL_LINK_STATUS, link, 0)
        if (link[0] <= 0) {
            Log.e(LOG_TAG, "Linking Failed")
            return 0
        }
        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)

        return programId
    }
}