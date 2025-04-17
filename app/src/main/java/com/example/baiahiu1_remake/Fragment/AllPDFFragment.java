package com.example.baiahiu1_remake.Fragment;

import static com.example.baiahiu1_remake.Function.Code.pdfFiles;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import com.example.baiahiu1_remake.Adapter.AdapterAllPdf;
import com.example.baiahiu1_remake.Function.OnRenameCompleteListener;
import com.example.baiahiu1_remake.R;

import java.util.ArrayList;

public class AllPDFFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnRenameCompleteListener {
    SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_p_d_f, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swLayout);
        //Kết nối với Adapter
        //Để sử dụng RecyclerView cần có 3 thành phần chính: Adapter, Data Source (thường là các class với dữ liệu) và Layout;
        //Để sử dụng RecyclerView sẽ có 7 bước chính sau đây:
        //B1: Xây dựng moldel class  để sử dụng data source
        //B2: Thêm RecyclerView vào Activity. Tạo một Layout hiển thị thông tin theo cách mình muốn.
        //B3: Tạo RecyclerView.Adapter (Adapter là nơi xử lý dữ liệu và gán data cho RecyclerVIew) và ViewHolder để trả về cách hiển thị
        //B4: Set Adapter cho RecyclerView. Bao gồm set Layout cho RView (chiều ngang, dọc hoặc lưới) và set Adapter.
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new AdapterAllPdf(getActivity(), pdfFiles(getActivity())));

        swipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AdapterAllPdf adapter = new AdapterAllPdf(getActivity(), pdfFiles(getActivity()));
        adapter.setOnRenameCompleteListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        recyclerView.setAdapter(new AdapterAllPdf(getActivity(), pdfFiles(getActivity())));
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        },500);
    }

    //Sử dụng interface để sau khi rename thì sẽ load lại dữ liệu
    @Override
    public void onRenameComplete() {
        onResume();
    }
}