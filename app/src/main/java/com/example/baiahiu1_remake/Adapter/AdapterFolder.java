package com.example.baiahiu1_remake.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.baiahiu1_remake.Activity.ToolActivity;
import com.example.baiahiu1_remake.Function.FileItem;
import com.example.baiahiu1_remake.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdapterFolder extends RecyclerView.Adapter<AdapterFolder.AdapterFolderViewHolder>{
    Context context;
    List<FileItem> fileList;

    public AdapterFolder(Context context, List<FileItem> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public AdapterFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.file_layout, parent,false);
        return new AdapterFolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFolderViewHolder holder, int position) {
        FileItem fileItem = fileList.get(position);
        holder.name.setText(fileItem.getName());
        holder.dateCreated.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(fileItem.getDateCreated()));
        holder.itemCount.setText(fileItem.isDirectory() ? "Items: " + fileItem.getItemCount() : "");

        if (!fileItem.isDirectory()) {
            holder.icon.setImageResource(R.drawable.file2); // Kiểm tra xem nếu là file thì sẽ dùng imgIcon khác
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileItem.isDirectory()) {
                    // Open folder
                    Intent intent = new Intent(context, ToolActivity.class);
                    intent.putExtra("path", fileItem.getPath());
                    context.startActivity(intent);
                } else {
                    // Open file
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(fileItem.getPath()));
                    intent.setDataAndType(fileUri, getMimeType(fileItem.getPath()));
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class AdapterFolderViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        ImageView icon;
        TextView dateCreated;
        TextView itemCount;
        public AdapterFolderViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.txtTitle);
            dateCreated = (TextView) itemView.findViewById(R.id.txtDate);
            itemCount = (TextView) itemView.findViewById(R.id.txtItems);
            icon = itemView.findViewById(R.id.imgIcon);
        }
    }
    private String getMimeType(String path) {
        String type = null;
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            type = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
