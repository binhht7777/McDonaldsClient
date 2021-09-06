package com.example.mcdonalds;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mcdonalds.Adapter.BackgroundSliderAdapter;
import com.example.mcdonalds.Common.Common;
import com.example.mcdonalds.Database.CartDataSource;
import com.example.mcdonalds.Database.CartDatabase;
import com.example.mcdonalds.Database.LocalCartDataSource;
import com.example.mcdonalds.Retrofit.IMcDonaldsAPI;
import com.example.mcdonalds.Retrofit.RetrofitClient;
import com.example.mcdonalds.Services.PicassoImageLoadingService;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.w3c.dom.Text;

import java.util.Calendar;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import ss.com.bannerslider.Slider;

public class PlaceOrderActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    EditText edt_date;
    TextView txt_total_cash, txt_user_phone, txt_user_address, txt_new_address;
    Button btn_add_new_address, btn_process;
    CheckBox ckb_default_address;
    RadioButton rdi_ood, rdi_online_payment;
    Toolbar toolbar;


    IMcDonaldsAPI iMcDonaldsAPI;
    BackgroundSliderAdapter adapter;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    CartDataSource cartDataSource;

    boolean isSelectDate = false, isAddNewAddr = false;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        edt_date = (EditText) findViewById(R.id.edt_date);
        txt_total_cash = (TextView) findViewById(R.id.txt_total_cash);
        txt_user_phone = (TextView) findViewById(R.id.text_user_phone);
        txt_user_address = (TextView) findViewById(R.id.txt_user_address);
//        txt_new_address = (TextView) findViewById(R.id.txt_new_address);
//        btn_add_new_address = (Button) findViewById(R.id.btn_new_address);
        ckb_default_address = (CheckBox) findViewById(R.id.chk_default_address);
        rdi_ood = (RadioButton) findViewById(R.id.rdi_ood);
        rdi_online_payment = (RadioButton) findViewById(R.id.rdi_online_payment);
        btn_process = (Button) findViewById(R.id.btn_processed);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        init();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        txt_user_phone.setText(Common.currentUser.getUserPhone());
        txt_user_address.setText(Common.currentUser.getName());

        toolbar.setTitle(getString(R.string.place_order));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btn_add_new_address.setOnClickListener(v -> {
            isAddNewAddr = true;
            ckb_default_address.setChecked(false);
            View layout_add_new_address = LayoutInflater.from(PlaceOrderActivity.this)
                    .inflate(R.layout.layout_add_new_address, null);

            EditText edt_new_address = (EditText) layout_add_new_address.findViewById(R.id.edt_add_new_address);
            edt_new_address.setText(txt_new_address.getText().toString());
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PlaceOrderActivity.this)
                    .setTitle("Add New Address")
                    .setView(layout_add_new_address)
                    .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
                    .setPositiveButton("Thêm mới", ((dialog, which) -> txt_new_address.setText(edt_new_address.getText().toString())));

            androidx.appcompat.app.AlertDialog addNewAddressDialog = builder.create();
            addNewAddressDialog.show();
        });

        edt_date.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(PlaceOrderActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH));
            dpd.show(getSupportFragmentManager(), "Datepickerdialog");
        });
        btn_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSelectDate) {
                    Toast.makeText(PlaceOrderActivity.this, "Please select date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isAddNewAddr) {
                    if (!ckb_default_address.isChecked()) {
                        Toast.makeText(PlaceOrderActivity.this, "Please choose default address or set new address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (rdi_ood.isChecked()) {
                } else if (rdi_online_payment.isChecked()) {

                }
            }
        });
    }

    private void init() {
        iMcDonaldsAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMcDonaldsAPI.class);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        isSelectDate = true;
        edt_date.setText(new StringBuilder("")
                .append(monthOfYear)
                .append("/")
                .append(dayOfMonth)
                .append("/")
                .append(year));

    }
}