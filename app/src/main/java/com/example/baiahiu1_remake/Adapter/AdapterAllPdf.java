package com.example.baiahiu1_remake.Adapter;

import static com.example.baiahiu1_remake.Function.Code.Delete;
import static com.example.baiahiu1_remake.Function.Code.addBook;
import static com.example.baiahiu1_remake.Function.Code.addDate;
import static com.example.baiahiu1_remake.Function.Code.getBook;
import static com.example.baiahiu1_remake.Function.Code.getInf;
import static com.example.baiahiu1_remake.Function.Code.reName;
import static com.example.baiahiu1_remake.Function.Code.share;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baiahiu1_remake.Activity.MainActivity;
import com.example.baiahiu1_remake.Activity.PDFActivity;
import com.example.baiahiu1_remake.Function.Code;
import com.example.baiahiu1_remake.Function.Database;
import com.example.baiahiu1_remake.Function.OnRenameCompleteListener;
import com.example.baiahiu1_remake.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

public class AdapterAllPdf extends RecyclerView.Adapter<AdapterAllPdf.AdapterViewholder> {
    Context context;
    private BottomSheetDialog bottomSheetDialog;
    List<String> pdflist;
    private OnRenameCompleteListener renameCompleteListener;

    public AdapterAllPdf(Context context, List<String> pdflist) {
        this.context = context;
        this.pdflist = pdflist;
    }

