/*
 * Copyright 2019 ExtendedUI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.app;

import android.animation.TimeAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.animation.ObjectAnimator;
import android.animation.TimeAnimator;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.internal.R;

public class ExUIEasterEgg extends Activity {

    ImageView mIcon;
    ImageView mBackground;
    Animation zoomOut;
    Animation zoomIn;
    BackslashDrawable mBackslash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setNavigationBarColor(0);
        getWindow().setStatusBarColor(0);
        getActionBar().hide();

        setContentView(R.layout.exui_easteregg);

        mBackground = findViewById(R.id.easter_egg_background);
        mIcon = findViewById(R.id.icon);

        final float dp = getResources().getDisplayMetrics().density;
        mBackslash = new BackslashDrawable((int) (50 * dp));

        mBackground.setBackground(mBackslash);
        mBackground.getBackground().setAlpha(0x20);

        zoomOut = AnimationUtils.loadAnimation(this, R.anim.zoomout_easteregg);
        zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoomin_easteregg);

        zoomOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }
            public void onAnimationStart(Animation animation) {
            }
            public void onAnimationEnd(Animation animation) {
                mIcon.startAnimation(zoomIn);
            }
        });

        mIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mBackslash.startAnimating();
                mIcon.startAnimation(zoomOut);
            }
        });
    }


    private static class BackslashDrawable extends Drawable implements TimeAnimator.TimeListener {
        Bitmap mTile;
        Paint mPaint = new Paint();
        BitmapShader mShader;
        TimeAnimator mAnimator = new TimeAnimator();
        Matrix mMatrix = new Matrix();

        public void draw(Canvas canvas) {
            canvas.drawPaint(mPaint);
        }

        BackslashDrawable(int width) {
            mTile = Bitmap.createBitmap(width, width, Bitmap.Config.ALPHA_8);
            mAnimator.setTimeListener(this);

            final Canvas tileCanvas = new Canvas(mTile);
            final float w = tileCanvas.getWidth();
            final float h = tileCanvas.getHeight();

            final Path path = new Path();
            path.moveTo(0, 0);
            path.lineTo(w / 2, 0);
            path.lineTo(w, h / 2);
            path.lineTo(w, h);
            path.close();

            path.moveTo(0, h / 2);
            path.lineTo(w / 2, h);
            path.lineTo(0, h);
            path.close();

            final Paint slashPaint = new Paint();
            slashPaint.setAntiAlias(true);
            slashPaint.setStyle(Paint.Style.FILL);
            slashPaint.setColor(0xFF000000);
            tileCanvas.drawPath(path, slashPaint);

            //mPaint.setColor(0xFF0000FF);
            mShader = new BitmapShader(mTile, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            mPaint.setShader(mShader);
        }

        public void startAnimating() {
            if (!mAnimator.isStarted()) {
                mAnimator.start();
            }
        }

        public void stopAnimating() {
            if (mAnimator.isStarted()) {
                mAnimator.cancel();
            }
        }

        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            mPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
            if (mShader != null) {
                mMatrix.postTranslate(deltaTime / 4f, 0);
                mShader.setLocalMatrix(mMatrix);
                invalidateSelf();
            }
        }
    }
}
