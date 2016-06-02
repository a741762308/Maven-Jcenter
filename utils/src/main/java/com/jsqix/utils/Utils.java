package com.jsqix.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils
{
    public static String getMacAdress(Context context)
    {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    public static void makeToast(final Context context, final String text)
    {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void makeToast(final Context context, final String text, final int time)
    {
        Toast.makeText(context, text, time).show();
    }

    public static boolean strIsNull(final String text)
    {
        return !strNotNull(text);
    }

    // ----------------------------------------------------------------------------------------------------------------------------
    // MD5相关函数
    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    // ----------------------------------------------------------------------------------------------------------------------------

    /**
     * 字符串版本比较函数
     * 
     * @param v1
     * @param v2
     * @return 如果v1大于v2，返回true，否则返回false
     */
    public static boolean compareVersion(final String v1, final String v2)
    {
        if ((v1 != null) && (v2 != null))
        {
            final StringBuffer sb1 = new StringBuffer(v1);
            final StringBuffer sb2 = new StringBuffer(v2);
            while ((sb1.length() > 0) && (sb2.length() > 0))
            {
                final String ver1 = Utils.getVerString(sb1);
                final String ver2 = Utils.getVerString(sb2);
                int version1 = 0;
                int version2 = 0;
                try
                {
                    version1 = Integer.parseInt(ver1);
                    version2 = Integer.parseInt(ver2);
                    if (version1 == version2)
                    {
                        continue;
                    }
                    else if (version1 > version2)
                    {
                        return true;
                    }
                    else if (version1 < version2)
                    {
                        return false;
                    }
                }
                catch (final NumberFormatException e)
                {
                    return false;
                }
            }
        }
        return false;
    }

    // ----------------------------------------------------------------------------------------------------------------------------

    /**
     * 根据资源创建对话框
     * 
     * @param a
     * @param title
     * @param msg
     * @return
     */
    public static AlertDialog createDlg(final BaseActivity a, final int title, final int msg)
    {
        final AlertDialog dialog = new Builder(a).create();
        dialog.setTitle(a.getString(title));
        dialog.setMessage(a.getString(msg));
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public static AlertDialog createDlg(final BaseActivity a, final String title, final String msg)
    {
        final Builder builder = new Builder(a);
        builder.setTitle(title);
        builder.setMessage(msg);
        final AlertDialog dlg = builder.create();
        dlg.setCanceledOnTouchOutside(false);
        return dlg;
    }

    // ----------------------------------------------------------------------------------------------------------------------------

    // ----------------------------------------------------------------------------------------------------------------------------
    // -------------------------------------------------------------------------------------------------------------------

    /**
     * 添加倒影，原理，先翻转图片，由上到下放大透明度
     * 
     * @param originalImage
     * @return
     */
    public static Bitmap createReflectedImage(final Bitmap originalImage)
    {
        // The gap we want between the reflection and the original image
        final int reflectionGap = 4;

        final int width = originalImage.getWidth();
        final int height = originalImage.getHeight();

        // This will not scale but will flip on the Y axis
        final Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // Create a Bitmap with the flip matrix applied to it.
        // We only want the bottom half of the image
        final Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 4, width, height / 4, matrix,
                false);

        // Create a new bitmap with same width but taller to fit reflection
        final Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + (height / 4)), Config.ARGB_8888);

        // Create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        final Canvas canvas = new Canvas(bitmapWithReflection);
        // Draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // Draw in the gap
        final Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        // Draw in the reflection
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        // Create a shader that is a linear gradient that covers the reflection
        final Paint paint = new Paint();
        final LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        // Set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

        return bitmapWithReflection;
    }

    // -------------------------------------------------------------------------------------------------------------------

    /**
     * 参数解码
     * 
     * @param s
     * @return
     */
    public static String decode(final String s)
    {
        if (s == null)
        {
            return "";
        }
        try
        {
            return URLDecoder.decode(s, "UTF-8");
        }
        catch (final UnsupportedEncodingException e)
        {
            LogWriter.debugError("Decode error :" + e.toString());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    /**
     * dip转像素,不同分辨率适配
     * 
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(final Context context, final float dipValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * scale) + 0.5f);
    }

    // -------------------------------------------------------------------------------------------------------------------
    // drawable 类型转化为bitmap
    public static Bitmap drawableToBitmap(final Drawable drawable)
    {
        final Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // ----------------------------------------------------------------------------------------------------------------------------

    /**
     * 参数编码
     */
    public static String encode(final String s)
    {
        if (s == null)
        {
            return "";
        }
        try
        {
            return URLEncoder.encode(s, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~")
                    .replace("#", "%23");
        }
        catch (final UnsupportedEncodingException e)
        {
            LogWriter.debugError("Encode error :" + e.toString());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static String getCachePath()
    {
        if (Utils.sdcardMounted())
        {
            return Environment.getExternalStorageDirectory().getPath() + "/Android/data/"
                    + FrameApplication.getInstance().getPackageName() + "/cache/";
        }
        else
        {
            return FrameApplication.getInstance().getFilesDir().getPath() + "data/"
                    + FrameApplication.getInstance().getPackageName() + "/cache/";
        }
    }

    // ----------------------------------------------------------------------------------------------------------------------------

    // ----------------------------------------------------------------------------------------------------------------------------------

    /**
     * 获取屏幕分辨率
     * 
     * @param context
     * @return
     */
    public static Display getDisplay(final Activity context)
    {
        return context.getWindowManager().getDefaultDisplay();
    }

    public static DisplayMetrics getDisplayMetrics(final BaseActivity c)
    {
        final DisplayMetrics dm = new DisplayMetrics();
        c.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    // ----------------------------------------------------------------------------------------------------------------------------

    // -------------------------------------------------------------------------------------------------------------------
    public static String getJsonFromAssets(final Activity context, final String assetsName)
    {
        String text = null;
        InputStream is = null;
        try
        {
            is = context.getAssets().open(assetsName);
            final int size = is.available();
            final byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
        }
        catch (final IOException e)
        {
            LogWriter.d("Can not load file " + assetsName);
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (final IOException e)
                {
                    e.printStackTrace();
                }
                is = null;
            }
        }
        return text;
    }

    /**
     * MD5运算
     * 
     * @param s
     * @return String 返回密文
     */
    public static String getMd5(final String s)
    {
        try
        {
            // Create MD5 Hash
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.trim().getBytes());
            final byte messageDigest[] = digest.digest();
            return Utils.toHexString(messageDigest);
        }
        catch (final NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return s;
    }

    // ----------------------------------------------------------------------------------------------------------------------------

    /**
     * 返回网络连接
     * 
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(final Context context)
    {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netinfo = cm.getActiveNetworkInfo();
        return netinfo;
    }

    public static boolean isNetworkAvailable()
    {
        ConnectivityManager cm = (ConnectivityManager) FrameApplication.getInstance().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (cm == null)
        {
        }
        else
        {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i < info.length; i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取后缀
     */
    public static String getPostfix(final String str)
    {
        return Utils.getPrefix(str, ".");
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    /**
     * 获取后缀
     */
    public static String getPostfix(final String str, final String c)
    {
        String prefix = null;
        if (str != null)
        {
            final int pos = str.indexOf(c);
            if (pos != -1)
            {
                prefix = str.substring(pos + c.length(), str.length());
            }
        }
        return prefix;
    }

    /**
     * 获取前缀
     */
    public static String getPrefix(final String str)
    {
        return Utils.getPrefix(str, ".");
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    /**
     * 获取前缀
     */
    public static String getPrefix(final String str, final String c)
    {
        String prefix = null;
        if (str != null)
        {
            final int pos = str.indexOf(c);
            if (pos != -1)
            {
                prefix = str.substring(0, pos);
            }
        }
        return prefix;
    }

    // -------------------------------------------------------------------------------------------------------------------
    public static Bitmap getRoundedCornerBitmap(final Bitmap bitmap)
    {
        return Utils.getRoundedCornerBitmap(bitmap, 8.0f);
    }

    /**
     * 生成圆角图片
     */
    public static Bitmap getRoundedCornerBitmap(final Bitmap bitmap, final float roundPx)
    {
        try
        {
            final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            final Canvas canvas = new Canvas(output);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }
        catch (final Exception e)
        {
            return bitmap;
        }
    }

    // ----------------------------------------------------------------------------------------------------------------------------
    /**
     * 获取SD卡剩余空间的大小
     * 
     * @return long SD卡剩余空间的大小（单位：byte）
     */
    // public static long getSDSize()
    // {
    // final String str = Environment.getExternalStorageDirectory().getPath();
    // final StatFs localStatFs = new StatFs(str);
    // final long blockSize = localStatFs.getBlockSize();
    // final long availCount = localStatFs.getAvailableBlocks();
    // return availCount * blockSize;
    // }

    // ----------------------------------------------------------------------------------------------------------------------------

    /**
     * 获取渠道号
     * 
     * @param activity
     * @param defaultID
     * @return
     */
    public static String getSourceID(final Activity activity, final String defaultID)
    {
        String text = null;
        InputStream is = null;
        try
        {
            is = activity.getAssets().open("source");
            final int size = is.available();
            final byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            text = new String(buffer);
        }
        catch (final IOException e)
        {
            // 渠道文件不存在，返回默认的sourceid
            text = defaultID;
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (final IOException e)
                {
                    e.printStackTrace();
                }
                is = null;
            }
        }
        return Utils.replaceBlank(text);
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    /**
     * 获取状态栏高度（其他方法不行）
     * 
     * @param ctx
     * @return
     */
    public static int getStatusBarHeight(final Context ctx)
    {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;

        try
        {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = ctx.getResources().getDimensionPixelSize(x);
        }
        catch (final Exception e1)
        {
            Log.d("ERROR", "get status bar height fail");
            e1.printStackTrace();
        }

        return sbar;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    /**
     * 返回manifest文件里的版本号（versionCode）
     * 
     * @param context
     * @return
     */
    public static int getVersionCode(final Context context)
    {
        int vCode = 0;
        final PackageManager packageManager = context.getPackageManager();
        try
        {
            final PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            vCode = packageInfo.versionCode;
        }
        catch (final NameNotFoundException e)
        {
            LogWriter.debugError(e.getMessage());
        }
        return vCode;
    }

    // ----------------------------------------------------------------------------------------------------------------------------

    /**
     * 返回manifest文件里的版本名（versionName）
     * 
     * @param context
     * @return
     */
    public static String getVersionName(final Context context)
    {
        String version = "0.0";
        final PackageManager packageManager = context.getPackageManager();
        try
        {
            final PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        }
        catch (final NameNotFoundException e)
        {
            LogWriter.debugError(e.getMessage());
        }
        return version;
    }

    public static String getVerString(final StringBuffer str)
    {
        String ver = null;
        if (str == null)
        {
            return null;
        }

        final int pos = str.indexOf(".");
        if (pos == -1)
        {
            ver = str.toString();
            str.delete(0, str.length());
            return ver;
        }
        ver = str.substring(0, pos);
        str.delete(0, pos + 1);
        return ver;
    }



    // -------------------------------------------------------------------------------------------------------------------

    /**
     * 安装apk
     * 
     * @param apkFile
     */
    public static void installApk(final Context context, final File apkFile)
    {
        try
        {
            final String command = "chmod 777 " + apkFile.getAbsolutePath();
            final Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        final Uri uri = Uri.fromFile(apkFile);
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // -------------------------------------------------------------------------------------------------------------------
    public static char IntToHex(final int n)
    {
        if (n <= 9)
        {
            return (char) (n + 0x30);
        }
        return (char) ((n - 10) + 0x61);
    }

    public static boolean isInRect(final int x, final int y, final Rect r)
    {
        return ((x > r.left) && (x < (r.left + r.width())) && (y > r.top) && (y < (r.top + r.height())));
    }

    // -------------------------------------------------------------------------------------------------------------------
    public static boolean IsSafe(final char ch)
    {
        if ((((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))) || ((ch >= '0') && (ch <= '9')))
        {
            return true;
        }
        switch (ch)
        {
            case '\'':
            case '(':
            case ')':
            case '*':
            case '-':
            case '.':
            case '_':
            case '!':
                return true;
        }
        return false;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(final Context context, final float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((pxValue / scale) + 0.5f);
    }

    /**
     * 像素转字体大小sp
     * 
     * @param context
     * @param pxValue
     *            （单位：像素）
     * @return
     */
    public static float px2sp(final Context context, final float pxValue)
    {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (pxValue / fontScale) + 0.5f;
    }

    public static String replaceBlank(final String str)
    {
        String dest = "";
        if (str != null)
        {
            final Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            final Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------
    public static float round(final float aNum, final int aScale)
    {
        final String s = String.valueOf(aNum);
        final BigDecimal b = new BigDecimal(s);
        return b.setScale(aScale, BigDecimal.ROUND_HALF_EVEN).floatValue();
    }

    // -------------------------------------------------------------------------------------------------------------------

    /**
     * 检查是否安装了sd卡
     * 
     * @return false 未安装
     */
    public static boolean sdcardMounted()
    {
        final String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED) && !state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    /**
     * 字体大小sp转像素
     * 
     * @param context
     * @param spValue
     *            字体大小（单位：sp）
     * @return
     */
    public static float sp2px(final Context context, final float spValue)
    {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale) + 0.5f;
    }

    public static boolean strNotNull(final String str)
    {
        return (str != null) && !"".equals(str);
    }

    /**
     * 转换为十六进制字符串
     * 
     * @param b
     *            byte数组
     * @return String byte数组处理后字符串
     */
    public static String toHexString(final byte[] b)
    {// String to byte
        final StringBuilder sb = new StringBuilder(b.length * 2);
        for (final byte element : b)
        {
            sb.append(Utils.HEX_DIGITS[(element & 0xf0) >>> 4]);
            sb.append(Utils.HEX_DIGITS[element & 0x0f]);
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------------------------------------------------
    public static String UrlEncodeUnicode(final String s)
    {
        if (s == null)
        {
            return null;
        }
        final int length = s.length();
        final StringBuilder builder = new StringBuilder(length); // buffer
        for (int i = 0; i < length; i++)
        {
            final char ch = s.charAt(i);
            if ((ch & 0xff80) == 0)
            {
                if (Utils.IsSafe(ch))
                {
                    builder.append(ch);
                }
                else if (ch == ' ')
                {
                    builder.append('+');
                }
                else
                {
                    builder.append('%');
                    builder.append(Utils.IntToHex((ch >> 4) & 15));
                    builder.append(Utils.IntToHex(ch & 15));
                }
            }
            else
            {
                builder.append("%u");
                builder.append(Utils.IntToHex((ch >> 12) & 15));
                builder.append(Utils.IntToHex((ch >> 8) & 15));
                builder.append(Utils.IntToHex((ch >> 4) & 15));
                builder.append(Utils.IntToHex(ch & 15));
            }
        }
        return builder.toString();
    }

    // -------------------------------------------------------------------------------------------------------------------

    public static String GetURLParams(final String url, String... urlParam)
    {
        if (urlParam != null && urlParam.length > 0)
        {
            final StringBuffer sBuffer = new StringBuffer();
            final StringBuffer urlBuffer = new StringBuffer(url);
            for (int i = 0; i < urlParam.length; i++)
            {
                sBuffer.append("{" + i + "}");
                final int start = urlBuffer.indexOf(sBuffer.toString());
                if (start == -1)
                {
                    sBuffer.delete(0, sBuffer.toString().length());
                    continue;
                }
                final int len = sBuffer.length();
                final String s = urlParam[i];
                String encode = null;
                try
                {
                    encode = URLEncoder.encode(s, "utf-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                if (encode != null)
                {
                    urlBuffer.replace(start, start + len, encode);
                    sBuffer.delete(0, sBuffer.toString().length());
                }
            }
            LogWriter.debugInfo("URL Param 处理之后:" + urlBuffer.toString());
            return urlBuffer.toString();
        }
        return null;
    }

    // -------------------------------------------------------------------------------------------------------------------
    public static String GetURLParams(final String url, final List<String> urlParam)
    {
        if (urlParam != null && urlParam.size() > 0)
        {
            // 处理url参数
            if (urlParam.size() > 0)
            {
                final StringBuffer sBuffer = new StringBuffer();
                final StringBuffer urlBuffer = new StringBuffer(url);
                for (int i = 0; i < urlParam.size(); i++)
                {
                    sBuffer.append("{" + i + "}");
                    final int start = urlBuffer.indexOf(sBuffer.toString());
                    if (start == -1)
                    {
                        sBuffer.delete(0, sBuffer.toString().length());
                        continue;
                    }
                    final int len = sBuffer.length();
                    final String s = urlParam.get(i).toString();
                    urlBuffer.replace(start, start + len, s);
                    sBuffer.delete(0, sBuffer.toString().length());
                }
                LogWriter.debugInfo("URL Param 处理之后:" + urlBuffer.toString());
                return urlBuffer.toString();
            }
        }
        return null;
    }

}
