package com.jsqix.utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.lang.Thread.UncaughtExceptionHandler;

public class ErrorHandler implements UncaughtExceptionHandler
{
    /** 是否开启日志输出, 在Debug状态下开启, 在Release状态下关闭以提升程序性能 */
    public static final boolean DEBUG = true;
    //系统默认的UncaughtException处理类   
    private UncaughtExceptionHandler mDefaultHandler;
    /** CrashHandler实例 */
    private static ErrorHandler INSTANCE;
    private static volatile boolean onError = false;
    /** Debug Log Tag */
    public static final String TAG = "CrashHandler";

    /** 获取CrashHandler实例 ,单例模式 */
    public static ErrorHandler getInstance()
    {
        if (ErrorHandler.INSTANCE == null)
        {
            ErrorHandler.INSTANCE = new ErrorHandler();
        }
        return ErrorHandler.INSTANCE;
    }

    /** 程序的Context对象 */
     private Context mContext;

    /** 保证只有一个CrashHandler实例 */
    private ErrorHandler()
    {
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void setToErrorHandler(final Context ctx)
    {
        mContext = ctx;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */

    public void uncaughtException(final Thread thread, final Throwable ex)
    {
        if (!handleException(thread,ex) && mDefaultHandler != null) {  
            //如果用户没有处理则让系统默认的异常处理器来处理  
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {  
            try {  
                Thread.sleep(3000);  
            } catch (InterruptedException e) {  
                Log.e(TAG, "error : ", e);  
            }  
            //退出程序  
            FrameApplication.exitApp(); 
        }  
    }

    private boolean handleException(Thread thread,Throwable ex)
    {
        if (ex == null) {  
            return false;  
        }  
        // 处理异常
        LogWriter.debugError("崩溃简短信息:" + ex.getMessage());
        LogWriter.debugError("崩溃简短信息:" + ex.toString());
        LogWriter.debugError("崩溃线程名称:" + thread.getName() + "崩溃线程ID:" + thread.getId());
        final StackTraceElement[] trace = ex.getStackTrace();
        for (final StackTraceElement element : trace)
        {
            LogWriter.crash("Line " + element.getLineNumber() + " : " + element.toString());
        }
        //使用Toast来显示异常信息  
        new Thread() {  
            @Override  
            public void run() {  
                Looper.prepare();  
                Utils.makeToast(mContext, "很抱歉,程序出现异常,即将退出");
                Looper.loop();  
            }  
        }.start(); 
        
        return true;
    }
}
