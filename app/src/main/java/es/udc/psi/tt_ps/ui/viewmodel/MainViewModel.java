package es.udc.psi.tt_ps.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;

import es.udc.psi.tt_ps.data.network.user.OnAuthStateChangeListener;
import es.udc.psi.tt_ps.data.repository.authRepository;


public class MainViewModel {
    private authRepository authRepository;
    LiveData<Boolean> isUserSignedOutLiveData;

    public MainViewModel(authRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void setAuthStateChangeListener(OnAuthStateChangeListener onAuthStateChangeListener) {
        Log.d("TAG", "setAuthStateChangeListener "+onAuthStateChangeListener.toString());
        authRepository.onAuthStateChangeListener = onAuthStateChangeListener;
        authRepository.setListener();
    }

    public void signOut(){
        isUserSignedOutLiveData = authRepository.signOut();
    }

    public void addAuthListener() {
        authRepository.addFirebaseAuthListener();
    }

    public void removeAuthListener(){
        authRepository.removeFirebaseAuthListener();
    }


}

