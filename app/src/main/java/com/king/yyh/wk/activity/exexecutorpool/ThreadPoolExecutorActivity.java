package com.king.yyh.wk.activity.exexecutorpool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.king.yyh.wk.R;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yuer on 2018/6/1.
 */

public class ThreadPoolExecutorActivity  extends AppCompatActivity {
    private final int CORE_POOL_SIZE = 4;//核心线程数
    private final int MAX_POOL_SIZE = 5;//最大线程数
    private final long KEEP_ALIVE_TIME = 10;//空闲线程超时时间
    private ThreadPoolExecutor executorPool;
    private int songIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_pool_executor);
        // 创建线程池
        // 创建一个核心线程数为4、最大线程数为5的线程池
        executorPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 点击下载
     *
     * @param view
     */
    public void begin(View view) {
        songIndex++;
        try {
            executorPool.execute(new WorkerThread("歌曲" + songIndex));
        } catch (Exception e) {
            Log.e("threadtest", "AbortPolicy...已超出规定的线程数量，不能再增加了....");
        }

        // 所有任务已经执行完毕，我们在监听一下相关数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20 * 1000);
                } catch (Exception e) {

                }
                Li("monitor after");
            }
        }).start();

    }

    private void Li(String mess) {
        Log.i("threadtest", "monitor " + mess
                + " CorePoolSize:" + executorPool.getCorePoolSize()
                + " PoolSize:" + executorPool.getPoolSize()
                + " MaximumPoolSize:" + executorPool.getMaximumPoolSize()
                + " ActiveCount:" + executorPool.getActiveCount()
                + " TaskCount:" + executorPool.getTaskCount()
        );
    }

    public class WorkerThread implements Runnable {
        private String threadName;

        public WorkerThread(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public synchronized void run() {
            boolean flag = true;
            try {
                while (flag) {
                    String tn = Thread.currentThread().getName();
                    //模拟耗时操作
                    Random random = new Random();
                    long time = (random.nextInt(5) + 1) * 1000;
                    Thread.sleep(time);
                    Log.e("threadtest", "线程\"" + tn + "\"耗时了(" + time / 1000 + "秒)下载了第<" + threadName + ">");
                    //下载完了跳出循环
                    flag = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public String getThreadName() {
            return threadName;
        }
    }
}
