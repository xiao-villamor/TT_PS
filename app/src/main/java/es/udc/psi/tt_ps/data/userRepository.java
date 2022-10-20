package es.udc.psi.tt_ps.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import es.udc.psi.tt_ps.data.model.UserModel;

public class userRepository {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private UserModel user;

    public userRepository(FirebaseAuth mAuth, FirebaseFirestore db) {
        this.mAuth = mAuth;
        this.db = db;
    }

    public void createUser(UserModel user){
        Log.d("TAG","create start");
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithEmail:success " + task.getResult().getUser() );
                db.collection("User_Info").document(task.getResult().getUser().getUid()).set(user);
            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithEmail:failure", task.getException());
            }
        });
    }

    public void updateUser(UserModel user) {
        db.collection("User_Info").document(mAuth.getUid()).set(user);
    }


    public void deleteUser() {
        db.collection("User_Info").document(mAuth.getUid()).delete();
        mAuth.getCurrentUser().delete();
    }

    public UserModel getUser(){
        db.collection("User_Info").document(mAuth.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            this.user = (document.toObject(UserModel.class));
                        } else {
                            this.user = (null);
                        }
                    } else {
                        this.user = (null);
                    }
                });
        return user;
    }



}
