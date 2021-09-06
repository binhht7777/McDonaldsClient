package com.example.mcdonalds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mcdonalds.Common.Common;
import com.example.mcdonalds.Retrofit.IMcDonaldsAPI;
import com.example.mcdonalds.Retrofit.RetrofitClient;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMcDonaldsAPI mcDonaldsAPI;
    TextInputEditText edtPhone;
    TextInputEditText edtHoten;
    Button btnLogin, btnSignUp;
    MaterialCheckBox chkDangky;

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        edtPhone = (TextInputEditText) findViewById(R.id.edtPhone);
        edtHoten = (TextInputEditText) findViewById(R.id.edtHoten);
        chkDangky = (MaterialCheckBox) findViewById(R.id.chkdDangky);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                startActivity(new Intent(MainActivity.this, com.example.mcdonalds.HomeActivity.class));
//            }
//        }, 3000);
        init();
        String userId = UUID.randomUUID().toString();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compositeDisposable.add(mcDonaldsAPI.getUsers(Common.API_KEY, edtPhone.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userModel -> {
                                    if (userModel.isSuccess()) {
                                        Common.currentUser = userModel.getResult().get(0);
                                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
//                                        Intent intent = new Intent(MainActivity.this, UpdateInfoActivity.class);
//                                        startActivity(intent);
//                                        finish();
                                        Toast.makeText(MainActivity.this, "Vui lòng đăng ký thông tin.!", Toast.LENGTH_SHORT).show();

                                    }
                                },
                                throwable -> {
                                    Toast.makeText(MainActivity.this, "[GET USER API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkDangky.isChecked()) {
                    compositeDisposable.add(mcDonaldsAPI.updateUses(Common.API_KEY,
                            userId,
                            edtPhone.getText().toString(),
                            edtHoten.getText().toString())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(updateUserModel -> {
                                        compositeDisposable.add(mcDonaldsAPI.getUsers(Common.API_KEY, edtPhone.getText().toString())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(userModel -> {
                                                            if (userModel.isSuccess()) {
                                                                Common.currentUser = userModel.getResult().get(0);
                                                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(MainActivity.this, "Vui lòng đăng ký thông tin.!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        },
                                                        throwable -> {
                                                            Toast.makeText(MainActivity.this, "Đăng ký không thành công" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }));
                                    },
                                    throwable -> {
                                        Toast.makeText(MainActivity.this, "[UPDATE USER API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }));
                }
            }
        });
    }

    private void init() {
        mcDonaldsAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMcDonaldsAPI.class);
    }

}