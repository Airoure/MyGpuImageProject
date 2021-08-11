package com.zjl.mygpuimageproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zjl.mygpuimageproject.renderer.GaussicBackRenderer
import com.zjl.mygpuimageproject.renderer.SmallVideoRenderer
import kotlinx.android.synthetic.main.activity_render_gaussic.*

class RenderGaussicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_render_gaussic)

        gl_surface.apply {
            setEGLContextClientVersion(2)
            val render = SmallVideoRenderer()
            setRenderer(render)
        }
    }
}