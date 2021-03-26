package com.moxun.tagcloud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.moxun.tagcloud.TestFragment

class FragmentTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_test)
        supportFragmentManager.beginTransaction()
                .add(R.id.container, TestFragment.Companion.newInstance())
                .commit()
    }
}