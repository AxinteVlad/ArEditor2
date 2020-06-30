package com.axintevlad.areditor2.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.axintevlad.areditor2.fragment.CloudAnchorFragment;
import com.axintevlad.areditor2.R;

import java.util.ArrayList;

public class ARActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> imageIds = (ArrayList<String>) getIntent().getSerializableExtra("images");
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("images", imageIds);


        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.fragment_container);
        if (frag == null) {
            frag = new CloudAnchorFragment();
            frag.setArguments(bundle);
            fm.beginTransaction().add(R.id.fragment_container, frag).commit();
        }
    }
}
