package com.example.myapplication.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.myapplication.BookActivity;
import com.example.myapplication.CarListActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.MyAppActivity;
import com.example.myapplication.SettingActivity;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private int initialSelectedItemId = R.id.nav_home; // Default selected item

    public void setInitialSelectedItemId(int itemId) {
        this.initialSelectedItemId = itemId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false);

        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);
        setupBottomNavigation();

        // Post delayed to ensure view is fully initialized
        bottomNavigationView.post(() -> {
            try {
                bottomNavigationView.setSelectedItemId(initialSelectedItemId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    public void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    Toast.makeText(getContext(), "Home Clicked", Toast.LENGTH_SHORT).show();
                    // Prevent launching same activity if we're already in MainActivity
                    if (!(getActivity() instanceof MainActivity)) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                    return true;
                } else if (itemId == R.id.nav_menucar) {
                    Toast.makeText(getContext(), "Cars Menu Clicked", Toast.LENGTH_SHORT).show();
                    // Prevent launching same activity if we're already in CarListActivity
                    if (!(getActivity() instanceof CarListActivity)) {
                        Intent intent = new Intent(getActivity(), CarListActivity.class);
                        startActivity(intent);
                    }
                    return true;
                } else if (itemId == R.id.nav_bookclock) {
                    Toast.makeText(getContext(), "Book Clicked", Toast.LENGTH_SHORT).show();
                    if (!(getActivity() instanceof BookActivity)) {
                        Intent intent = new Intent(getActivity(), BookActivity.class);
                        startActivity(intent);
                    }
                    return true;
                } else if (itemId == R.id.nav_info) {
                    Toast.makeText(getContext(), "Info Clicked", Toast.LENGTH_SHORT).show();
                    if (!(getActivity() instanceof SettingActivity)) {
                        Intent intent = new Intent(getActivity(), SettingActivity.class);
                        startActivity(intent);
                    }
                    return true;
                } else if(itemId == R.id.nav_camera){
                    Toast.makeText(getContext(), "Camera Clicked", Toast.LENGTH_SHORT).show();
                    if (!(getActivity() instanceof MyAppActivity)) {
                        Intent intent = new Intent(getActivity(), MyAppActivity.class);
                        startActivity(intent);
                    }
                    return true;
                }

                return false;
            }
        });
    }

    // ẩn/hiện navigation
    public void setNavigationVisibility(boolean visible) {
        if (bottomNavigationView != null) {
            if (visible) {
                bottomNavigationView.animate().alpha(1f).setDuration(300);
            } else {
                bottomNavigationView.animate().alpha(0f).setDuration(300);
            }
        }
    }
}