package com.moutamid.whatstool.textmagic;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.whatstool.R;

import java.util.ArrayList;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.FontHolder> {
    Context context;
    ArrayList<Font> dataSet;
    AdapterView.OnItemClickListener onItemClickListener;
    SharedPreferences preferences;

    public FontAdapter(Context context2, ArrayList<Font> arrayList, AdapterView.OnItemClickListener onItemClickListener2) {
        this.dataSet = arrayList;
        this.onItemClickListener = onItemClickListener2;
        this.context = context2;
    }

    @NonNull
    public FontHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(viewGroup.getContext());
        return new FontHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.font_item_layout, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull final FontHolder fontHolder, int i) {
        fontHolder.tvStylishText.setText(this.dataSet.get(fontHolder.getAdapterPosition()).fontText);

        fontHolder.ivWhatsapp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.putExtra("android.intent.extra.TEXT", fontHolder.tvStylishText.getText());
                intent.setPackage("com.whatsapp");
                intent.setType("text/plain");
                FontAdapter.this.context.startActivity(intent);
            }
        });
        fontHolder.ivCopy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((ClipboardManager) fontHolder.itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("stylish text", fontHolder.tvStylishText.getText()));
                Toast.makeText(fontHolder.itemView.getContext(), "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });
        fontHolder.ivShare.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.setFlags(335544320);
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.TEXT", fontHolder.tvStylishText.getText());
                FontAdapter.this.context.startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return this.dataSet.size();
    }

    public class FontHolder extends RecyclerView.ViewHolder {
        ImageView ivCopy;
        ImageView ivShare;
        ImageView ivWhatsapp;
        TextView tvStylishText;

        public FontHolder(@NonNull View view) {
            super(view);
            this.tvStylishText = view.findViewById(R.id.tv_stlish_text);
            this.ivWhatsapp = view.findViewById(R.id.iv_whatsapp);
            this.ivCopy = view.findViewById(R.id.iv_copy);
            this.ivShare = view.findViewById(R.id.iv_share);
        }
    }
}
