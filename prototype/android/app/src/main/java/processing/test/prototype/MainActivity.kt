package processing.test.prototype

import android.os.Bundle
import android.content.Intent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.support.v7.app.AppCompatActivity

import processing.android.PFragment
import processing.android.CompatUtils
import processing.core.PApplet

class MainActivity : AppCompatActivity() {

    private lateinit var sketch: PApplet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val frame = FrameLayout(this)
        frame.setId(CompatUtils.getUniqueViewId())
        setContentView(frame, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))
        sketch = Prototype()
        val fragment = PFragment(sketch)
        fragment.setView(frame, this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (sketch != null) {
            sketch.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onNewIntent(intent: Intent) {
        if (sketch != null) {
            sketch.onNewIntent(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (sketch != null) {
            sketch.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        if (sketch != null) {
            sketch.onBackPressed()
        }
    }

}