package com.example.baiahiu1_remake.Activity;

import static com.example.baiahiu1_remake.Function.Code.bottom_menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.example.baiahiu1_remake.Adapter.ViewPagerAdapter;
import com.example.baiahiu1_remake.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private SharedPreferences sharedPreferences;
    ViewPager2 viewPager2;
    TabLayout tabLayout;
//    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
         mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

         viewPager2 = (ViewPager2) findViewById(R.id.viewPager2);
         tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //--------------------------TAB LAYOUT------------------------------------------

        //Để tạo được Tabmeunu với TabLayout + Fragment + ViewPager2 cần có 5 bước:
        //B1: Tạo TabLayout và ViewPager2 ở layout
        //B2: Tạo Fragment tương ứng với mỗi tab
        //B3: Tạo Adapter cho ViewPager (ViewPager là một trình quản lý qiao diện cho phép người dùng lướt qua trái hoặc phải tùy thích
        //B4: set Adapter cho ViewPager
        //B5: Tạo sự kiện cho tablayout khi chọn vào tab sẽ set viewPager2 với số tương ứng cùng với fragment tương ứng

        viewPager2.setAdapter(new ViewPagerAdapter(this));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
                //.setCurrentItem là một phương thức trong Android được sử dụng để đặt trang hiển thị cho một ViewPager (hoặc ViewPager2).
                // Khi gọi phương thức này với một giá trị position, ViewPager sẽ chuyển đến trang tương ứng với vị trí đó.
                // Ví dụ, nếu bạn có một ViewPager hiển thị ba trang (vị trí 0, 1 và 2), bạn có thể sử dụng .setCurrentItem(1) để chuyển đến trang thứ hai.
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //phương thức này khi trang hiển thị trong ViewPager2 thay đổi, phương thức onPageSelected sẽ được gọi.
        //tabLayout.getTabAt(position).select(); - getTabAt là trả về Tab tại vị trí được chỉ định "position" (VD khi getTabAt(1) thì tab đấy sẽ luôn ở vị trí 1) -
        // .select() Phương thức này được gọi trên một Tab để đặt nó làm tab đang được chọn.

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });


        //-------------------------Design bottom menu---------------------------
        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottomMenu);
        bottom_menu(bottomNavigation);

        //-------------------------Su Kien bottom menu---------------------------
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position){
                    case 1:
                        Intent intent = new Intent(MainActivity.this, ToolActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        Intent intent1 = new Intent(MainActivity.this, MeActivity.class);
                        startActivity(intent1);
                        break;
                }

                return true;
            }
        });
    }
}