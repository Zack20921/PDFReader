package com.example.baiahiu1_remake.Fragment;

import static com.example.baiahiu1_remake.Function.Code.pdfFiles;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baiahiu1_remake.Adapter.AdapterAllPdf;
import com.example.baiahiu1_remake.Function.Database;
import com.example.baiahiu1_remake.Function.OnRenameCompleteListener;
import com.example.baiahiu1_remake.R;

import java.util.ArrayList;

public class RecentsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnRenameCompleteListener {
    SwipeRefreshLayout swipeRefreshLayout;
    private Database database;
    RecyclerView recyclerView;
    ArrayList<String> pdfPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recents, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerRecent);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swLayoutRecent);
         pdfPath = new ArrayList<>();
        database = new Database(getActivity());

        ArrayList<String> asd = pdfFiles(getActivity());

        refreshData();
        swipeRefreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
    private void refreshData() {
        Cursor data = database.getData("SELECT * FROM PdfFile WHERE Date IS NOT NULL ORDER BY Date DESC LIMIT 5");
        pdfPath.clear();
        while (data.moveToNext()) {
            String path = data.getString(1);
            pdfPath.add(path);
        }
        AdapterAllPdf adapter = new AdapterAllPdf(getActivity(), pdfPath);
        adapter.setOnRenameCompleteListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter); // Update adapter
    }

    @Override
    public void onRefresh() {
        refreshData();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        },500);
    }

    @Override
    public void onRenameComplete() {
        onResume();
    }
}