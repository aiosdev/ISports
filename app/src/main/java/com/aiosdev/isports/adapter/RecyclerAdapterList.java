package com.aiosdev.isports.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aiosdev.isports.R;
import com.aiosdev.isports.data.MapContract;
import com.aiosdev.isports.data.Task;

import java.util.List;




/**
 * 日期List适配器
 */

public class RecyclerAdapterList extends RecyclerView.Adapter<RecyclerAdapterList.MyViewHolder> {


    private Context context;
    private List<Task> list;
    private OnItemClickListener listener;

    public RecyclerAdapterList(Context context, List<Task> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final String date = list.get(position).getDate();
        final String taskNo = list.get(position).getTaskNo();
        holder.tvDate.setText(date);
        holder.tvTaskNo.setText("编号 " + taskNo);
        holder.tvSteps.setText(list.get(position).getStep() + " 步");
        int locations = getLocations(date, taskNo);
        holder.tvLocations.setText(String.valueOf(locations));
        holder.btDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除list数据源中的item
                list.remove(position);
                notifyDataSetChanged();

                //删除数据库Item数据
                delItemData(date, taskNo);
            }
        });

        /**
         * 调用接口回调
         */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener)
                    listener.onItemClick(position, date, v);
            }
        });
    }

    private int getLocations(String date, String taskNo) {
        int res = 0;
        Uri urlTask = MapContract.LoactionEntry.CONTENT_URI;
        String columns[] = new String[]{"count(*)"};
        String whereTask = "substr(" + MapContract.LoactionEntry.COLUMN_DATE_TIME + ", 1, 10) = ? and task_no = ?";
        String[] argusTask = {date, taskNo};
        res = context.getContentResolver().query(urlTask, columns,whereTask, argusTask,null).getColumnCount();

        return res;
    }

    private void delItemData(String date, String taskNo) {

        //删除数据库task数据
        Uri urlTask = MapContract.TaskEntry.CONTENT_URI;
        String whereTask = "date = ? and task_no = ?";
        String[] argusTask = {date, taskNo};
        context.getContentResolver().delete(urlTask, whereTask, argusTask);

        Uri urlLocation = MapContract.LoactionEntry.CONTENT_URI;
        String whereLocation = "substr(" + MapContract.LoactionEntry.COLUMN_DATE_TIME + ", 1, 10) = ? and " + MapContract.LoactionEntry.COLUMN_TASK_NO + " = ?";
        String[] argusLocation = {date, taskNo};
        context.getContentResolver().delete(urlLocation, whereLocation, argusLocation);

        Toast.makeText(context, "任务删除成功！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return null == list ? 0 : list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvDate;      //日期
        private TextView tvTaskNo;    //任务编号
        private TextView tvSteps;    //步数
        private TextView tvLocations;  //坐标点数量
        private Button btDel;


        public MyViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.item_view);
            tvTaskNo = (TextView) view.findViewById(R.id.item_task_no);
            tvSteps = (TextView) view.findViewById(R.id.item_steps);
            btDel = (Button) view.findViewById(R.id.item_del);
            tvLocations = (TextView) view.findViewById(R.id.item_locations);

        }
    }

    /**
     * 内部接口回调方法
     */
    public interface OnItemClickListener {
        void onItemClick(int position, Object object, View view);
    }

    /**
     * 设置监听方法
     *
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
