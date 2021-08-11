package com.zjl.mygpuimageproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zjl.mygpuimageproject.texturerenderer.TextureIconRenderer
import com.zjl.mygpuimageproject.texturerenderer.TexturePicRenderer
import kotlinx.android.synthetic.main.activity_render_video.*

class RenderVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render_video)
        gl_texture.apply {
            setEGLContextClientVersion(2)
            val render = TextureIconRenderer(context,0.5f, this, OpenGLUtils.dp2px(100f,context.resources), OpenGLUtils.dp2px(100f,context.resources))
            setRenderer(render)
        }
    }
}