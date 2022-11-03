package es.udc.psi.tt_ps.core;

import android.content.Context;

import com.google.firebase.FirebaseApp;

import es.udc.psi.tt_ps.ui.view.MainActivity;

public class firebaseConnection implements connection {


    public void connect(Context ctx){
        FirebaseApp.initializeApp(ctx);
    }

}
