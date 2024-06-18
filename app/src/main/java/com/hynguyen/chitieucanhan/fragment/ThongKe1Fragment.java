package com.hynguyen.chitieucanhan.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hynguyen.chitieucanhan.R;
import com.hynguyen.chitieucanhan.adapter.ChiTieuAdapter;
import com.hynguyen.chitieucanhan.database.AppViewModel;
import com.hynguyen.chitieucanhan.databinding.FragmentThongKe1Binding;
import com.hynguyen.chitieucanhan.mdel.ChiTieu;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ThongKe1Fragment extends Fragment {

    private static final String CHANNEL_ID = "expense_notifications";
    private static final int NOTIFICATION_ID = 1;
    private static final long MONTHLY_EXPENSE_THRESHOLD = 1000000;  // Set your threshold value here

    FragmentThongKe1Binding binding;
    private String type = "ngay";
    private long tongThu;
    private long tongChi;
    private LiveData<List<ChiTieu>> chiTieuLiveData;
    private AppViewModel appViewModel;
    private ChiTieuAdapter chiTieuAdapter;
    int year, month, dayOfMonth;

    public ThongKe1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentThongKe1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appViewModel = new AppViewModel(getActivity().getApplication());
        chiTieuLiveData = appViewModel.tatCaChiTieu();
        if (getArguments() != null) {
            type = getArguments().getString("type");
        }

        chiTieuAdapter = new ChiTieuAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rcvThongKe.setLayoutManager(linearLayoutManager);
        binding.rcvThongKe.setAdapter(chiTieuAdapter);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        updateDatePicker(calendar);

        binding.cvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    updateDatePicker(calendar);
                    getData(calendar.getTimeInMillis());
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        chiTieuLiveData.observe(getViewLifecycleOwner(), new Observer<List<ChiTieu>>() {
            @Override
            public void onChanged(List<ChiTieu> chiTieus) {
                chiTieuAdapter.setChiTieus(chiTieus);
                getData(calendar.getTimeInMillis());
            }
        });

        // Create notification channel
        createNotificationChannel();
    }

    private void updateDatePicker(Calendar calendar) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        binding.tvTime.setText(sdf1.format(calendar.getTime()));
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void getData(long timeInMillis) {
        switch (type) {
            case "ngay":
                thongke("ngay");
                SimpleDateFormat sdfNgay = new SimpleDateFormat("dd/MM/yyyy");
                binding.tvTime.setText("Ngày : " + sdfNgay.format(timeInMillis));
                break;
            case "thang":
                thongke("thang");
                SimpleDateFormat sdfThang = new SimpleDateFormat("MM/yyyy");
                binding.tvTime.setText("Tháng : " + sdfThang.format(timeInMillis));
                break;
            case "tuan":
                thongke("tuan");
                SimpleDateFormat sdfTuan = new SimpleDateFormat("W/yyyy");
                binding.tvTime.setText("Tuần : " + sdfTuan.format(timeInMillis));
                break;
        }
    }

    private void thongke(String type) {
        tongThu = 0;
        tongChi = 0;
        Log.d("TAGAPI2", "thongke: " + dayOfMonth + " " + month + " " + year);
        List<ChiTieu> filteredChiTieu = new ArrayList<>();

        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(year, month, dayOfMonth);

        for (ChiTieu chiTieu : chiTieuLiveData.getValue()) {
            Calendar chiTieuCalendar = Calendar.getInstance();
            Date date = chiTieu.getDate();
            chiTieuCalendar.set(date.getYear(), date.getMonth(), date.getDate());
            Log.d("TAGAPI2", "thongke2: " + chiTieuCalendar.get(Calendar.DAY_OF_MONTH) + " " + chiTieuCalendar.get(Calendar.MONTH) + " " + chiTieuCalendar.get(Calendar.YEAR));

            boolean matches = false;
            if (type.equals("ngay")) {
                matches = (chiTieuCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth
                        && chiTieuCalendar.get(Calendar.MONTH) == month
                        && chiTieuCalendar.get(Calendar.YEAR) == year);
            } else if (type.equals("thang")) {
                matches = (chiTieuCalendar.get(Calendar.MONTH) == month
                        && chiTieuCalendar.get(Calendar.YEAR) == year);
            } else if (type.equals("tuan")) {
                int selectedWeek = selectedCalendar.get(Calendar.WEEK_OF_YEAR);
                matches = (chiTieuCalendar.get(Calendar.WEEK_OF_YEAR) == selectedWeek
                        && chiTieuCalendar.get(Calendar.YEAR) == year);
            }

            if (matches) {
                if (chiTieu.getType() == 1) {
                    tongThu += Long.parseLong(chiTieu.getMoney());
                } else {
                    tongChi += Long.parseLong(chiTieu.getMoney());
                }
                filteredChiTieu.add(chiTieu);
            }
        }

        // Update the adapter with the filtered list
        chiTieuAdapter.setChiTieus(filteredChiTieu);

        // Update the UI with the calculated totals
        binding.tvTongThu.setText("Tổng Thu : " + numberFormat(String.valueOf(tongThu)));
        binding.tvTongChi.setText("Tổng Chi : " + numberFormat(String.valueOf(tongChi)));

        // Check if the total expenses exceed the monthly threshold
        if (type.equals("thang") && tongChi > tongThu) {
            sendOverExpenseNotification();
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Expense Notifications";
            String description = "Notifications for when expenses exceed the threshold";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint("NotificationPermission")
    private void sendOverExpenseNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Chi tiêu vượt mức")
                .setContentText("Chi tiêu của bạn đã vượt quá mức cho phép trong tháng!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public String numberFormat(String string) {
        Long number = Long.parseLong(string);
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        String formattedString = formatter.format(number);
        return formattedString;
    }
}
