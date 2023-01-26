package es.udc.psi.tt_ps.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.ui.viewmodel.ListUsers;

public class ListUsersAdapter extends RecyclerView.Adapter<ListUsersAdapter.UsersHolder>{
    private List<ListUsers> mData;
    private final LayoutInflater mInflater;
    private final Context context;
    static ListUsersAdapter.OnItemClickListener listener = null;

    public interface OnItemClickListener{
        void onItemClick(es.udc.psi.tt_ps.ui.viewmodel.ListUsers ListUsers);
    }

    public ListUsersAdapter(List<ListUsers> mData, Context context) {
        this.mData = mData;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        listener = null;
    }

    public ListUsersAdapter(List<ListUsers> mData, Context context, ListUsersAdapter.OnItemClickListener listener) {
        this.mData = mData;
        this.context = context;
        this.mInflater=LayoutInflater.from(context);
        ListUsersAdapter.listener = listener;
    }

    @NonNull
    @Override
    public ListUsersAdapter.UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_users, parent, false);
        return new ListUsersAdapter.UsersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListUsersAdapter.UsersHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    public List<ListUsers> getmData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setItems(List<ListUsers> items){
        mData =items;
    }



    public static class UsersHolder extends  RecyclerView.ViewHolder{
        ImageView profile_pic;
        TextView name, surname, birthdate;
        RatingBar ratingBar;

        UsersHolder(View itemView){
            super(itemView);
            profile_pic = itemView.findViewById(R.id.profile_pic);
            name = itemView.findViewById(R.id.userName);
            surname = itemView.findViewById(R.id.userSurname);
            birthdate = itemView.findViewById(R.id.userBirthDate);
            ratingBar = itemView.findViewById(R.id.ratingUser);
        }

        void bindData(final ListUsers item){
            if(item.getProfilePic() != null){
                Glide.with(itemView.getContext())
                        .load(item.getProfilePic())
                        .into(profile_pic);

            }
            name.setText(item.getName());
            surname.setText(item.getSurname());
            birthdate.setText(item.getBirthDate().toString());
            ratingBar.setRating(item.getRating().get(0));
            itemView.setOnClickListener(view -> listener.onItemClick(item));

        }

    }
}
