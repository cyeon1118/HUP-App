package com.example.auctionapp.domain.user.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.auctionapp.MainActivity;
import com.example.auctionapp.databinding.ActivityChangeInfoBinding;
import com.example.auctionapp.domain.email.view.Email;
import com.example.auctionapp.domain.user.constant.Constants;
import com.example.auctionapp.domain.user.dto.SignUpRequest;
import com.example.auctionapp.domain.user.dto.UpdateUserRequest;
import com.example.auctionapp.domain.user.dto.UpdateUserResponse;
import com.example.auctionapp.domain.user.view.ChangeInfoView;
import com.example.auctionapp.global.retrofit.MainRetrofitCallback;
import com.example.auctionapp.global.retrofit.MainRetrofitTool;
import com.example.auctionapp.global.retrofit.RetrofitTool;

import org.json.JSONObject;

import java.util.regex.Pattern;

import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class ChangeInfoPresenter implements ChangeInfoPresenterInterface{
    // Attributes
    private ChangeInfoView changeInfoView;
    private ActivityChangeInfoBinding binding;
    private Context context;

    // Constructor
    public ChangeInfoPresenter(ChangeInfoView changeInfoView, ActivityChangeInfoBinding binding, Context context){
        this.changeInfoView = changeInfoView;
        this.binding = binding;
        this.context = context;
    }
    @Override
    public Intent UpdateCheck() {
        if(validLoginIdCheck()==false){
            return null;
        } else if(validPasswordCheck()==false){
            return null;
        } else if(validNameCheck()==false){
            return null;
        } else{
            UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                    .loginId(binding.edtUserId.getText().toString())
                    .username(binding.edtNickname.getText().toString())
                    .password(binding.edtPassword.getText().toString())
                    .phoneNumber(binding.edtPhoneNum.getText().toString())
                    .build();
            RetrofitTool.getAPIWithNullConverter().updateUser(updateUserRequest)
                    .enqueue(MainRetrofitTool.getCallback(new UpdateCallback()));

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            return intent;
        }
    }

    @Override
    public boolean validLoginIdCheck() {
        String inputId = binding.edtUserId.getText().toString();
        if(inputId.length() < 5 || inputId.length() > 11){
            showToast(Constants.ESignUp.idWarningMessage.getText());
            return false; }
        return true;
    }

    @Override
    public boolean validPasswordCheck() {
        String inputPW = binding.edtPasswordCheck.getText().toString();
        String inputCheckPW = binding.edtPassword.getText().toString();
        Boolean inputPWEnglishNumberResult = Pattern.matches(Constants.ESignUp.pwEnglishNumberFormat.getText(), inputPW);
        Boolean inputCheckPWEnglishNumberResult = Pattern.matches(Constants.ESignUp.pwEnglishNumberFormat.getText(), inputCheckPW);
        if(inputPW.length()==0 || inputCheckPW.length()==0){
            showToast(Constants.ESignUp.pwInputMessage.getText());
            return false;
        }
        if(inputPWEnglishNumberResult==false || inputCheckPWEnglishNumberResult==false){
            showToast(Constants.ESignUp.pwWarningMessage.getText());
            return false;
        }
        if(!inputPW.equals(inputCheckPW)){
            showToast(Constants.ESignUp.pwNotMatchMessage.getText());
            return false;
        }
        return true;
    }

    @Override
    public boolean validNameCheck() {
        String inputName = binding.edtNickname.getText().toString();
        if(inputName.length()==0){
            showToast(Constants.ESignUp.nameInputMessage.getText());
            return false;
        }
        if(inputName.length()<3 || inputName.length()>10 ){
            showToast(Constants.ESignUp.nameInput2Message.getText());
            return false;
        }
        return true;
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    class UpdateCallback implements MainRetrofitCallback<UpdateUserResponse> {

        @Override
        public void onSuccessResponse(Response<UpdateUserResponse> response) {
//            Constants.userId = response.body().getUserId();
            System.out.println("UpdateUser: "+Constants.userId);
        }
        @Override
        public void onFailResponse(Response<UpdateUserResponse> response) {
            try {

                JSONObject jObjError = new JSONObject(response.errorBody().string());
                Log.d("error", jObjError.getString("message"));
                showToast(jObjError.getString("message"));
            } catch (Exception e) {
                Log.d("error", e.getMessage());

            }
            Log.d(TAG, "onFailResponse");
        }

        @Override
        public void onConnectionFail(Throwable t) {
            Log.e("연결실패", t.getMessage());
        }
    }
}