package com.example.baiahiu1_remake.Activity;

import static com.example.baiahiu1_remake.Function.Code.bottom_menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.example.baiahiu1_remake.Adapter.AdapterFolder;
import com.example.baiahiu1_remake.Function.FileItem;
import com.example.baiahiu1_remake.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToolActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private AdapterFolder adapterFolder;
    private List<FileItem> folderClasses;
    RecyclerView recyclerViewTool;
    LinearLayout reBar;
    private String currentPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        recyclerViewTool = (RecyclerView) findViewById(R.id.recyclerViewTool);
        reBar = (LinearLayout) findViewById(R.id.reBar);

        setupRecyclerView();
        //-------------------------Design bottom menu---------------------------
        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottomMenu);
        bottom_menu(bottomNavigation);
        bottomNavigation.setCurrentItem(1);

        //-------------------------Su Kien bottom menu---------------------------
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position){
                    case 0:
                        Intent intent = new Intent(ToolActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        Intent intent1 = new Intent(ToolActivity.this, MeActivity.class);
                        startActivity(intent1);
                        break;
                }
                return true;
            }
        });

    }

    private void setupRecyclerView() {
        recyclerViewTool.setLayoutManager(new LinearLayoutManager(this));

        String path = getIntent().getStringExtra("path");
        if (path == null) {
            path = Environment.getExternalStorageDirectory().getPath();
        }
        currentPath = path;

        loadFiles(new File(currentPath));
    }

    private void loadFiles(File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            Toast.makeText(this, "Directory not found", Toast.LENGTH_SHORT).show();
            return;
        }

        currentPath = directory.getPath();
        folderClasses = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                Date lastModified = new Date(file.lastModified());
                int itemCount = file.isDirectory() ? (file.listFiles() == null ? 0 : file.listFiles().length) : 0;
                folderClasses.add(new FileItem(file.getName(), file.getPath(), file.isDirectory(), lastModified, itemCount));
            }
        }

        updateBreadcrumb(currentPath);

        adapterFolder = new AdapterFolder(this, folderClasses);
        recyclerViewTool.setAdapter(adapterFolder);
    }

    private void updateBreadcrumb(String path) {
        reBar.removeAllViews();
        String displayPath = path.replace(Environment.getExternalStorageDirectory().getAbsolutePath(), "Internal storage");
        String[] parts = displayPath.split("/");

        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                ImageView arrow = new ImageView(this);
                arrow.setImageResource(R.drawable.baseline_chevron_right_24); // Your arrow drawable
                reBar.addView(arrow);
            }

            TextView partTextView = new TextView(this);
            partTextView.setText(parts[i]);
            partTextView.setPadding(8, 0, 8, 0);
            final int index = i;
            partTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleBreadcrumbClick(index);
                }
            });
            reBar.addView(partTextView);
        }
    }

    private void handleBreadcrumbClick(int index) {
        String[] parts = new String[reBar.getChildCount() / 2 + 1];
        int partIndex = 0;
        for (int i = 0; i < reBar.getChildCount(); i++) {
            View view = reBar.getChildAt(i);
            if (view instanceof TextView) {
                parts[partIndex++] = ((TextView) view).getText().toString();
            }
        }
        StringBuilder pathBuilder = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath());

        for (int i = 0; i <= index; i++) {
            if (!parts[i].equals("Internal storage")) {
                pathBuilder.append("/").append(parts[i]);
            }
        }

        File directory = new File(pathBuilder.toString());
        if (directory.exists() && directory.isDirectory()) {
            loadFiles(directory);
        } else {
            Toast.makeText(this, "Directory does not exist", Toast.LENGTH_SHORT).show();
        }
    }

}