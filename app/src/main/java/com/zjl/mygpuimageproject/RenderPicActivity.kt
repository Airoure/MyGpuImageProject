package com.zjl.mygpuimageproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zjl.mygpuimageproject.renderer.PicRenderer
import kotlinx.android.synthetic.main.activity_render_pic.*

class RenderPicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render_pic)
        render.apply {
            setEGLContextClientVersion(2)
            val shader = PicRenderer(this@RenderPicActivity)
            setRenderer(shader)
        }
    }
}