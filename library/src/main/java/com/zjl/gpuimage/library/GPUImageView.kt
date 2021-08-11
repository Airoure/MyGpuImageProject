package com.zjl.gpuimage.library

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/**
 * Project Name: MyGpuImageProject
 * ClassName:    GPUImageView
 *
 * Description:
 *
 * @author  zjl
 * @date    2021年08月09日 10:00
 *
 * Copyright (c) 2021年, 4399 Network CO.ltd. All Rights Reserved.
 */
class GPUImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    private var mSurfaceType = GPUImage.SURFACE_TYPE_SURFACE_VIEW
    private val mContext: Context = context
    private val mGpuImage: GPUImage = GPUImage(context)
    private var mSurfaceView: View

    init {
        if (attributeSet != null) {
            val a = context.obtainStyledAttributes(attributeSet, R.styleable.GPUImageView, 0, 0)
            try {
                mSurfaceType =
                    a.getInt(R.styleable.GPUImageView_gpuimage_surface_type, mSurfaceType)
            } finally {
                a.recycle()
            }
        }
        if (mSurfaceType == GPUImage.SURFACE_TYPE_SURFACE_VIEW) {
            mSurfaceView = GPUImageSurfaceView(mContext)
            mGpuImage.setGLSurfaceView(mSurfaceView as GLSurfaceView)
        } else {
            mSurfaceView = GPUImageTextureView(mContext)
            mGpuImage.setGLTextureView(mSurfaceView as GLTextureView)
        }
        addView(mSurfaceView)
    }

    fun getGpuImage() = mGpuImage


    private class GPUImageSurfaceView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null
    ) : GLSurfaceView(context, attributeSet) {

    }

    private class GPUImageTextureView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null
    ) : GLTextureView(context, attributeSet) {

    }

}