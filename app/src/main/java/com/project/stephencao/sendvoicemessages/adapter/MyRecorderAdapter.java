package com.project.stephencao.sendvoicemessages.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.project.stephencao.sendvoicemessages.R;
import com.project.stephencao.sendvoicemessages.bean.Recorder;

import java.util.List;

public class MyRecorderAdapter extends ArrayAdapter<Recorder> {
    private List<Recorder> mRecorders;
    private Context mContext;
    private int mMinItemWidth;
    private int mMaxItemWidth;

    public MyRecorderAdapter(@NonNull Context context, List<Recorder> mRecorders) {
        super(context, -1, mRecorders);
        this.mContext = context;
        this.mRecorders = mRecorders;
        WindowManager windowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_listview_item, parent, false);
            viewHolder.dialogIconLength = convertView.findViewById(R.id.id_recorder_length);
            viewHolder.duration = convertView.findViewById(R.id.id_duration);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Recorder recorder = mRecorders.get(position);
        viewHolder.duration.setText(Math.round(recorder.getDuration()) + "\"");
        ViewGroup.LayoutParams params = viewHolder.dialogIconLength.getLayoutParams();
        params.width = (int) (mMinItemWidth + (mMaxItemWidth / 60) * recorder.getDuration());
        viewHolder.dialogIconLength.setLayoutParams(params);
        return convertView;
    }

    class ViewHolder {
        View dialogIconLength;
        TextView duration;
    }
}
