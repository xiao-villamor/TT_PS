package es.udc.psi.tt_ps.ui.viewmodel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.udc.psi.tt_ps.R;

public class ListActivitiesAdapter extends RecyclerView.Adapter<ListActivitiesAdapter.ActivitiesHolder> {
    private List<ListActivities> mData;
    private final LayoutInflater mInflater;
    private final Context context;
    final ListActivitiesAdapter.OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(es.udc.psi.tt_ps.ui.viewmodel.ListActivities ListActivities);
    }

    public ListActivitiesAdapter(List<ListActivities> mData, Context context) {
        this.mData = mData;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        listener = null;
    }

    public ListActivitiesAdapter(List<ListActivities> mData, Context context, OnItemClickListener listener) {
        this.mData = mData;
        this.context = context;
        this.mInflater=LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActivitiesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_activities,null);
        return new ActivitiesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivitiesHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setItems(List<ListActivities> items){
        mData =items;
    }



    public static class ActivitiesHolder extends  RecyclerView.ViewHolder{
        ImageView activity_image;
        TextView title, location, end_date;

        ActivitiesHolder(View itemView){
            super(itemView);
            activity_image = itemView.findViewById(R.id.activity_image);
            title = itemView.findViewById(R.id.activityName);
            location = itemView.findViewById(R.id.coordenadasXY);
            end_date = itemView.findViewById(R.id.endDate);
        }

        void bindData(final ListActivities item){
            activity_image.setImageResource(item.getActivityImage());
            title.setText(item.getTitle());
            location.setText(item.getLocation().toString());
            end_date.setText(item.getEnd_date().toString());

        }

    }
}


