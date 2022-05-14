package com.blog.a.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;

public class CommonUtils {

    /** dp转化成px */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /** sp转换成px */
    public static int sp2px(Context context, float spValue) {
        float fontScale=context.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue*fontScale+0.5f);
    }

    /**
     * 缩放到固定尺寸。
     *
     * @param bitmap    待缩放的bitmap
     * @param newWidth  缩放后的宽度
     * @param newHeight 缩放后的高度
     */
    public static Bitmap conversionBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap b = bitmap;
        int width = b.getWidth();
        int height = b.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(b, 0, 0, width, height, matrix, true);
    }

    /**
     * 生成圆角图片。
     *
     * @param bitmap 待转换的bitmap
     * @param radius 圆弧半径
     * @return       圆角bitmap
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float radius) {
        try {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.BLACK);
            canvas.drawRoundRect(rectF, radius, radius, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            final Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return output;
        } catch (OutOfMemoryError outOfMemoryError) {
            outOfMemoryError.printStackTrace();
            return bitmap;
        } catch (Exception e) {
            return bitmap;
        }
    }

    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标
     *
     * @param cirX       圆centerX
     * @param cirY       圆centerY
     * @param radius     圆半径
     * @param cirAngle   当前弧角度
     * @param orginAngle 起点弧角度
     * @return           扇形终射线与圆弧交叉点的xy坐标
     */
    public static PointF calcArcEndPointXY(float cirX, float cirY, float radius, float
            cirAngle, float orginAngle) {
        cirAngle = (orginAngle + cirAngle) % 360;
        return calcArcEndPointXY(cirX, cirY, radius, cirAngle);
    }

    /**
     * 依圆心坐标，半径，扇形角度，计算出扇形终射线与圆弧交叉点的xy坐标。
     *
     * @param cirX     圆centerX
     * @param cirY     圆centerY
     * @param radius   圆半径
     * @param cirAngle 当前弧角度
     * @return         扇形终射线与圆弧交叉点的xy坐标
     */
    public static PointF calcArcEndPointXY(float cirX, float cirY, float radius, float
            cirAngle) {
        float posX;
        float posY;
        //将角度转换为弧度
        float arcAngle = (float) (Math.PI * cirAngle / 180.0);

        if (cirAngle < 90) {
            posX = cirX + (float) (Math.cos(arcAngle)) * radius;
            posY = cirY + (float) (Math.sin(arcAngle)) * radius;
        } else if (cirAngle == 90) {
            posX = cirX;
            posY = cirY + radius;
        } else if (cirAngle > 90 && cirAngle < 180) {
            arcAngle = (float) (Math.PI * (180 - cirAngle) / 180.0);
            posX = cirX - (float) (Math.cos(arcAngle)) * radius;
            posY = cirY + (float) (Math.sin(arcAngle)) * radius;
        } else if (cirAngle == 180) {
            posX = cirX - radius;
            posY = cirY;
        } else if (cirAngle > 180 && cirAngle < 270) {
            arcAngle = (float) (Math.PI * (cirAngle - 180) / 180.0);
            posX = cirX - (float) (Math.cos(arcAngle)) * radius;
            posY = cirY - (float) (Math.sin(arcAngle)) * radius;
        } else if (cirAngle == 270) {
            posX = cirX;
            posY = cirY - radius;
        } else {
            arcAngle = (float) (Math.PI * (360 - cirAngle) / 180.0);
            posX = cirX + (float) (Math.cos(arcAngle)) * radius;
            posY = cirY - (float) (Math.sin(arcAngle)) * radius;
        }
        return new PointF(posX, posY);
    }

    /**
     * 根据刻度获取当前渐变颜色。
     *
     * @param p            当前刻度
     * @param specialColor 每个范围的颜色值
     * @return             当前需要的颜色值
     */
    public static int evaluateColor(int p, int[] specialColor) {
        // 初始值
        int startInt = 0xFFbebebe;
        int endInt = 0xFFbebebe;
        float fraction = 0.5f;

        // 绿 蓝 黄 橙 红
        if(p != 0 && p != 100) {
            startInt = specialColor[p/20];
            endInt = specialColor[p/20+1];
            fraction = (p - (p/20)*20)/20f;
        }

        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24)
                | ((startR + (int) (fraction * (endR - startR))) << 16)
                | ((startG + (int) (fraction * (endG - startG))) << 8)
                | ((startB + (int) (fraction * (endB - startB))));
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getApplicationContext()
                .getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getApplicationContext()
                .getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取获取系统状态栏高度。
     *
     * @param appContext APP的上下文
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context appContext) {
        int result = 0;
        int resourceId = appContext.getResources().getIdentifier
                ("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = appContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
