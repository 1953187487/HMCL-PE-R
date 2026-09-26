package com.tungsten.hmclpe.monitor;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameLogMonitor {

    private static final String TAG = "GameLogMonitor";
    private static final int MAX_LINES = 500;
    private static final long POLL_INTERVAL = 500;

    public interface OnLogUpdatedListener {
        void onLogUpdated(String[] recentLines);
    }

    private String logFilePath;
    private HandlerThread handlerThread;
    private Handler handler;
    private final List<String> logLines = new ArrayList<>();
    private OnLogUpdatedListener listener;
    private final CopyOnWriteArrayList<OnLogUpdatedListener> additionalListeners = new CopyOnWriteArrayList<>();
    private volatile boolean running = false;
    private volatile boolean tailMode = true;

    public GameLogMonitor() {
    }

    public void start(String gameDirectory, OnLogUpdatedListener listener) {
        this.listener = listener;
        this.logFilePath = gameDirectory + "/logs/latest.log";
        handlerThread = new HandlerThread("GameLogMonitor");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        running = true;
        poll();
    }

    public void stop() {
        running = false;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (handlerThread != null) {
            handlerThread.quitSafely();
            handlerThread = null;
        }
    }

    public List<String> getLogLines() {
        synchronized (logLines) {
            return new ArrayList<>(logLines);
        }
    }

    public void clearLog() {
        synchronized (logLines) {
            logLines.clear();
        }
    }

    public void addListener(OnLogUpdatedListener l) {
        additionalListeners.add(l);
    }

    public void removeListener(OnLogUpdatedListener l) {
        additionalListeners.remove(l);
    }

    private void poll() {
        if (!running) return;

        try {
            File logFile = new File(logFilePath);
            if (logFile.exists() && logFile.isFile()) {
                RandomAccessFile raf = new RandomAccessFile(logFile, "r");
                try {
                    long fileSize = raf.length();
                    if (fileSize > 0) {
                        raf.seek(0);
                        String line;
                        int count = 0;
                        while ((line = raf.readLine()) != null && count < MAX_LINES) {
                            synchronized (logLines) {
                                if (logLines.isEmpty() || !line.equals(logLines.get(logLines.size() - 1))) {
                                    logLines.add(line);
                                    if (logLines.size() > MAX_LINES) {
                                        logLines.remove(0);
                                    }
                                }
                            }
                            count++;
                        }
                    }
                } finally {
                    raf.close();
                }
            }

            if (listener != null) {
                String[] lines;
                synchronized (logLines) {
                    lines = logLines.toArray(new String[0]);
                }
                listener.onLogUpdated(lines);
                for (OnLogUpdatedListener l : additionalListeners) {
                    l.onLogUpdated(lines);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error polling log", e);
        }

        if (running) {
            handler.postDelayed(this::poll, POLL_INTERVAL);
        }
    }
}
