package com.onlylemi.indoor;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onlylemi.dr.util.AsyncImageLoader;
import com.onlylemi.parse.Data;
import com.onlylemi.parse.info.ActivityTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by only乐秘 on 2015-10-05.
 */
public class ViewsActivityAdapter extends BaseAdapter {

    private static final String TAG = "ViewsActivityAdapter:";

    public List<ActivityTable> activityTable;
    private Context mContext;
    private AsyncImageLoader asyncImageLoader;
    int pid = 2;
    int fn = 1;

    public ViewsActivityAdapter(Context context) {
        this.mContext = context;
        activityTable = new ArrayList<>();
        for (int i = 0; i < Data.viewTableList.size(); i++) {
            if (Data.viewTableList.get(i).getPid() == pid &&
                    Data.viewTableList.get(i).getFn() == fn) {
                for (ActivityTable activities : Data.activityTableList) {
                    if (activities.getVid() == Data.viewTableList.get(i).getId()) {
                        activityTable.add(activities);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public int getCount() {
        return activityTable.size();
    }


    @Override
    public Object getItem(int position) {
        return activityTable.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FindActivityHolder activityHolder;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.views_activity_list_item, null);
            activityHolder = new FindActivityHolder();
            activityHolder.name = (TextView) convertView.findViewById(R.id.views_activity_name);
            activityHolder.time = (TextView) convertView.findViewById(R.id.views_activity_time);
            activityHolder.intro = (TextView) convertView.findViewById(R.id.views_activity_intro);
            convertView.setTag(activityHolder);
        } else {
            activityHolder = (FindActivityHolder) convertView.getTag();
        }

        activityHolder.name.setText(activityTable.get(position).getName());
        activityHolder.time.setText(activityTable.get(position).getTime());
        activityHolder.intro.setText(activityTable.get(position).getIntro());

        return convertView;
    }

    class FindActivityHolder {

        public TextView name;
        public TextView time;
        public TextView intro;

    }
}
