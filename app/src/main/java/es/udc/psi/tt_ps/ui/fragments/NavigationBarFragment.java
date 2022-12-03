package es.udc.psi.tt_ps.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.geofire.GeoLocation;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarMenu;

import java.util.List;

import es.udc.psi.tt_ps.R;
import es.udc.psi.tt_ps.databinding.ActivityShowActivitiesBinding;
import es.udc.psi.tt_ps.databinding.FragmentNavigationBarBinding;
import es.udc.psi.tt_ps.ui.adapter.ListActivitiesAdapter;
import es.udc.psi.tt_ps.ui.viewmodel.ActivityListsPres;


public class NavigationBarFragment extends Fragment {

    private FragmentNavigationBarBinding binding;
    private NavigationBarFragment.FragmentListener listener;

    public interface FragmentListener {
        void onFragmentInteraction(int number);
    }

    public NavigationBarFragment() {
        // Required empty public constructor
    }

    public static NavigationBarFragment newInstance(String param1, String param2) {
        NavigationBarFragment fragment = new NavigationBarFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNavigationBarBinding.inflate(inflater, container, false);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.page_1:
                    listener.onFragmentInteraction(0);
                    break;
                case R.id.page_2:
                    listener.onFragmentInteraction(1);
                    Log.d("TAG", "onCreateView: dashboard");
                    break;
                case R.id.page_3:
                    listener.onFragmentInteraction(2);
                    Log.d("TAG", "onCreateView: notifications");
                    break;
                case R.id.page_4:
                    listener.onFragmentInteraction(3);
                    Log.d("TAG", "onCreateView: Profile");
                    break;
            }
            return true;
        });

        return binding.getRoot();
    }

    public void onTabChanged(int tabId) {
      //change the menu index
        Log.d("_TAG", "onTabChanged: " + tabId);
        BottomNavigationView menu = binding.bottomNavigation;
        switch (tabId) {
            case 0:
                menu.setSelectedItemId(R.id.page_1);
                break;
            case 1:
                menu.setSelectedItemId(R.id.page_2);
                break;
            case 2:
                menu.setSelectedItemId(R.id.page_3);
                break;
            case 3:
                menu.setSelectedItemId(R.id.page_4);
                break;
        }


    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ActivityListFragment.FragmentListener) {
            listener = (NavigationBarFragment.FragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentListener");
        }
    }

    public interface FragmentBarListener {
        void onTabChange(int tab);
    }
}