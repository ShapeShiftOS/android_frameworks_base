package com.android.systemui.screenshot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.graphics.Path;
import android.graphics.RectF;
import com.android.systemui.R;

public class RoundedImageView extends ImageView {

    private Context mContext;
    public static float radius;

    public RoundedImageView(Context context) {
        super(context);
        mContext = context;
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        radius = mContext.getResources().getDimensionPixelSize(R.dimen.screenshot_ui_imageview_corner_radius);
        Path clipPath = new Path();
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}