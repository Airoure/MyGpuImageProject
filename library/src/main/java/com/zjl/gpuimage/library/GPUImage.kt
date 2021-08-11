package com.zjl.gpuimage.library

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView

/**
 * Project Name: MyGpuImageProject
 * ClassName:    GPUImage
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年08月09日 10:05
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
class GPUImage(context: Context) {

    private var surfaceType: Int = SURFACE_TYPE_SURFACE_VIEW
    private var glSurfaceView: GLSurfaceView? = null
    private var glTextureView: GLTextureView? = null

    private var renderer: GPUImageRenderer? = null

    companion object{
        const val SURFACE_TYPE_SURFACE_VIEW = 0
        const val SURFACE_TYPE_TEXTURE_VIEW = 1
    }

    fun setGLSurfaceView(view: GLSurfaceView) {
        surfaceType = SURFACE_TYPE_SURFACE_VIEW
        glSurfaceView = view
        glSurfaceView?.let { glSurfaceView ->
            glSurfaceView.setEGLContextClientVersion(2)
            glSurfaceView.setEGLConfigChooser(8,8,8,8,16,0)
            glSurfaceView.holder.setFormat(PixelFormat.RGBA_8888)
            glSurfaceView.setRenderer(renderer)
            glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            glSurfaceView.requestRender()
        }
    }

    fun setGLTextureView(view: GLTextureView) {
        surfaceType = SURFACE_TYPE_TEXTURE_VIEW
        glTextureView = view
        glTextureView?.let { glTextureView ->
            glTextureView.setEGLContextClientVersion(2)
            glTextureView.setEGLConfigChooser(8,8,8,8,16,0)
            glTextureView.isOpaque = false
            glTextureView.setRenderer(renderer)
            glTextureView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            glTextureView.requestRender()
        }
    }
}