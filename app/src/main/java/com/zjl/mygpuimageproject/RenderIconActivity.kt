package com.zjl.mygpuimageproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zjl.mygpuimageproject.renderer.IconRenderer
import com.zjl.mygpuimageproject.renderer.PicRenderer
import kotlinx.android.synthetic.main.activity_render_pic.*

class RenderIconActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render_icon)
        render.apply {
            setEGLContextClientVersion(2)
            val shader = IconRenderer(this@RenderIconActivity,0.5f, this, OpenGLUtils.dp2px(100f,context.resources), OpenGLUtils.dp2px(100f,context.resources))
            setRenderer(shader)
        }
    }
}