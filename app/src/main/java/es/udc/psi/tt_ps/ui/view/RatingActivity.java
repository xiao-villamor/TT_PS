package es.udc.psi.tt_ps.ui.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import es.udc.psi.tt_ps.data.model.ActivityModel;
import es.udc.psi.tt_ps.data.model.UserModel;
import es.udc.psi.tt_ps.databinding.ActivityRatingBinding;
import es.udc.psi.tt_ps.domain.activity.getActivityUseCase;
import es.udc.psi.tt_ps.domain.user.editUserInfoUseCase;
import es.udc.psi.tt_ps.domain.user.getUserInfoUseCase;

public class RatingActivity extends AppCompatActivity {

    ActivityRatingBinding binding;
    float ratingGiven;
    String id;
    String userId;
    UserModel user;
    int participants;
    ActivityModel activityModel;
    String UUID;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRatingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            UUID = extras.getString("UUID");
            count = Integer.parseInt(extras.getString("count"));

            try {
                 participants = count;
                 userId = UUID;
                 if (userId != null)
                    user = getUserInfoUseCase.getInfo(userId).data;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
        binding.logoName.setText(user.getName());

        binding.ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            binding.ratingBar.setRating(rating);
            ratingGiven = rating;
        });

        binding.submitRating.setOnClickListener(v -> {
            Float oldRating = user.getRating();
            user.setRatingCount(user.getRatingCount()+1);
            user.setRating((oldRating +(ratingGiven/participants))/user.getRatingCount());
            try {
                editUserInfoUseCase.updateEditedUser(user);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            finish();
        });

    }
}