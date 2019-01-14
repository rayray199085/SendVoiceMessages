package com.project.stephencao.sendvoicemessages.activity;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.project.stephencao.sendvoicemessages.R;
import com.project.stephencao.sendvoicemessages.adapter.MyRecorderAdapter;
import com.project.stephencao.sendvoicemessages.bean.Recorder;
import com.project.stephencao.sendvoicemessages.util.MyMediaManager;
import com.project.stephencao.sendvoicemessages.view.AudioRecorderButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AudioRecorderButton.OnAudioFinishListener {
    private ListView mListView;
    private ArrayAdapter<Recorder> mAdapter;
    private List<Recorder> mRecorders;
    private AudioRecorderButton mRecorderButton;
    private View mAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.id_listview);
        mRecorderButton = findViewById(R.id.id_recorder_button);
        mRecorderButton.setOnAudioFinishListener(this);
        mRecorders = new ArrayList<>();
        mAdapter = new MyRecorderAdapter(this, mRecorders);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mAnimView!=null){
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    mAnimView = null;
                }
                mAnimView = view.findViewById(R.id.id_recorder_anim);
                mAnimView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable drawable = (AnimationDrawable) mAnimView.getBackground();
                drawable.start();
                MyMediaManager.playSound(mRecorders.get(position).getFilePath(), new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimView.setBackgroundResource(R.drawable.adj);
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyMediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyMediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyMediaManager.release();
    }

    @Override
    public void doFinish(float duration, String filePath) {
        Recorder recorder = new Recorder(duration, filePath);
        mRecorders.add(recorder);
        mAdapter.notifyDataSetChanged();
        mListView.setSelection(mRecorders.size() - 1);
    }


}