    public void setOnRenameCompleteListener(OnRenameCompleteListener listener) {
        this.renameCompleteListener = listener;
    }
    //Phương thức này dùng để setLayout cho RecycleView
    //Phương thức onCreateViewHolder cần một View để trả về một ViewHolder mới.
    // Vì vậy sẽ phải “inflate” (chuyển đổi) layout từ layout file(Xml) sang View(Java code) để có thể setLayout cho RecycleView
    @NonNull
    @Override
    public AdapterViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //LayoutInflater là 1 component (thành phần) giúp chuyển layout file(Xml) thành View(Java code) trong Android.
        //Nó thường được sử dụng trong phương thức onCreateView của fragment hoặc phương thức getView khi custom adapter.
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pdf_view, parent,false);
        return new AdapterViewholder(view);
    }

    //Mỗi khi ViewHolder được tạo trước đó được sử dụng lại, RecyclerView sẽ bảo Adapter cập nhật dữ liệu của nó.
    //Bạn tùy chỉnh quy trình này bằng cách ghi đè lên BindViewHolder().
    //TÓM LẠI: Phương thức này sẽ ghi đè dữ liệu từ Class lên các Views.
    @Override
    public void onBindViewHolder(@NonNull AdapterViewholder holder, int position) {
        String path = pdflist.get(position); //Tạo biến String = đường dẫn từ pdflist
        File pdfFile = new File(path); //Tạo một đối tượng File từ đường dẫn tệp PDF đã lấy.
        String filename = pdfFile.getName(); //Lấy tên tệp PDF (không bao gồm đường dẫn).
        long lastModifiedTime = pdfFile.lastModified(); //Lấy thời gian sửa đổi cuối cùng của tệp PDF (dưới dạng timestamp).
        Date lastModifiedDate = new Date(lastModifiedTime); //Chuyển đổi timestamp thành đối tượng Date.
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm"); //Tạo định dạng cho thời gian (ngày/tháng/năm - giờ:phút).
        String formattedDate = dateFormat.format(lastModifiedDate);// Định dạng thời gian thành chuỗi theo định dạng đã chọn.
        long fileSizeBytes = pdfFile.length(); //Lấy kích thước tệp PDF
        double fileSizeKB = fileSizeBytes / 1024.0; // Chuyển đổi kích thước tệp từ byte sang kilobyte.

        holder.txtName.setText(filename);
        holder.txtDay.setText(formattedDate);
        holder.txtMb.setText(String.format("%.2f MB", fileSizeKB / 1024.0));

        //Check xem file pdf đã được bookmark chưa. Nếu rồi thì gắn sao
        int check = getBook(context,path); //check bằng Book
        if(check == 0){
            holder.imgStarB.setVisibility(View.GONE);
        }else{
            holder.imgStarB.setVisibility(View.VISIBLE);
        }

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDate(context, path);
                Intent intent = new Intent(context, PDFActivity.class);
                intent.putExtra("pdfPath",path);
                context.startActivity(intent);//Bằng cách sử dụng context.startActivity(intent), bạn đảm bảo rằng Intent được khởi chạy trong ngữ cảnh đúng (tức là ngữ cảnh của Activity hoặc Fragment bao quanh).
            }
        });

        holder.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sử dụng context để tạo BottomSheetDialog
                if (context instanceof MainActivity) {
                    bottomSheetDialog = new BottomSheetDialog((MainActivity) context, R.style.BottomSheetThem);

                    // Sử dụng LayoutInflater để tìm và gán view
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View sheetView = inflater.inflate(R.layout.bottom_sheet, holder.itemView.findViewById(R.id.bottom_sheet_layout), false);
                    bottomSheetDialog.setContentView(sheetView);
                    bottomSheetDialog.show();

                    TextView txtShare = (TextView) sheetView.findViewById(R.id.txtShare);
                    TextView txtNumberPage = (TextView) sheetView.findViewById(R.id.txtNumberPage);
                    TextView txtPath = (TextView) sheetView.findViewById(R.id.txtPath);
                    TextView txtName = (TextView) sheetView.findViewById(R.id.txtName);
                    TextView txtMb = (TextView) sheetView.findViewById(R.id.txtMb);
                    TextView txtDay = (TextView) sheetView.findViewById(R.id.txtDay);
                    TextView txtDelete = (TextView) sheetView.findViewById(R.id.txtDelete);
                    TextView txtRename = (TextView) sheetView.findViewById(R.id.txtRename);
                    EditText editRename = (EditText) sheetView.findViewById(R.id.editRename);
                    TextView txtBook = (TextView) sheetView.findViewById(R.id.txtBook);
                    ImageView imgBook = (ImageView) sheetView.findViewById(R.id.imgBookB);

                    //Set tổng số trang của flie PDF
                    int i = 0;
                    try {
                        i = countPages(pdfFile);
                        txtNumberPage.setText(String.valueOf(i));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //Check để set BookMark
                    int check2 = getBook(context,path); //i bằng Book
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
                        int check3 = getBook(context,path); //i bằng Book
                        @Override
                        public void onClick(View v) {
                            if(check3 == 0){
                                addBook(context, path, 1);
                                holder.imgStarB.setVisibility(View.VISIBLE);
                                txtBook.setText("UnBookmark");
                                int color = Color.parseColor("#FBB503");
                                imgBook.setImageDrawable(new ColorDrawable(color));
                                bottomSheetDialog.dismiss();
                            }else{
                                addBook(context, path, 0);
                                holder.imgStarB.setVisibility(View.GONE);
                                txtBook.setText("Bookmark");
                                imgBook.setImageDrawable(new ColorDrawable());
                                bottomSheetDialog.dismiss();
                            }

                        }
                    });

                    getInf(path, txtPath, txtDay, txtName, txtMb);
                    share(txtShare, context, pdfFile);

                    txtRename.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            txtRename.setVisibility(View.GONE);
                            editRename.setVisibility(View.VISIBLE);

                        }
                    });

                    editRename.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                                String newPath = reName(editRename, bottomSheetDialog, context,path);
                                if (newPath != null) {
                                    if (renameCompleteListener != null) {
                                        renameCompleteListener.onRenameComplete();
                                    }
                                }
                            }
                            return false;
                        }
                    });

                    txtDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Are you sure you want to delete this PDF?");

                            builder.setPositiveButton("Delete", (dialog, which) -> {
                                // Delete the file
                                File pdfFile = new File(path);
                                if (pdfFile.exists()) {
                                    boolean deleted = pdfFile.delete();
                                    if (deleted) {
                                        Toast.makeText(context, "PDF deleted successfully!", Toast.LENGTH_SHORT).show();
                                        //Xóa dữ liệu file PDF trong database
                                        Database database = new Database(context);
                                        Cursor data = database.getData("SELECT * FROM PdfFile");
                                        while (data.moveToNext()){
                                            if(data.getString(1).equals(path)){
                                                database.creatData("DELETE FROM PdfFile WHERE Path = '"+path+"'");
                                            }
                                        }
                                        if (renameCompleteListener != null) {
                                            renameCompleteListener.onRenameComplete();
                                        }
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
            }
        });
    }

    //Để vẽ được danh sách trên màn hình ,RecyclerView sẽ hỏi Adapter sẽ có tổng cộng bao nhiêu item.
    //Và Adapter của chúng ta sẽ trả lời thông tin này ở trong hàm getItemCount().
    //TÓM LẠI: Phương thức này sẽ trả về số lượng view muốn hiển thị ở RecycleView. Như ở đây sẽ trả về số lượng filePdf mà mình có.
    @Override
    public int getItemCount() {
        return pdflist.size();
    }

    //Với RecycleView bắt buộc phải có class ViewHolder để tối ưu hóa dữ liệu.
    //Bên cạnh đó, RecyclerView Adapter sẽ yêu cầu "ViewHolder" object trong đó mô tả và cung cấp quyền truy cập vào tất cả các Views trong layout
    public class AdapterViewholder extends RecyclerView.ViewHolder{
        TextView txtName, txtMb, txtDay;

        ImageView imgMore, imgStarB;
        public AdapterViewholder(@NonNull View itemView) {
            super(itemView);
            txtDay = itemView.findViewById(R.id.txtDay);
            txtName = itemView.findViewById(R.id.txtName);
            txtMb = itemView.findViewById(R.id.txtMb);
            imgMore = itemView.findViewById(R.id.imgMore2);
            imgStarB = itemView.findViewById(R.id.imgStarB);
        }
    }

    private int countPages(File pdfFile) throws IOException {
        int totalpages = 0;
        try {
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer pdfRenderer = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                totalpages = pdfRenderer.getPageCount();
                pdfRenderer.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return totalpages;
    }
}
