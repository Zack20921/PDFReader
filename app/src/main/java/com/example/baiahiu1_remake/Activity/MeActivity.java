package com.example.baiahiu1_remake.Activity;

import static com.example.baiahiu1_remake.Function.Code.bottom_menu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.example.baiahiu1_remake.R;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MeActivity extends AppCompatActivity {
    TextView txtPri, txtTerm, txtVersion, txtFeedBack;
    ImageView imgClose;
    WebView webView;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mapping();

        txtPri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgClose.setVisibility(View.VISIBLE);
                txtTerm.setVisibility(View.GONE);
                txtFeedBack.setVisibility(View.GONE);
                txtPri.setVisibility(View.GONE);
                txtVersion.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);

                webView.loadUrl("https://www.google.com/");
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgClose.setVisibility(View.GONE);
                txtTerm.setVisibility(View.VISIBLE);
                txtFeedBack.setVisibility(View.VISIBLE);
                txtPri.setVisibility(View.VISIBLE);
                txtVersion.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            }
        });

        txtTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/"));
                startActivity(browserIntent);
            }
        });

        txtFeedBack.setOnClickListener(v -> {
            Dialogsend();
        });

        //-------------------------Design bottom menu---------------------------
        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottomMenu);
        bottom_menu(bottomNavigation);
        bottomNavigation.setCurrentItem(2);

        //-------------------------Su Kien bottom menu---------------------------
        bottomNavigation.setOnTabSelectedListener((position, wasSelected) -> {
            switch (position){
                case 0:
                    Intent intent = new Intent(MeActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    Intent intent1 = new Intent(MeActivity.this, ToolActivity.class);
                    startActivity(intent1);
                    break;
            }
            return true;
        });
    }

    public void Dialogsend(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_send_email);

        EditText editFeed = (EditText) dialog.findViewById(R.id.editFeedBack);
        EditText editSubject = (EditText) dialog.findViewById(R.id.editSubject);
        Button btnSend = (Button) dialog.findViewById(R.id.btnSend);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);

        btnSend.setOnClickListener(v -> {
            if(editFeed.getText().toString().equals("")|| editSubject.getText().toString().equals("")){
                Toast.makeText(MeActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            }else {
                sendEmail(editFeed, editSubject);
            }
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void sendEmail(EditText editText, EditText editSubject){
        String subject = editSubject.getText().toString().trim();
        String feedBack = editText.getText().toString().trim();
        String email = "minh20921@gmail.com";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, feedBack);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose email client: "));
    }

    public void mapping(){
        txtPri = (TextView) findViewById(R.id.txtPri);
        txtTerm = (TextView) findViewById(R.id.txtTerm);
        txtVersion = (TextView) findViewById(R.id.txtVersion);
        txtFeedBack = (TextView) findViewById(R.id.txtFeedBack);
        imgClose = (ImageView) findViewById(R.id.imgClose);
        webView = (WebView) findViewById(R.id.webView);
    }

}