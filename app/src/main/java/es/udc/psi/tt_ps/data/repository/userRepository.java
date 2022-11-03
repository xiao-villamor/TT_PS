package es.udc.psi.tt_ps.data.repository;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
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

import es.udc.psi.tt_ps.data.model.UserModel;

public class userRepository {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private UserModel user = null;

    public userRepository(FirebaseAuth mAuth, FirebaseFirestore db, FirebaseStorage storage) {
        this.mAuth = mAuth;
        this.db = db;
        this.storage = storage;
    }

    public void createUser(String email , String password ,UserModel user,File pic){
        Log.d("TAG","create start");
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithEmail:success " + task.getResult().getUser() );
                //default value
                AtomicReference<String> url = new AtomicReference<>( "https://firebasestorage.googleapis.com/v0/b/tt-ps-f0782.appspot.com/o/users_profile_pic%2F40jG2SBbIySEzfCNaDvBE7I4yJ42.jpg?alt=media&token=99f24552-7d6c-4b9d-8ea1-484f76fa258f");

                if(pic != null) {
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
                }
                user.profilePic(url.get());
                db.collection("User_Info").document(task.getResult().getUser().getUid()).set(user);
            } else {

           }
        });
    }

    public void loginUser(String email, String password){

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                Log.d("TAG", "signInWithEmail:success " + task.getResult().getUser() );
            } else {

            }
        });
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

    public void updateUserPasword(String password){
        mAuth.getCurrentUser().updatePassword(password);
    }

    public String uploadProfilePic(String uuid, File image) throws FileNotFoundException, ExecutionException, InterruptedException, TimeoutException {
        StorageReference storageRef = storage.getReference();
        StorageReference profilePicRef = storageRef.child("users_profile_pic/"+uuid+".jpg");
        String uri = null;

        UploadTask.TaskSnapshot res = Tasks.await(profilePicRef.putStream(new FileInputStream(image)), 10, TimeUnit.SECONDS);
        if(res != null){
            uri = Tasks.await(profilePicRef.getDownloadUrl(), 10, TimeUnit.SECONDS).toString();
            Log.d("TAG",uri);
        }
        return uri;
    }

}
    