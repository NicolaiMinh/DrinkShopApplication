package minh.com.drinkshop.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import minh.com.drinkshop.R;
import minh.com.drinkshop.model.CheckUserResponse;
import minh.com.drinkshop.model.User;
import minh.com.drinkshop.retrofit.IDrinkShopAPI;
import minh.com.drinkshop.utils.Common;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1001;
    private static int APP_REQUEST_CODE = 1000;
    @BindView(R.id.btn_continute)
    Button btnContinute;

    IDrinkShopAPI mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //start retrofit
        mService = Common.getAPI();

        //check session facebook account kit still available

        requestPermission();

        if (AccountKit.getCurrentAccessToken() != null) {
            //auto login
            final AlertDialog alertDialog = new SpotsDialog(MainActivity.this);
            alertDialog.show();
            alertDialog.setMessage("Please wait...");
            //get user phone and check exists on server
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {
                    mService.checkUserExists(account.getPhoneNumber().toString())//account.getPhoneNumber().toString() là số dtn nhập từ acount kit
                            .enqueue(new Callback<CheckUserResponse>() {
                                @Override
                                public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                    CheckUserResponse userResponse = response.body();
                                    if (userResponse.isExists()) {
                                        //fetch user information
                                        mService.getUserInformation(account.getPhoneNumber().toString())
                                                .enqueue(new Callback<User>() {
                                                    @Override
                                                    public void onResponse(Call<User> call, Response<User> response) {
                                                        //if user already exists , just start new activity
                                                        alertDialog.dismiss();

                                                        //set current user
                                                        Common.currentUser = response.body();

                                                        //start new activity
                                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                        finish();//close MainActivity
                                                    }

                                                    @Override
                                                    public void onFailure(Call<User> call, Throwable t) {
                                                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                    } else {
                                        //else need register
                                        alertDialog.dismiss();

                                        //show register dialog
                                        showRegisterDialog(account.getPhoneNumber().toString());
                                    }
                                }

                                @Override
                                public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Log.d("ERROR ", accountKitError.getErrorType().getMessage());
                }
            });
        }

//        printKeyHash();
    }


    private void requestPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0]  == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permission deny!", Toast.LENGTH_SHORT).show();
                }


                break;
            default:
                break;
        }
    }

    private void getUserForSGVN(){
        final AlertDialog alertDialog = new SpotsDialog(MainActivity.this);
        alertDialog.show();
        alertDialog.setMessage("Please wait...");
        mService.getUserInformation("123456789")
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        //if user already exists , just start new activity
                        alertDialog.dismiss();

                        //set current user
                        Common.currentUser = response.body();

                        //start new activity
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();//close MainActivity
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @OnClick({R.id.btn_continute})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_continute:
                getUserForSGVN();
//                startLoginPage(LoginType.PHONE);//login to account kit facebook
                break;
        }
    }

    private void startLoginPage(LoginType loginType) {
        Intent intent = new Intent(MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        loginType,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError() != null) {

                Toast.makeText(this, "" + loginResult.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();

            } else if (loginResult.wasCancelled()) {

                Toast.makeText(this, "Login Cancelled", Toast.LENGTH_SHORT).show();

            } else {
                if (loginResult.getAccessToken() != null) {
                    final AlertDialog alertDialog = new SpotsDialog(MainActivity.this);
                    alertDialog.show();
                    alertDialog.setMessage("Please wait...");
                    //get user phone and check exists on server
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(final Account account) {
                            mService.checkUserExists(account.getPhoneNumber().toString())//account.getPhoneNumber().toString() là số dtn nhập từ acount kit
                                    .enqueue(new Callback<CheckUserResponse>() {
                                        @Override
                                        public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                            CheckUserResponse userResponse = response.body();
                                            if (userResponse.isExists()) {
                                                //fetch user information
                                                mService.getUserInformation(account.getPhoneNumber().toString())
                                                        .enqueue(new Callback<User>() {
                                                            @Override
                                                            public void onResponse(Call<User> call, Response<User> response) {
                                                                //if user already exists , just start new activity
                                                                alertDialog.dismiss();


                                                                //get current user
                                                                Common.currentUser = response.body();
                                                                //start new activity
                                                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                                finish();//close MainActivity
                                                            }

                                                            @Override
                                                            public void onFailure(Call<User> call, Throwable t) {
                                                                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });


                                            } else {
                                                //else need register
                                                alertDialog.dismiss();

                                                //show register dialog
                                                showRegisterDialog(account.getPhoneNumber().toString());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Log.d("ERROR ", accountKitError.getErrorType().getMessage());
                        }
                    });
                } else {

                }


            }


        }
    }

    private void showRegisterDialog(final String phone) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("REGISTER");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        final View register_layout = layoutInflater.inflate(R.layout.register_layout, null);

        final MaterialEditText edt_name = register_layout.findViewById(R.id.edt_name);
        final MaterialEditText edt_address = register_layout.findViewById(R.id.edt_address);
        final MaterialEditText edt_birthday = register_layout.findViewById(R.id.edt_birthday);
        Button btn_register = register_layout.findViewById(R.id.btn_register);

        edt_birthday.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        builder.setView(register_layout);
        final AlertDialog dialog = builder.create();

        //event btn register
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                waitingDialog.show();
                waitingDialog.setMessage("Please wait...");

                dialog.dismiss();

                if (TextUtils.isEmpty(edt_name.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edt_address.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Please enter your address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edt_birthday.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Please enter your birthday", Toast.LENGTH_SHORT).show();
                    return;
                }

                mService.createNewUser(phone,
                        edt_name.getText().toString(),
                        edt_birthday.getText().toString(),
                        edt_address.getText().toString()
                ).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        waitingDialog.dismiss();
                        User user = response.body();
                        if (TextUtils.isEmpty(user.getError_msg())) {
                            Toast.makeText(MainActivity.this, "User register succesfully!", Toast.LENGTH_SHORT).show();
                            //current user
                            Common.currentUser = response.body();
                            //start new activity
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            finish();//close MainActivity
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        waitingDialog.dismiss();

                    }
                });
            }
        });

        dialog.show();

    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("minh.com.drinkshop",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //exit application when press back
    boolean isBackButtonPress = false;

    @Override
    public void onBackPressed() {
        if(isBackButtonPress){
            super.onBackPressed();
            return;
        }
        this.isBackButtonPress = true;
        Toast.makeText(this, "Please click Back again to exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        isBackButtonPress = false;
        super.onResume();

    }
}
