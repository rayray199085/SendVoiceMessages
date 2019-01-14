package com.project.stephencao.sendvoicemessages.util;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MyAudioManager {

    private MediaRecorder mRecorder;
    private String mDir;
    private AudioStateListener mListener;
    private String mCurrentFilePath;
    private String mFileName;
    private String mDirectory;

    private static MyAudioManager mInstance;

    private boolean mHasPrepared;

    public MyAudioManager(String directory) {
        mDirectory = directory;
    }

    ;


    public interface AudioStateListener {
        void hasPreparedWell();
    }
    public void setAudioStateListener(AudioStateListener listener) {
        this.mListener = listener;
    }

    public static MyAudioManager getInstance(String directoryPath) {
        if (mInstance == null) {
            synchronized (MyDialogManager.class) {
                if (mInstance == null) {
                    mInstance = new MyAudioManager(directoryPath);
                }
            }
        }
        return mInstance;
    }

    public void prepareAudio() {
        try {
            mHasPrepared = false;
            File directory = new File(mDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            mFileName = generateFileName();
            File file = new File(directory, mFileName);
            mCurrentFilePath = file.getAbsolutePath();
            mRecorder = new MediaRecorder();
            mRecorder.setOutputFile(mCurrentFilePath);
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
            mRecorder.start();
            mHasPrepared = true;
            if (mListener != null) {
                mListener.hasPreparedWell();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public int getVoiceLevel(int maxLevel) {
        if (mHasPrepared) {
            //获得最大的振幅getMaxAmplitude() 1-32767
            try {
                return maxLevel * mRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e) {

            }
        }
        return 1;
    }

    public void release() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void cancel() {
        release();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }
}
