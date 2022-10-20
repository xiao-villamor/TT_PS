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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.concurrent.Executor;

import es.udc.psi.tt_ps.data.model.UserModel;

public class userRepository {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public String createUser( UserModel user){
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword("dev@gmail.com", "pass12345").addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithEmail:success");
                db.collection("User_Info").document(task.getResult().getUser().getUid()).set(user);

            } else {
                // If sign in fails, display a message to the user.
                Log.w("TAG", "signInWithEmail:failure", task.getException());

            }
        });
         return "mAuth.getCurrentUser().getDisplayName()";
    }
}
