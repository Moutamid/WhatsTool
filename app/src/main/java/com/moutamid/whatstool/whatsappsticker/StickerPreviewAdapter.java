package com.moutamid.whatstool.whatsappsticker;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.moutamid.whatstool.R;

public class StickerPreviewAdapter extends RecyclerView.Adapter<StickerPreviewViewHolder> {
    private int cellLimit = 0;
    private int cellPadding;
    private final int cellSize;
    private final int errorResource;
    private final LayoutInflater layoutInflater;
    @NonNull
    private StickerPack stickerPack;

    StickerPreviewAdapter(@NonNull LayoutInflater layoutInflater2, int i, int i2, int i3, @NonNull StickerPack stickerPack2) {
        this.cellSize = i2;
        this.cellPadding = i3;
        this.layoutInflater = layoutInflater2;
        this.errorResource = i;
        this.stickerPack = stickerPack2;
    }

    @NonNull
    public StickerPreviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        StickerPreviewViewHolder stickerPreviewViewHolder = new StickerPreviewViewHolder(this.layoutInflater.inflate(R.layout.sticker_image, viewGroup, false));
        ViewGroup.LayoutParams layoutParams = stickerPreviewViewHolder.stickerPreviewView.getLayoutParams();
        int i2 = this.cellSize;
        layoutParams.height = i2;
        layoutParams.width = i2;
        stickerPreviewViewHolder.stickerPreviewView.setLayoutParams(layoutParams);
        SimpleDraweeView simpleDraweeView = stickerPreviewViewHolder.stickerPreviewView;
        int i3 = this.cellPadding;
        simpleDraweeView.setPadding(i3, i3, i3, i3);
        return stickerPreviewViewHolder;
    }

    public void onBindViewHolder(@NonNull StickerPreviewViewHolder stickerPreviewViewHolder, int i) {
        stickerPreviewViewHolder.stickerPreviewView.setImageResource(this.errorResource);
        stickerPreviewViewHolder.stickerPreviewView.setImageURI(StickerPackLoader.getStickerAssetUri(this.stickerPack.identifier, this.stickerPack.getStickers().get(i).imageFileName));
    }

    public int getItemCount() {
        int size = this.stickerPack.getStickers().size();
        int i = this.cellLimit;
        return i > 0 ? Math.min(size, i) : size;
    }
}
