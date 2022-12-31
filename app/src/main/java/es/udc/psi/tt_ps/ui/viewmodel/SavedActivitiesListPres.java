package es.udc.psi.tt_ps.ui.viewmodel;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.udc.psi.tt_ps.data.repository.activityRepository;

public class SavedActivitiesListPres {
    activityRepository ar = new activityRepository();
    private DocumentSnapshot prevDocSnap;
    public boolean getMore = true;
    public List<String> participants = new ArrayList<>();


    public void setRecycledDataByRol(String rol){


    }
}


