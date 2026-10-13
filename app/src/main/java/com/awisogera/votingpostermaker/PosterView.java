package com.awisogera.votingpostermaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

public class PosterView extends View {
    private Bitmap imageA, imageB;
    private ArrayList<TextItem> texts = new ArrayList<>();
    private Paint textPaint = new Paint();
    private float ax = 50, ay = 50, bx = 450, by = 50;
    private float scaleA = 1f, scaleB = 1f;
    private int activePhoto = 0, activeText = -1;
    private float lastX, lastY, startDistance, startScale;

    public PosterView(Context context) { super(context); }

    public void setImageA(Uri uri) { imageA = loadBitmap(uri); invalidate(); }
    public void setImageB(Uri uri) { imageB = loadBitmap(uri); invalidate(); }
    public void addText(String value) { texts.add(new TextItem(value)); activeText = texts.size() - 1; invalidate(); }
    public void deleteActiveText() {
        if (activeText >= 0 && activeText < texts.size()) {
            texts.remove(activeText);
            activeText = texts.isEmpty() ? -1 : texts.size() - 1;
            invalidate();
        }
    }

    private Bitmap loadBitmap(Uri uri) {
        try {
            return BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(uri));
        } catch (Exception e) { return null; }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                lastX = x; lastY = y;
                if (event.getPointerCount() == 2) {
                    float dx = event.getX(1) - event.getX(0);
                    float dy = event.getY(1) - event.getY(0);
                    startDistance = (float) Math.sqrt(dx * dx + dy * dy);
                    startScale = (activePhoto == 1) ? scaleA : scaleB;
                } else {
                    activeText = -1;
                    for (int i = texts.size() - 1; i >= 0; i--) {
                        TextItem item = texts.get(i);
                        if (new RectF(item.x, item.y - 60, item.x + 300, item.y + 20).contains(x, y)) {
                            activeText = i; break;
                        }
                    }
                    if (activeText == -1) {
                        if (imageA != null && new RectF(ax, ay, ax + 400 * scaleA, ay + 400 * scaleA).contains(x, y)) activePhoto = 1;
                        else if (imageB != null && new RectF(bx, by, bx + 400 * scaleB, by + 400 * scaleB).contains(x, y)) activePhoto = 2;
                        else activePhoto = 0;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2 && startDistance > 0) {
                    float dx = event.getX(1) - event.getX(0);
                    float dy = event.getY(1) - event.getY(0);
                    float factor = (float) Math.sqrt(dx * dx + dy * dy) / startDistance;
                    if (activePhoto == 1) scaleA = Math.max(0.5f, Math.min(startScale * factor, 3f));
                    else if (activePhoto == 2) scaleB = Math.max(0.5f, Math.min(startScale * factor, 3f));
                    invalidate();
                } else if (event.getPointerCount() == 1) {
                    float dx = x - lastX, dy = y - lastY;
                    if (activeText >= 0 && activeText < texts.size()) {
                        texts.get(activeText).x += dx; texts.get(activeText).y += dy;
                    } else if (activePhoto == 1) { ax += dx; ay += dy; }
                    else if (activePhoto == 2) { bx += dx; by += dy; }
                    lastX = x; lastY = y; invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                activePhoto = 0; break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.rgb(245, 245, 245));
        if (imageA != null) canvas.drawBitmap(imageA, null, new RectF(ax, ay, ax + 400 * scaleA, ay + 400 * scaleA), null);
        if (imageB != null) canvas.drawBitmap(imageB, null, new RectF(bx, by, bx + 400 * scaleB, by + 400 * scaleB), null);
        for (TextItem item : texts) {
            textPaint.setTextSize(item.size);
            textPaint.setColor(item.color);
            textPaint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            if (item.shadow) textPaint.setShadowLayer(8, 4, 4, 0xff000000);
            else textPaint.clearShadowLayer();
            textPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(item.text, item.x, item.y, textPaint);
        }
    }
}
