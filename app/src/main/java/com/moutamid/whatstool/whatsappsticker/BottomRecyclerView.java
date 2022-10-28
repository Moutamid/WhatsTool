package com.moutamid.whatstool.whatsappsticker;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class BottomRecyclerView extends RecyclerView {

    @Override
    public float getTopFadingEdgeStrength() {
        return 0.0f;
    }

    public BottomRecyclerView(Context context) {
        super(context);
    }

    public BottomRecyclerView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public BottomRecyclerView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }
}
