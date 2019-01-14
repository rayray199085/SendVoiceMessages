package com.project.stephencao.sendvoicemessages.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.project.stephencao.sendvoicemessages.R;

public class MyDialogManager {
    private Dialog mDialog;
    private ImageView mMicIcon;
    private ImageView mVolume;
    private TextView mLabel;
    private Context mContext;

    public MyDialogManager(Context context) {
        this.mContext = context;
    }

    public void displayRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.DialogTheme);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_dialog, null);
        mDialog.setContentView(view);
        mMicIcon = mDialog.findViewById(R.id.id_recorder_dialog_mic);
        mVolume = mDialog.findViewById(R.id.id_recorder_dialog_volume);
        mLabel = mDialog.findViewById(R.id.id_recorder_dialog_label);
        mDialog.show();
    }

    public void recording(){
        if(mDialog!=null && mDialog.isShowing()){
            mMicIcon.setVisibility(View.VISIBLE);
            mVolume.setVisibility(View.VISIBLE);
            mLabel.setVisibility(View.VISIBLE);
            mMicIcon.setImageResource(R.drawable.recorder);
            mLabel.setText(R.string.str_recorder_recording);
        }
    }

    public void wantToCancel() {
        if(mDialog!=null && mDialog.isShowing()){
            mMicIcon.setVisibility(View.VISIBLE);
            mVolume.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);
            mMicIcon.setImageResource(R.drawable.cancel);
            mLabel.setText(R.string.str_recorder_want_to_cancel);
        }
    }

    public void voiceNotLongEnough() {
        if(mDialog!=null && mDialog.isShowing()){
            mMicIcon.setVisibility(View.VISIBLE);
            mVolume.setVisibility(View.GONE);
            mLabel.setVisibility(View.VISIBLE);
            mMicIcon.setImageResource(R.drawable.voice_to_short);
            mLabel.setText(R.string.str_audio_too_short);
        }

    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void updateVolumeLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {
//            mMicIcon.setVisibility(View.VISIBLE);
//            mVolume.setVisibility(View.VISIBLE);
//            mLabel.setVisibility(View.VISIBLE);
            int resId = mContext.getResources().getIdentifier("v"+level,"drawable",mContext.getPackageName());
            mVolume.setImageResource(resId);
        }
    }
}
