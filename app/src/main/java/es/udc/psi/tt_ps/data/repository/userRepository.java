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

    public void createUser(UserModel user){
        Log.d("TAG","create start");
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithEmail:success " + task.getResult().getUser() );
                // Sbir imagen

                db.collection("User_Info").document(task.getResult().getUser().getUid()).set(user);
            } else {
                // If sign in fails, display a message to the user.
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
        Log.d("TAG", "getUser start");
        DocumentReference userDocument = (db.collection("User_Info").document(uuid));
        DocumentSnapshot res =  Tasks.await(userDocument.get(), 5, TimeUnit.SECONDS);
        if(res.exists()) {
            user = res.toObject(UserModel.class);
            Log.d("TAG", "getUser success");
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
    