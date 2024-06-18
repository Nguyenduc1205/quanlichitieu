package com.hynguyen.chitieucanhan.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hynguyen.chitieucanhan.R;
import com.hynguyen.chitieucanhan.databinding.FragmentThongKeBinding;
import com.hynguyen.chitieucanhan.mdel.ChiTieu;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.List;

public class ThongKeFragment extends Fragment {


    FragmentThongKeBinding binding;

    public ThongKeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentThongKeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frm_admin2, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}