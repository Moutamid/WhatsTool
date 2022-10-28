package com.moutamid.whatstool.whatsappsticker;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.moutamid.whatstool.R;

import java.util.List;

public class StickerPackListAdapter extends RecyclerView.Adapter<StickerPackListItemViewHolder> {
    private int maxNumberOfStickersInARow;
    @NonNull
    private final OnAddButtonClickedListener onAddButtonClickedListener;
    @NonNull
    private List<StickerPack> stickerPacks;

    public interface OnAddButtonClickedListener {
        void onAddButtonClicked(StickerPack stickerPack);
    }

    StickerPackListAdapter(@NonNull List<StickerPack> list, @NonNull OnAddButtonClickedListener onAddButtonClickedListener2) {
        this.stickerPacks = list;
        this.onAddButtonClickedListener = onAddButtonClickedListener2;
    }

    @NonNull
    public StickerPackListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new StickerPackListItemViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sticker_packs_list_item, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull StickerPackListItemViewHolder stickerPackListItemViewHolder, final int i) {
        final StickerPack stickerPack = this.stickerPacks.get(i);
        Context context = stickerPackListItemViewHolder.publisherView.getContext();
        stickerPackListItemViewHolder.publisherView.setText(stickerPack.publisher);
        stickerPackListItemViewHolder.filesizeView.setText(Formatter.formatShortFileSize(context, stickerPack.getTotalSize()));
        stickerPackListItemViewHolder.titleView.setText(stickerPack.name);
        stickerPackListItemViewHolder.container.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                StickerPackListAdapter.lambda$onBindViewHolder$0(stickerPacks.get(i), view);
            }
        });
        stickerPackListItemViewHolder.imageRowView.removeAllViews();
        int min = Math.min(this.maxNumberOfStickersInARow, stickerPack.getStickers().size());
        for (int i2 = 0; i2 < min; i2++) {
            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) LayoutInflater.from(context).inflate(R.layout.sticker_pack_list_item_image, stickerPackListItemViewHolder.imageRowView, false);
            simpleDraweeView.setImageURI(StickerPackLoader.getStickerAssetUri(stickerPack.identifier, stickerPack.getStickers().get(i2).imageFileName));
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) simpleDraweeView.getLayoutParams();
            int measuredWidth = (((stickerPackListItemViewHolder.imageRowView.getMeasuredWidth() - (this.maxNumberOfStickersInARow * stickerPackListItemViewHolder.imageRowView.getContext().getResources().getDimensionPixelSize(R.dimen.sticker_pack_list_item_preview_image_size))) / (this.maxNumberOfStickersInARow - 1)) - layoutParams.leftMargin) - layoutParams.rightMargin;
            if (i2 != min - 1 && measuredWidth > 0) {
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin + measuredWidth, layoutParams.bottomMargin);
                simpleDraweeView.setLayoutParams(layoutParams);
            }
            stickerPackListItemViewHolder.imageRowView.addView(simpleDraweeView);
        }
        setAddButtonAppearance(stickerPackListItemViewHolder.addButton, stickerPack);
    }

    static void lambda$onBindViewHolder$0(StickerPack stickerPack, View view) {
        Intent intent = new Intent(view.getContext(), StickerPackDetailsActivity.class);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_SHOW_UP_BUTTON, true);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_DATA, stickerPack);
        view.getContext().startActivity(intent);
    }

    private void setAddButtonAppearance(ImageView imageView, final StickerPack stickerPack) {
        if (stickerPack.getIsWhitelisted()) {
            imageView.setImageResource(R.drawable.sticker_3rdparty_added);
            imageView.setClickable(false);
            imageView.setOnClickListener((View.OnClickListener) null);
            setBackground(imageView, (Drawable) null);
            return;
        }
        imageView.setImageResource(R.drawable.sticker_3rdparty_add);
        imageView.setOnClickListener(new View.OnClickListener() {

            public final void onClick(View view) {
                StickerPackListAdapter.this.lambda$setAddButtonAppearance$1$StickerPackListAdapter(stickerPack, view);
            }
        });
        TypedValue typedValue = new TypedValue();
        imageView.getContext().getTheme().resolveAttribute(16843534, typedValue, true);
        imageView.setBackgroundResource(typedValue.resourceId);
    }

    public void lambda$setAddButtonAppearance$1$StickerPackListAdapter(StickerPack stickerPack, View view) {
        this.onAddButtonClickedListener.onAddButtonClicked(stickerPack);
    }

    private void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public int getItemCount() {
        return this.stickerPacks.size();
    }


    public void setMaxNumberOfStickersInARow(int i) {
        if (this.maxNumberOfStickersInARow != i) {
            this.maxNumberOfStickersInARow = i;
            notifyDataSetChanged();
        }
    }

    public void setStickerPackList(List<StickerPack> list) {
        this.stickerPacks = list;
    }
}
