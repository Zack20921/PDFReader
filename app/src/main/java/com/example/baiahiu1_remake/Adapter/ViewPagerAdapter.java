package com.example.baiahiu1_remake.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.baiahiu1_remake.Fragment.AllPDFFragment;
import com.example.baiahiu1_remake.Fragment.BookmarkFragment;
import com.example.baiahiu1_remake.Fragment.RecentsFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    //Phương thức này tạo và trả về một Fragment dựa trên giá trị của position. Mỗi case trong switch tương ứng với 1 Fragment khác nhau
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new RecentsFragment();
            case 1:
                return new BookmarkFragment();
            case 2:
                return new AllPDFFragment();
            default:
                return new RecentsFragment();
        }
    }

    //Trả về số luọng menu của mình
    @Override
    public int getItemCount() {
        return 3;
    }
}
