package com.example.baiahiu1_remake.Activity;

import static com.example.baiahiu1_remake.Function.Code.Delete;
import static com.example.baiahiu1_remake.Function.Code.addBook;
import static com.example.baiahiu1_remake.Function.Code.getBook;
import static com.example.baiahiu1_remake.Function.Code.getInf;
import static com.example.baiahiu1_remake.Function.Code.reName;
import static com.example.baiahiu1_remake.Function.Code.share;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.baiahiu1_remake.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;


public class PDFActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private BottomSheetDialog bottomSheetDialog;
    private int pageNumber, countPage;
    private String filePath;
    private boolean nightMode;
    ImageView imgBack, imgBook, imgMore2;
    TextView currentPageTextView;
    PDFView pdfView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfactivity);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mapping();

        filePath = getIntent().getStringExtra("pdfPath");
        File file = new File(filePath);

        // sharedPreferences lưu vào bộ nhớ máy
        sharedPreferences = getSharedPreferences("theme_mode", Context.MODE_PRIVATE);

        nightMode = sharedPreferences.getBoolean("night", false); // Mặc định chế độ Theme
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        ////////////////Nút Back về///////////////////////
        imgBack.setOnClickListener(v -> onBackPressed());


        /////bottom_sheet đổi chế độ
        imgBook.setOnClickListener(v -> {
            Context context = v.getContext();
            bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetThem);
            View sheetview = LayoutInflater.from(context)
                    .inflate(R.layout.bottom_bookmark, (ViewGroup) findViewById(R.id.bottom_bookmark_layout));

            RadioButton radioScrool = (RadioButton) sheetview.findViewById(R.id.radioScroll);
            RadioButton radioLight = (RadioButton) sheetview.findViewById(R.id.radioLight);
            RadioButton radioNight = (RadioButton) sheetview.findViewById(R.id.radioNight);
            RadioGroup radioGroup = (RadioGroup) sheetview.findViewById(R.id.radioGroupThemeMode);

            radioScrool.setChecked(true);
            if (nightMode) {
                radioNight.setChecked(true);
            } else {
                radioLight.setChecked(true);
            }

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if(checkedId == R.id.radioLight){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night",false);
                    editor.commit();
                }

                if(checkedId==R.id.radioNight){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night",true);
                    editor.commit();

                }
            });

            bottomSheetDialog.setContentView(sheetview);
            bottomSheetDialog.show();
        });

        // bottom_sheet thông tin file PDF///////////////
        imgMore2.setOnClickListener(v -> {
            Context context = v.getContext();
            bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetThem);
            View sheetview = LayoutInflater.from(context)
                    .inflate(R.layout.bottom_sheet, (ViewGroup) findViewById(R.id.bottom_sheet_layout));

            TextView txtPath = (TextView) sheetview.findViewById(R.id.txtPath);
            TextView txtName = (TextView) sheetview.findViewById(R.id.txtName);
            TextView txtMb = (TextView) sheetview.findViewById(R.id.txtMb);
            TextView txtDay = (TextView) sheetview.findViewById(R.id.txtDay);
            TextView txtPage = (TextView) sheetview.findViewById(R.id.txtNumberPage);
            TextView txtRename = (TextView) sheetview.findViewById(R.id.txtRename);
            EditText editRename = (EditText) sheetview.findViewById(R.id.editRename);
            TextView txtDelete = (TextView) sheetview.findViewById(R.id.txtDelete);
            TextView txtBook = (TextView) sheetview.findViewById(R.id.txtBook);
            ImageView imgBook = (ImageView) sheetview.findViewById(R.id.imgBookB);
            TextView txtShare = (TextView) sheetview.findViewById(R.id.txtShare);

            getInf(filePath, txtPath, txtDay, txtName, txtMb);//Hàm set thông tin PDF

            //Set BookMark
            int check2 = getBook(context,filePath); //i bằng Book
            if(check2 == 0){
                txtBook.setText("Bookmark");
                int tintColor = Color.argb(1, 255, 255, 255);;
                imgBook.setColorFilter(tintColor);
            }else{
                txtBook.setText("UnBookmark");
                int tintColor = Color.argb(255, 251, 181, 3);;
                imgBook.setColorFilter(tintColor);
            }

            txtBook.setOnClickListener(new View.OnClickListener() {
                int check3 = getBook(context,filePath); //i bằng Book
                @Override
                public void onClick(View v) {
                    if(check3 == 0){
                        addBook(context, filePath, 1);
                        txtBook.setText("UnBookmark");
                        int color = Color.parseColor("#FBB503");
                        imgBook.setImageDrawable(new ColorDrawable(color));
                        bottomSheetDialog.dismiss();
                    }else{
                        addBook(context, filePath, 0);
                        txtBook.setText("Bookmark");
                        imgBook.setImageDrawable(new ColorDrawable());
                        bottomSheetDialog.dismiss();
                    }

                }
            });

            txtRename.setOnClickListener(v1 -> {
                txtRename.setVisibility(View.GONE);
                editRename.setVisibility(View.VISIBLE);

            });

            editRename.setOnKeyListener((v12, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    String newPath = reName(editRename, bottomSheetDialog, context,filePath);
                    if (newPath != null) {
                        filePath = newPath;
                    }
                }
                return false;
            });

            Delete(txtDelete, context,filePath,bottomSheetDialog); //Hàm xóa
            share(txtShare, context, file);

            String number  = String.valueOf(countPage);
            txtPage.setText(number);

            bottomSheetDialog.setContentView(sheetview);
            bottomSheetDialog.show();

        });
        showPDF(filePath);
    }
    public void showPDF(String filepath){
        File file = new File(filepath);
        Uri path = Uri.fromFile(file);

        pdfView.fromUri(path)
                .defaultPage(pageNumber)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        countPage =pageCount;
                        pageNumber = page +1;
                        currentPageTextView.setText(pageNumber +"/"+ pageCount);
                    }
                })
                .enableAnnotationRendering(true)
//                .autoSpacing(false)
                .enableSwipe(true)
//                .fitEachPage(true)
                .enableDoubletap(true)
//                .pageSnap(true)
                .swipeHorizontal(false)
//                .spacing(10)
                .load();
    }

    public void mapping(){
        currentPageTextView = (TextView) findViewById(R.id.currentPageTextView);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBook = (ImageView) findViewById(R.id.imgBook);
        imgMore2 = (ImageView) findViewById(R.id.imgMore2);
        pdfView = (PDFView) findViewById(R.id.pdfView);
    }
}