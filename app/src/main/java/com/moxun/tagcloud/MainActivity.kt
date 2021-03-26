package com.moxun.tagcloud

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.moxun.tagcloudlib.view.TagCloudView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tagCloudView = findViewById<View>(R.id.tag_cloud) as TagCloudView

        val textTagsAdapter = TextTagsAdapter(*arrayOfNulls(20))
        val viewTagsAdapter = ViewTagsAdapter()
        val vectorTagsAdapter = VectorTagsAdapter()
        tagCloudView.setAdapter(textTagsAdapter)

        findViewById<View>(R.id.tag_text).setOnClickListener { tagCloudView.setAdapter(textTagsAdapter) }
        findViewById<View>(R.id.tag_view).setOnClickListener { tagCloudView.setAdapter(viewTagsAdapter) }
        findViewById<View>(R.id.tag_vector).setOnClickListener { tagCloudView.setAdapter(vectorTagsAdapter) }
        findViewById<View>(R.id.test_fragment).setOnClickListener {
            startActivity(Intent(this@MainActivity, FragmentTestActivity::class.java))
        }
    }
}