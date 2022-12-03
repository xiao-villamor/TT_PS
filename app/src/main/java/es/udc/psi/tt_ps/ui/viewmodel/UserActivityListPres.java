package es.udc.psi.tt_ps.ui.viewmodel;

import static es.udc.psi.tt_ps.domain.activity.getUserActivitiesUseCase.getActivitiesByAdmin;

import android.content.Context;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.udc.psi.tt_ps.core.firebaseConnection;
import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.repository.activityRepository;
import es.udc.psi.tt_ps.databinding.ActivityUserInfoBinding;
import es.udc.psi.tt_ps.ui.view.UserInfoActivity;

public class UserActivityListPres {

    activityRepository ar = new activityRepository();

    public static void moreActivityInfo(ListActivities ListActivities){
        //Metodo para ir a la vista detallada de actividades
        Log.d("TAG", "Mostrar en detalle" );
        //Intent intent = new Intent(this,ActivityListActivities.class);
        //intent.putExtra("events", ListActivities);
        //startActivity(intent);
    }

    public void setRecycledData(List<ListActivities> listActivities,Context ctx) throws InterruptedException, IOException {

        Result<List<ActivityModel>, Exception> data ;

        data = getActivitiesByAdmin(firebaseConnection.getUser(),5);

        List<ActivityModel> res = new ArrayList<>(data.data);
        //get context

        //get location name using geocoder
        Geocoder geocoder = new Geocoder(ctx ,Locale.getDefault());

        for (int i=0; i<res.size();i++){
            List<Address> addresses = geocoder.getFromLocation(res.get(i).getLocation().getLatitude(), res.get(i).getLocation().getLongitude(), 1);
            Address obj = addresses.get(0);
            String name = obj.getFeatureName() +", " +obj.getLocality() + ", " + obj.getCountryName();
            listActivities.add(new ListActivities(res.get(i).getImage(),res.get(i).getTitle(),
                    name, res.get(i).getStart_date(),
                    res.get(i).getDescription()));
        }
    }


}
