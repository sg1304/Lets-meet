package com.project.letsmeet;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch(i){
            case 0: EventsFragment eventsFragment = new EventsFragment();
                    return eventsFragment;
            case 1: AddEventFragment addEventFragment = new AddEventFragment();
                return addEventFragment;
            case 2: ProfileFragment profileFragment = new ProfileFragment();
                return profileFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0: return "My Events";
            case 1: return "Add Event";
            case 2: return "Profile";
            default:
                return null;
        }

    }
}
