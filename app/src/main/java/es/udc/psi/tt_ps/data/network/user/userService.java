package es.udc.psi.tt_ps.data.network.user;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InvalidClassException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.data.model.UserModel;

public class userService implements userServiceInterface,FirebaseAuth.AuthStateListener{

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private UserModel user = null;
    OnAuthStateChangeListener onAuthStateChangeListener;
     public void auxCreateUser(String User, String pass) throws InterruptedException {
         Thread thread = new Thread(new Runnable() {
             @Override
             public void run() {
                 try {
                     mAuth.createUserWithEmailAndPassword(User, pass);
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         });
         thread.start();
         thread.join();
         thread.interrupt();
     }

    public void createUser(String email , String password , UserModel user, File pic) throws ExecutionException, InterruptedException, TimeoutException {
        Log.d("TAG","create start");
        Tasks.await(mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                //default value
                AtomicReference<String> url = new AtomicReference<>("");
                Log.d("PIC", pic.getPath());
                if(pic.getTotalSpace() != 0){
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                url.set(uploadProfilePic(mAuth.getUid(), pic));
                            } catch (InterruptedException | TimeoutException | ExecutionException | FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                    };
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    thread.interrupt();
                }

                Log.d("TAG","create end " +  url.get());
                user.profilePic(url.get());
                Thread t = new Thread(() -> {
                    try {
                        db.collection("User_Info").document(mAuth.getUid()).set(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                t.interrupt();
                try {
                    auxCreateUser(email, password);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithEmail:failure", task.getException());
                throw new RuntimeException(task.getException());
            }
        }), 10, TimeUnit.SECONDS);
    }

    public void loginUser(String email, String password) throws ExecutionException, InterruptedException, TimeoutException {
        Tasks.await(mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success " + task.getResult().getUser());
                } else {
                    Log.d("TAG", "signInWithEmail:failure " + task.getException());
                }
            }), 10, TimeUnit.SECONDS);

    }
    public void updateUser(UserModel user) {
        db.collection("User_Info").document(mAuth.getUid()).set(user);
    }

    public UserModel getUser(String uuid) throws ExecutionException, InterruptedException, TimeoutException {
        DocumentReference userDocument = (db.collection("User_Info").document(uuid));
        DocumentSnapshot res =  Tasks.await(userDocument.get(), 5, TimeUnit.SECONDS);
        if(res.exists()) {
            user = res.toObject(UserModel.class);
        }
        return user;
    }

    public List<UserModel> getUserByUsername(String username) throws ExecutionException, InterruptedException, TimeoutException {
        List <UserModel> users = null;

        Query userQuery = db.collection("User_Info").whereEqualTo("username", username);
        users = Tasks.await(userQuery.get(), 5, TimeUnit.SECONDS).toObjects(UserModel.class);

        return users;
    }

    public void deleteUser(){
        db.collection("User_Info").document(mAuth.getUid()).delete();
        storage.getReference().child("users_profile_pic").child(mAuth.getUid()+".jpg").delete();
        mAuth.getCurrentUser().delete();
    }

    public void UpdateUserDetails(String uuid, UserModel user){
        db.collection("User_Info").document(uuid).set(user);
    }

    public void updateUserEmail(String email){
        mAuth.getCurrentUser().updateEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                db.collection("User_Info").document(mAuth.getUid()).update("email", email);
            }
        });
    }
    public void updateUserPassword(String password){
        mAuth.getCurrentUser().updatePassword(password);
    }

    public String uploadProfilePic(String uuid, File image) throws FileNotFoundException, ExecutionException, InterruptedException, TimeoutException {
        StorageReference storageRef = storage.getReference();
        StorageReference profilePicRef = storageRef.child("users_profile_pic/"+uuid+".jpg");
        String uri = null;
        Log.d("TAG","image to upload in" + image.getAbsolutePath());

        UploadTask.TaskSnapshot res = Tasks.await(profilePicRef.putStream(new FileInputStream(image)), 10, TimeUnit.SECONDS);
        if(res != null){
            uri = Tasks.await(profilePicRef.getDownloadUrl(), 10, TimeUnit.SECONDS).toString();
            Log.d("TAG",uri);
        }
        return uri;
    }

    public void setListener(OnAuthStateChangeListener listener){
        onAuthStateChangeListener = listener;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Log.d("TAG", "onAuthStateChanged: "+ onAuthStateChangeListener );
        if(firebaseUser == null){
            onAuthStateChangeListener.onAuthStateChanged(true);
        }
    }

    public void addFirebaseAuthListener() {
        mAuth.addAuthStateListener(this);
    }

    public void removeFirebaseAuthListener() {
        mAuth.removeAuthStateListener(this);
    }

    private void firebaseSignOut(){
        mAuth.signOut();
    }

    public MutableLiveData<Boolean> signOut() {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            firebaseSignOut();
            mutableLiveData.setValue(true);
        }
        return mutableLiveData;
    }

}
