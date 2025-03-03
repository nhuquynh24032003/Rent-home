package com.example.myapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.myapplication.screens.AppointedFragment;
import com.example.myapplication.screens.MyAppointmentsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private String myId;

    public ViewPagerAdapter(@NonNull FragmentManager fm, String myId) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.myId = myId;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MyAppointmentsFragment.newInstance(myId);
            case 1:
                return AppointedFragment.newInstance(myId);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2; // Number of tabs
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "My Appointments";
            case 1:
                return "Scheduled Appointments";
            default:
                return null;
        }
    }
}
