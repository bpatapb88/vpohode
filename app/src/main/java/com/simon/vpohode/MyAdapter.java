package com.simon.vpohode;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends FragmentStateAdapter {

    private static List<Item[]> looks;

    public MyAdapter(FragmentActivity fragmentActivity, List<Item[]> looks) {
        super(fragmentActivity);
        this.looks = looks;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return(PageFragment.newInstance(position));
    }

    @Override
    public int getItemCount() {
        if(looks == null){
            return 0;
        }
        return looks.size();
    }

    public static List<Item[]> getLooks() {
        return looks;
    }
}
