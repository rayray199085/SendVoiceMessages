package com.project.stephencao.sendvoicemessages.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import com.project.stephencao.sendvoicemessages.R;
import com.project.stephencao.sendvoicemessages.util.MyAudioManager;
import com.project.stephencao.sendvoicemessages.util.MyDialogManager;

public class AudioRecorderButton extends android.support.v7.widget.AppCompatButton implements MyAudioManager.AudioStateListener {
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    private static final int MSG_AUDIO_HAS_PREPARED = 101;
    private static final int MSG_VOLUME_UPDATED = 102;
    private static final int MSG_DIALOG_DISMISS = 103;
    private static final String DIRECTORY_NAME = "recorderFiles";
    private int mVerticalCancelDistance;
    private boolean mIsRecording = false;
    private float mTimer;
    private boolean mDoesLongClickActivate = false;
    private MyDialogManager mMyDialogManager;
    private MyAudioManager mMyAudioManager;
    private int mCurrentState = STATE_NORMAL;
    private OnAudioFinishListener mListener;

    public void setOnAudioFinishListener(OnAudioFinishListener onAudioFinishListener) {
        this.mListener = onAudioFinishListener;
    }

    public interface OnAudioFinishListener {
        void doFinish(float duration, String filePath);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_HAS_PREPARED: {
                    mMyDialogManager.displayRecordingDialog();
                    mIsRecording = true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (mIsRecording) {
                                try {
                                    Thread.sleep(100);
                                    mTimer += 0.1f;
                                    mHandler.sendEmptyMessage(MSG_VOLUME_UPDATED);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    break;
                }
                case MSG_VOLUME_UPDATED: {
                    mMyDialogManager.updateVolumeLevel(mMyAudioManager.getVoiceLevel(7));
                    break;
                }
                case MSG_DIALOG_DISMISS: {
                    mMyDialogManager.dismissDialog();
                    break;
                }
            }
        }
    };

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        String directory = Environment.getExternalStorageDirectory() + "/" + DIRECTORY_NAME;
        mMyAudioManager = MyAudioManager.getInstance(directory);
        mMyAudioManager.setAudioStateListener(this);
        mMyDialogManager = new MyDialogManager(getContext());
        mVerticalCancelDistance = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                50, getResources().getDisplayMetrics());
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mMyAudioManager.prepareAudio();
                mDoesLongClickActivate = true;
                return false;
            }
        });
    }

    @Override
    public void hasPreparedWell() {
        mHandler.sendEmptyMessage(MSG_AUDIO_HAS_PREPARED);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                updateState(STATE_RECORDING);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mIsRecording) {
                    if (wantToCancel(x, y)) {
                        updateState(STATE_WANT_TO_CANCEL);
                    } else {
                        updateState(STATE_RECORDING);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (!mDoesLongClickActivate) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!mIsRecording || mTimer < 0.6f) {
                    mMyDialogManager.voiceNotLongEnough();
                    mMyAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1300);
                } else if (mCurrentState == STATE_RECORDING) {
                    // release media recorder
                    mMyDialogManager.dismissDialog();
                    mMyAudioManager.release();
                    if (mListener != null) {
                        mListener.doFinish(mTimer, mMyAudioManager.getCurrentFilePath());
                    }
                    // call back to activity
                } else if (mCurrentState == STATE_WANT_TO_CANCEL) {
                    // cancel media recorder
                    mMyAudioManager.cancel();
                    mMyDialogManager.dismissDialog();
                }
                reset();
                break;
            }
        }
        return super.onTouchEvent(event);
    }


    private void reset() {
        mIsRecording = false;
        updateState(STATE_NORMAL);
        mTimer = 0;
        mDoesLongClickActivate = false;
    }

    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -mVerticalCancelDistance || y > getHeight() + mVerticalCancelDistance) {
            return true;
        }
        return false;
    }

    private void updateState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL: {
                    setBackgroundResource(R.drawable.recorder_button_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                }
                case STATE_RECORDING: {
                    setBackgroundResource(R.drawable.recorder_button_recording);
                    setText(R.string.str_recorder_recording);
                    if (mIsRecording) {
                        // update dialog style
                        mMyDialogManager.recording();
                    }
                    break;
                }
                case STATE_WANT_TO_CANCEL: {
                    setBackgroundResource(R.drawable.recorder_button_recording);
                    setText(R.string.str_recorder_want_to_cancel);
                    mMyDialogManager.wantToCancel();
                    break;
                }
            }
        }
    }


}
