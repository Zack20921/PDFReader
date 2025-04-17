package com.example.baiahiu1_remake.Function;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.baiahiu1_remake.Activity.MainActivity;
import com.example.baiahiu1_remake.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Code {

    public static void bottom_menu(AHBottomNavigation bottomNavigation){

        // Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.Recent, R.drawable.home2, R.color.grey);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.Tool, R.drawable.tool, R.color.grey);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.Me, R.drawable.baseline_person_24, R.color.grey);
        // Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);

        bottomNavigation.setTitleTextSize(30,30);

        bottomNavigation.setAccentColor(Color.parseColor("#EF6969"));
    }


    public static void getInf(String filepath, TextView txtPath, TextView txtDay, TextView txtName, TextView txtMb){
        File pdfFile = new File(filepath);
        String filename = pdfFile.getName();
        long lastModifiedTime = pdfFile.lastModified();
        Date lastModifiedDate = new Date(lastModifiedTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        String formattedDate = dateFormat.format(lastModifiedDate);
        long fileSizeBytes = pdfFile.length();
        double fileSizeKB = fileSizeBytes / 1024.0;

        txtPath.setText(filepath);
        txtDay.setText(formattedDate);
        txtName.setText(filename);
        txtMb.setText(String.format("%.2f MB", fileSizeKB / 1024.0));
    }

    public static void share(TextView txtShare, Context context, File pdfFile){
        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pdfFile);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.addFlags(intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(Intent.createChooser(intent, "Share File: "));
            }
        });
    }
    public static String reName(EditText editRename, BottomSheetDialog bottomSheetDialog, Context context, String filepath){
        String newa = null;
        String newName = editRename.getText().toString().trim();

        File oldFile = new File(filepath);
        String newFilePath = oldFile.getParent() + File.separator + newName + ".pdf";
        File newFile = new File(newFilePath);
        String FileName = newFile.getName();

        if (newName.isEmpty()) {
            Toast.makeText(context, "Please enter a new name", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        }else {
            if (newFile.exists()) {
                Toast.makeText(context, "File with that name already exists", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            } else {
                if (oldFile.renameTo(newFile)) {
                    newa = newFilePath;
                    Toast.makeText(context, "File renamed successfully!", Toast.LENGTH_SHORT).show();
                    Database database = new Database(context);
                    Cursor data = database.getData("SELECT * FROM PdfFile");
                    while (data.moveToNext()) {
                        if (data.getString(1).equals(filepath)) {
                            database.creatData("UPDATE PdfFile SET Path = '" + newFilePath + "', FileName = '"+FileName+"' WHERE Path = '" + filepath + "'");
                        }
                    }

                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(context, "Error renaming file. Check permissions.", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                }
            }
        }
        return newa;
    }

    public static void Delete(TextView textView ,Context context,String filepath, BottomSheetDialog bottomSheetDialog){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Confirmation dialog (optional)
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this PDF?");

                builder.setPositiveButton("Delete", (dialog, which) -> {
                    // Delete the file
                    File pdfFile = new File(filepath);
                    if (pdfFile.exists()) {
                        boolean deleted = pdfFile.delete();
                        if (deleted) {
                            Toast.makeText(context, "PDF deleted successfully!", Toast.LENGTH_SHORT).show();
                            //Xóa dữ liệu file PDF trong database
                            Database database = new Database(context);
                            Cursor data = database.getData("SELECT * FROM PdfFile");
                            while (data.moveToNext()){
                                if(data.getString(1).equals(filepath)){
                                    database.creatData("DELETE FROM PdfFile WHERE Path = '"+filepath+"'");
                                }
                            }
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Error deleting PDF!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "PDF file not found!", Toast.LENGTH_SHORT).show();
                    }
                    bottomSheetDialog.dismiss();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        });

    }

    ///Hàm lấy path file PDF trong bộ nhớ ngoài đồng thời add vào thông tin file vào database
    public static ArrayList<String> pdfFiles(Context context) {
        Database database = new Database(context);
        ContentResolver contentResolver = context.getContentResolver(); // Tạo biến ContentResolver để cho phép truy cập bộ lưu trữ của thiết bị như video, img,...

        String mime = MediaStore.Files.FileColumns.MIME_TYPE + "=?"; //Tạo biến String mine để
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        String[] args = new String[]{mimeType};
        String[] proj = {MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.DISPLAY_NAME};
        String sortingOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
        Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"),
                proj, mime, args, sortingOrder);

        ArrayList<String> pdfFiles = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                String path = cursor.getString(index);
                String filename = new File(path).getName();

                //Kiểm tra xem path này đã có trong Database chưa. Nếu chưa thì sẽ add vào database
                if(!path.equals("")){
                    int dem =0;
                    Cursor data = database.getData("SELECT * FROM PdfFile");

                    while (data.moveToNext()){
                        if( data.getString(4).equals(filename)) {
                            dem++;
                            if(!data.getString(1).equals(path)){
                                database.creatData("UPDATE PdfFile SET Path = '"+path+"' WHERE FileName = '"+filename+"'");
                            }
                        }

                    }
                    if(dem == 0){
                        database.creatData("INSERT INTO PdfFile VALUES(null, '"+path+"', 0, null, '"+filename+"')");
                    }
                }
                //Add path vào chuỗi
                pdfFiles.add(path);
            }
            cursor.close();
        }
        return pdfFiles;
    }

    public static void addBook(Context context, String path, int i){
        Database database = new Database(context);
        database.creatData("UPDATE PdfFile SET Book = "+i+" WHERE Path = '"+path+"'");
    }

    public static int getBook(Context context, String path){
        int i = 1;
        Database database = new Database(context);
        Cursor data = database.getData("SELECT * FROM PdfFile");
        while (data.moveToNext()){
            if(data.getString(1).equals(path)){
                i = data.getInt(2);
            }
        }
        return  i;
    }

    public static void addDate(Context context, String path){
        Database database = new Database(context);
        database.creatData("UPDATE PdfFile SET Date = CURRENT_TIMESTAMP WHERE Path = '"+path+"'");
    }

}
