package com.hynguyen.chitieucanhan.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.hynguyen.chitieucanhan.R;
import com.hynguyen.chitieucanhan.databinding.FragmentThongKeBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class ThongKeActivity extends AppCompatActivity {
    FragmentThongKeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = FragmentThongKeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.chipNavi.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                if (id == R.id.nav_ngay){
                    fragment = new ThongKe1Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "ngay");
                    fragment.setArguments(bundle);
                } else if (id == R.id.nav_thang){
                    fragment = new ThongKe1Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "thang");
                    fragment.setArguments(bundle);
                } else if (id == R.id.nav_tuan){
                    fragment = new ThongKe1Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "tuan");
                    fragment.setArguments(bundle);
                }
                loadFragment(fragment);
            }
        });
        binding.chipNavi.setItemSelected(R.id.nav_ngay, true);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frm_admin2, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}