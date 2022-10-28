package com.moutamid.whatstool.whatsappsticker;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.whatstool.R;


public class StickerPackListItemViewHolder extends RecyclerView.ViewHolder {
    ImageView addButton;
    View container;
    TextView filesizeView;
    LinearLayout imageRowView;
    TextView publisherView;
    TextView titleView;

    StickerPackListItemViewHolder(View view) {
        super(view);
        this.container = view;
        this.titleView = (TextView) view.findViewById(R.id.sticker_pack_title);
        this.publisherView = (TextView) view.findViewById(R.id.sticker_pack_publisher);
        this.filesizeView = (TextView) view.findViewById(R.id.sticker_pack_filesize);
        this.addButton = (ImageView) view.findViewById(R.id.add_button_on_list);
        this.imageRowView = (LinearLayout) view.findViewById(R.id.sticker_packs_list_item_image_list);
    }
}
