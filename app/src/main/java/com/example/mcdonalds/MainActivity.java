package com.example.mcdonalds;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mcdonalds.Common.Common;
import com.example.mcdonalds.Retrofit.IMcDonaldsAPI;
import com.example.mcdonalds.Retrofit.RetrofitClient;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMcDonaldsAPI mcDonaldsAPI;
    TextInputEditText edtPhone;
    TextInputEditText edtPassword;
    Button btnLogin, btnSignUp;
    MaterialCheckBox chkDangky;
    String IMEI = "";
    TextInputLayout outlinedTextField, outlinedTextField2;


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
        edtPassword = (TextInputEditText) findViewById(R.id.edtPassword);
//        chkDangky = (MaterialCheckBox) findViewById(R.id.chkdDangky);
        outlinedTextField = (TextInputLayout) findViewById(R.id.outlinedTextField);
        outlinedTextField2 = (TextInputLayout) findViewById(R.id.outlinedTextField2);
        edtPhone.setVisibility(View.INVISIBLE);
        edtPassword.setVisibility(View.INVISIBLE);
        btnLogin.setVisibility(View.INVISIBLE);
        btnSignUp.setVisibility(View.INVISIBLE);
        outlinedTextField.setVisibility(View.INVISIBLE);
        outlinedTextField2.setVisibility(View.INVISIBLE);
        IMEI = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Common.Imei = IMEI;

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                startActivity(new Intent(MainActivity.this, com.example.mcdonalds.HomeActivity.class));
//            }
//        }, 3000);
        init();
        checkImei();
//        String userId = UUID.randomUUID().toString();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compositeDisposable.add(mcDonaldsAPI.getUserByPhone(Common.API_KEY, edtPhone.getText().toString(), edtPassword.getText().toString())
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
                                        Snackbar.make(v, "Vui l??ng ????ng k?? th??ng tin.!", Snackbar.LENGTH_LONG)
                                                .setAction("Th??ng b??o", null).show();
//                                        Toast.makeText(MainActivity.this, "Vui l??ng ????ng k?? th??ng tin.!", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(MainActivity.this, CreateUserActivity.class);
                startActivity(intent);
                finish();
//                if (chkDangky.isChecked()) {
//                    compositeDisposable.add(mcDonaldsAPI.updateUses(Common.API_KEY,
//                            IMEI,
//                            edtPhone.getText().toString(),
//                            edtHoten.getText().toString())
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(updateUserModel -> {
//                                        compositeDisposable.add(mcDonaldsAPI.getUserByPhone(Common.API_KEY, edtPhone.getText().toString())
//                                                .subscribeOn(Schedulers.io())
//                                                .observeOn(AndroidSchedulers.mainThread())
//                                                .subscribe(userModel -> {
//                                                            if (userModel.isSuccess()) {
//                                                                Common.currentUser = userModel.getResult().get(0);
//                                                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                                                                startActivity(intent);
//                                                                finish();
//                                                            } else {
//                                                                Toast.makeText(MainActivity.this, "Vui l??ng ????ng k?? th??ng tin.!", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        },
//                                                        throwable -> {
//                                                            Toast.makeText(MainActivity.this, "????ng k?? kh??ng th??nh c??ng" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                                                        }));
//                                    },
//                                    throwable -> {
//                                        Toast.makeText(MainActivity.this, "[UPDATE USER API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }));
//                }
            }
        });
    }

    private void checkImei() {
        compositeDisposable.add(mcDonaldsAPI.getImei(Common.API_KEY, IMEI)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userModel -> {
                            if (userModel.isSuccess()) {
                                Common.currentUser = userModel.getResult().get(0);
                                if (userModel.getResult().get(0).getIscustomeryn().compareTo("N") == 0) {
                                    Common.isCustomerYN = userModel.getResult().get(0).getIscustomeryn();
                                    edtPhone.setVisibility(View.INVISIBLE);
                                    edtPassword.setVisibility(View.INVISIBLE);
//                                    chkDangky.setVisibility(View.INVISIBLE);
                                    btnLogin.setVisibility(View.INVISIBLE);
                                    btnSignUp.setVisibility(View.INVISIBLE);
                                    outlinedTextField.setVisibility(View.INVISIBLE);
                                    outlinedTextField2.setVisibility(View.INVISIBLE);
//                                    Toast.makeText(MainActivity.this, "May cua hang!", Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                            finish();
                                        }
                                    }, 3000);
                                } else {
                                    Common.isCustomerYN = userModel.getResult().get(0).getIscustomeryn();
                                    edtPhone.setVisibility(View.VISIBLE);
                                    edtPassword.setVisibility(View.VISIBLE);
//                                    chkDangky.setVisibility(View.VISIBLE);
                                    btnLogin.setVisibility(View.VISIBLE);
                                    btnSignUp.setVisibility(View.VISIBLE);
                                    outlinedTextField.setVisibility(View.VISIBLE);
                                    outlinedTextField2.setVisibility(View.VISIBLE);
//                                    Toast.makeText(MainActivity.this, "May khach.!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Common.isCustomerYN = "Y";
//                                Toast.makeText(MainActivity.this, "L???i, vui l??ng li??n h??? ti???p t??n.!", Toast.LENGTH_SHORT).show();
                                edtPhone.setVisibility(View.VISIBLE);
                                edtPassword.setVisibility(View.VISIBLE);
//                                    chkDangky.setVisibility(View.VISIBLE);
                                btnLogin.setVisibility(View.VISIBLE);
                                btnSignUp.setVisibility(View.VISIBLE);
                                outlinedTextField.setVisibility(View.VISIBLE);
                                outlinedTextField2.setVisibility(View.VISIBLE);
//                                    Toast.makeText(MainActivity.this, "May khach.!", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            Toast.makeText(MainActivity.this, "[GET IMEI API]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));
    }

    private void init() {
        mcDonaldsAPI = RetrofitClient.getInstance(Common.API_RESTAURANT_ENDPOINT).create(IMcDonaldsAPI.class);
    }

}