package com.lyyjy.yfyb.bionicfish.Activity;

import android.content.res.AssetManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lyyjy.yfyb.bionicfish.HelpDocument.HelpItemFragment;
import com.lyyjy.yfyb.bionicfish.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpDocumentActivity extends AppCompatActivity {

    private static final String TAG=HelpDocumentActivity.class.getSimpleName();
    private static final String HTML_FOLDER = "html";

    private static final Map<String, String> FILE_PAGER_MAP = new HashMap<String, String>() {
        {
            put("快速指南","quick_tour.html");
            put("操作步骤","operator_manual.html");
            put("参数规格","specification.html");
            put("安全须知","safety_instruction.html");
        }
    };

    private List<String> mPageTitles = new ArrayList<String>(){
        {
            add("快速指南");
            add("操作步骤");
            add("参数规格");
            add("安全须知");
        }
    };
    private List<String> mHelpTexts = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_document);

        initDocuments();

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return HelpItemFragment.newInstance(mHelpTexts.get(position));
            }

            @Override
            public int getCount() {
                return mHelpTexts.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mPageTitles.get(position);
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return true;
    }

    private void initDocuments() {
        AssetManager assetManager = getAssets();
        try {
            String names[] = assetManager.list(HTML_FOLDER);
            for (String name : mPageTitles) {

                String path = HTML_FOLDER + File.separator + FILE_PAGER_MAP.get(name);
                InputStream inputStream = assetManager.open(path);
                if (inputStream==null){
                    continue;
                }
                byte[] data = new byte[inputStream.available()];
                inputStream.read(data);
                mHelpTexts.add(new String(data, "UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
