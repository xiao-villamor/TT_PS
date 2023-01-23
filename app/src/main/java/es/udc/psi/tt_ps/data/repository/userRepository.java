package es.udc.psi.tt_ps.data.repository;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.data.network.user.userService;

public class userRepository {
    private final userService api = new userService();

    public void createUser(String email , String password , UserModel user, byte[] pic) throws ExecutionException, InterruptedException, TimeoutException {
        api.createUser(email,password,user,pic);
    }

    public void loginUser(String email, String password) throws ExecutionException, InterruptedException, TimeoutException {

        api.loginUser(email,password);
    }

    public void updateUser(UserModel user) {
        api.updateUser(user);
    }

    public UserModel getUser(String uuid) throws ExecutionException, InterruptedException, TimeoutException {
       return api.getUser(uuid);
    }

    public List<UserModel> getUserByUsername(String username) throws ExecutionException, InterruptedException, TimeoutException {
       return api.getUserByUsername(username);
    }

    public void deleteUser(){
        api.deleteUser();
    }

    public void UpdateUserDetails(String uuid, UserModel user){
        api.UpdateUserDetails(uuid,user);
    }

    public void updateUserEmail(String email){
        api.updateUserEmail(email);
    }

    public void updateUserPassword(String password){
        api.updateUserPassword(password);
    }

    public String uploadProfilePic(String uuid, byte[] image) throws FileNotFoundException, ExecutionException, InterruptedException, TimeoutException {
       return api.uploadProfilePic(uuid,image);
    }


}
    