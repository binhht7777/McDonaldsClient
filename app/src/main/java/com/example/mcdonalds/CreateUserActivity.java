package com.example.mcdonalds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mcdonalds.Common.Common;
import com.example.mcdonalds.Retrofit.IMcDonaldsAPI;
import com.example.mcdonalds.Retrofit.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CreateUserActivity extends AppCompatActivity {
    IMcDonaldsAPI iMcDonaldsAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    TextInputEditText edtPhone, edtName, edtAddress, edtPassword;
    Button btnUpdate;
    String Imei = "";

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        edtPhone = (TextInputEditText) findViewById(R.id.edtPhone);
        edtName = (TextInputEditText) findViewById(R.id.edtName);
        edtAddress = (TextInputEditText) findViewById(R.id.edtAddress);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        edtPassword = (TextInputEditText)findViewById(R.id.edtPassword);

        ButterKnife.bind(this);
        init();
        initView();
    }

    private void initView() {
        Imei = Common.Imei;
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compositeDisposable.add(iMcDonaldsAPI.updateUserInfo(Common.API_KEY,
                        Imei,
                        edtPhone.getText().toString(),
                        edtPassword.getText().toString(),
                        edtName.getText().toString(),
                        edtAddress.getText().toString(),
                        "Y")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(updateUserModel -> {
                                    if (updateUserModel.isSuccess()) {
                                        compositeDisposable.add(iMcDonaldsAPI.getImei(Common.API_KEY, Imei)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(userModel -> {
                                                            if (userModel.isSuccess()) {
                                                                Common.currentUser = userModel.getResult().get(0);
                                                                startActivity(new Intent(CreateUserActivity.this, HomeActivity.class));
                                                                finish();
                                                            } else {
                                                                Toast.makeText(CreateUserActivity.this, "GET USER RETURN" + userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        },
                                                        throwable -> {

                                                            Toast.makeText(CreateUserActivity.this, "[GET USER]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                        })
                                        );
                                    } else {
                                        Toast.makeText(CreateUserActivity.this, "[UPDATE USER API RETURN]" + updateUserModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                },
                                throwable -> {
                                    Toast.makeText(CreateUserActivity.this, "[UPDATE USER API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                );
            }
        });
    }

    private void init() {
        iMcDonaldsAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMcDonaldsAPI.class);
    }
}