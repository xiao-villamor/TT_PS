package es.udc.psi.tt_ps.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import es.udc.psi.tt_ps.data.network.user.OnAuthStateChangeListener;
import es.udc.psi.tt_ps.data.network.user.userService;

public class authRepository implements FirebaseAuth.AuthStateListener {
    private final userService api = new userService();
    public OnAuthStateChangeListener onAuthStateChangeListener;

    public void setListener() {
        api.setListener(onAuthStateChangeListener);
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        api.onAuthStateChanged(firebaseAuth);

    }

    public void addFirebaseAuthListener() {
        api.addFirebaseAuthListener();
    }

    public void removeFirebaseAuthListener() {
        api.removeFirebaseAuthListener();
    }

    public MutableLiveData<Boolean> signOut() {
        return api.signOut();
    }
}
