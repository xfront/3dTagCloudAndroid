package com.moxun.tagcloud;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class FragmentTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, TestFragment.newInstance())
                .commit();
    }
}
