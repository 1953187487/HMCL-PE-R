package com.tungsten.hmclpe.monitor;

import android.os.Handler;
import android.os.Looper;
import android.view.Choreographer;

public class FpsMonitor implements Choreographer.FrameCallback {

    public interface OnFpsUpdatedListener {
        void onFpsUpdated(int fps);
    }

    private int frameCount = 0;
    private long lastTimestamp = 0;
    private long lastSecond = 0;
    private int currentFps = 0;
    private volatile boolean running = false;
    private final OnFpsUpdatedListener listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public FpsMonitor(OnFpsUpdatedListener listener) {
        this.listener = listener;
    }

    public void start() {
        if (running) return;
        running = true;
        frameCount = 0;
        lastTimestamp = 0;
        lastSecond = 0;
        Choreographer.getInstance().postFrameCallback(this);
    }

    public void stop() {
        running = false;
        Choreographer.getInstance().removeFrameCallback(this);
    }

    public int getCurrentFps() {
        return currentFps;
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (!running) return;

        frameCount++;
        long currentTimeMillis = frameTimeNanos / 1_000_000;

        if (lastTimestamp == 0) {
            lastTimestamp = currentTimeMillis;
        }

        long elapsed = currentTimeMillis - lastSecond;
        if (elapsed >= 1000) {
            currentFps = (int) ((frameCount * 1000.0) / elapsed);
            frameCount = 0;
            lastSecond = currentTimeMillis;

            if (listener != null) {
                final int fps = currentFps;
                mainHandler.post(() -> listener.onFpsUpdated(fps));
            }
        }

        Choreographer.getInstance().postFrameCallback(this);
    }
}
