package es.udc.psi.tt_ps.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.data.model.Result;
import es.udc.psi.tt_ps.databinding.ActivityCreateBinding;
import es.udc.psi.tt_ps.domain.activity.createActivityUseCase;


public class ActivityCreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;
    private List<String> selectedTags=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        binding.createActivity.setOnClickListener(v -> {


            try {
                Result<Object, Exception> res = createActivityUseCase.createAcyivity(
                        binding.activityTitle.getText().toString(), binding.activityDescription.getText().toString(),
                        Date.valueOf(binding.activityStart.getText().toString()), Date.valueOf(binding.activityEnd.getText().toString()),
                        user.getUid(), selectedTags);

                if(res.exception!=null){
                    Log.d("TAG", res.exception.toString());
                    Toast.makeText(getApplicationContext(), "Activity cannot be created", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("TAG", "Actividad creada correctamente");
                    Toast.makeText(getApplicationContext(), "activity created successfully", Toast.LENGTH_SHORT).show();
                    Intent userProfileIntent = new Intent(this, MainActivity.class);
                    startActivity(userProfileIntent);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });


        binding.activityTag.setOnClickListener(v -> {
            selectedTags = new ArrayList();
            showTagsChooser();
        });

    }


    private void showTagsChooser(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
        dialogo.setTitle("Choose the tags");
        //Array con los posibles intereses
        String[] interests=getResources().getStringArray(R.array.interests_array);


        dialogo.setMultiChoiceItems(interests, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            selectedTags.add(interests[which]);

                        } else if (selectedTags.contains(interests[which])) {
                            // Else, if the item is already in the array, remove it
                            selectedTags.remove(interests[which]);
                        }
                        Log.d("_TAG", selectedTags.toString());
                    }
                }
        );

        dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("_TAG", "Dialogo aceptado");
                if(selectedTags.isEmpty()){
                    selectedTags=null;
                }
            }
        });
        dialogo.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("_TAG", "Dialogo cancelado");
                selectedTags=null;
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = dialogo.create();
        alert.show();
    }



}