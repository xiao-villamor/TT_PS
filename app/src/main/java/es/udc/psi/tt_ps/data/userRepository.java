package es.udc.psi.tt_ps.data;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.Objects;
import java.util.concurrent.Executor;

import es.udc.psi.tt_ps.data.model.UserModel;

public class userRepository {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

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



}
