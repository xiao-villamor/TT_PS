package es.udc.psi.tt_ps.ui.viewmodel;

import static es.udc.psi.tt_ps.domain.activity.getFirstActivitiesFiltered.getActivitiesFiltered;
import static es.udc.psi.tt_ps.domain.activity.getNextActivitesFilteredUseCase.getActivitiesFilteredNext;
import static es.udc.psi.tt_ps.domain.activity.getUsersByName.getUsersByName;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoLocation;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.QueryResult;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.data.repository.userRepository;
import es.udc.psi.tt_ps.ui.adapter.ListActivitiesAdapter;

public class UserListPres extends RecyclerView.OnScrollListener {
    userRepository ur = new userRepository();
    public boolean getMore = true;
    public List<String> interests ;
    public List<Float> rating ;
    public List<String> rrss ;






    public void setRecycledDataFiltered(List<ListUsers> listUsers,String name) throws InterruptedException {
        Result<List<UserModel>,Exception> data;


        data = getUsersByName(name);
        if (data.exception == null && data.data.size() > 0) {
            Log.d("_TAG", "Presenter " + " data not null");
            List<UserModel> res = new ArrayList<>(data.data);


            for (int i = 0; i < res.size(); i++) {
                if (res.get(i).getrrss()==null){
                    rrss=new ArrayList<>();
                }else{
                    rrss=res.get(i).getrrss();
                }
                if (res.get(i).getRating()==null){
                    rating=new ArrayList<>();
                }else{
                    rating=res.get(i).getRating();
                }
                if (res.get(i).getInterests()==null){
                    interests=new ArrayList<>();
                }else{
                    interests=res.get(i).getInterests();
                }
                if(res.get(i).getrrss()==null) rrss=new ArrayList<>();
                if(res.get(i).getRating()==null) rating=new ArrayList<>();
                if(res.get(i).getInterests()==null) interests=new ArrayList<>();
                listUsers.add(new ListUsers(res.get(i).getName(),res.get(i).getSurname(),res.get(i).getBirthDate(),
                        res.get(i).getEmail(),res.get(i).getPhone(),res.get(i).getProfilePic(),res.get(i).getrrss(),
                        res.get(i).getRating(),res.get(i).getInterests(),res.get(i).getDescription()));


            }

        }
    }



}
