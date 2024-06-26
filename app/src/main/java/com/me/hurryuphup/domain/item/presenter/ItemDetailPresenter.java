package com.me.hurryuphup.domain.item.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.me.hurryuphup.MainActivity;
import com.me.hurryuphup.R;
import com.me.hurryuphup.databinding.ActivityItemDetailBinding;
import com.me.hurryuphup.domain.item.adapter.ItemDetailViewPagerAdapter;
import com.me.hurryuphup.domain.item.adapter.qnaAdapter;
import com.me.hurryuphup.domain.item.constant.ItemConstants;
import com.me.hurryuphup.global.dto.DefaultResponse;
import com.me.hurryuphup.domain.item.dto.ItemDetailsResponse;
import com.me.hurryuphup.domain.item.model.qnaData;
import com.me.hurryuphup.domain.item.view.ItemDetailView;
import com.me.hurryuphup.domain.pricesuggestion.dto.MaximumPriceResponse;
import com.me.hurryuphup.domain.pricesuggestion.dto.ParticipantsResponse;
import com.me.hurryuphup.domain.scrap.dto.ScrapCheckedRequest;
import com.me.hurryuphup.domain.scrap.dto.ScrapCheckedResponse;
import com.me.hurryuphup.domain.scrap.dto.ScrapRegisterRequest;
import com.me.hurryuphup.domain.scrap.dto.ScrapRegisterResponse;
import com.me.hurryuphup.domain.user.constant.Constants;
import com.me.hurryuphup.domain.user.dto.UserInfoResponse;
import com.me.hurryuphup.global.retrofit.MainRetrofitCallback;
import com.me.hurryuphup.global.retrofit.MainRetrofitTool;
import com.me.hurryuphup.global.retrofit.RetrofitTool;
import com.me.hurryuphup.global.util.ErrorMessageParser;
import com.me.hurryuphup.global.util.GetTime;

import org.json.JSONException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class ItemDetailPresenter implements ItemDetailPresenterInterface{
    private ArrayList<String> itemImageList;
    private static final int DP = 24;
    // id
    Long myId = Constants.userId;
    private Long itemId;
    private Long scrapId;
    public Boolean isHeart = false;
    ItemDetailViewPagerAdapter itemDetailViewPagerAdapter;
    ArrayList<qnaData> qnaList = new ArrayList<qnaData>();
    qnaAdapter adapter;

    // Attributes
    private ItemDetailView itemDetailView;
    private ActivityItemDetailBinding binding;
    private Context context;
    ErrorMessageParser errorMessageParser;

    // Constructor
    public ItemDetailPresenter(ItemDetailView itemDetailView, ActivityItemDetailBinding binding, Context context){
        this.itemDetailView = itemDetailView;
        this.binding = binding;
        this.context = context;
    }

    @Override
    public void init(Long id) {
        itemId = id;
        binding.isheart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isHeart) { deleteHeartCallback(scrapId); }
                else { createHeartCallback(Constants.userId, itemId); }
            }
        });
    }

    @Override
    public void initializeImageData() {
        itemImageList = new ArrayList();
        itemDetailViewPagerAdapter = new ItemDetailViewPagerAdapter(context, itemImageList);
        binding.itemDetailViewPager.setAdapter(itemDetailViewPagerAdapter);
    }

    @Override
    public void qnaInit() {
        adapter = new qnaAdapter(context, qnaList);
        binding.itemDetailQnaList.setAdapter(adapter);

        qnaList.add(new qnaData("자전거 많이 무겁나요?", "2021.11.27", "hoa9***", false, true));

        binding.qaCount.setText("(" + String.valueOf(qnaList.size()-1) + ")");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void heartCheckCallback(Long userId, Long itemId) {
        RetrofitTool.getAPIWithAuthorizationToken(Constants.accessToken)
                .isCheckedHeart(ScrapCheckedRequest.of(userId, itemId))
                .enqueue(MainRetrofitTool.getCallback(new isCheckedHeartCallback()));
    }

    @Override
    public void deleteHeartCallback(Long scrapId) {
        RetrofitTool.getAPIWithAuthorizationToken(Constants.accessToken)
                .deleteHeart(scrapId)
                .enqueue(MainRetrofitTool.getCallback(new deleteScrapCallback()));
    }

    @Override
    public void createHeartCallback(Long userId, Long itemId) {
        RetrofitTool.getAPIWithAuthorizationToken(Constants.accessToken)
                .createScrap(ScrapRegisterRequest.of(userId, itemId))
                .enqueue(MainRetrofitTool.getCallback(new createScrapCallback()));
    }

    @Override
    public void getItemInfoCallback(Long itemId) {
        RetrofitTool.getAPIWithAuthorizationToken(Constants.accessToken).getMaximumPrice(itemId)
                .enqueue(MainRetrofitTool.getCallback(new getMaximumPriceCallback()));

        RetrofitTool.getAPIWithAuthorizationToken(Constants.accessToken).getParticipants(itemId)
                .enqueue(MainRetrofitTool.getCallback(new getParticipantsCallback()));

        RetrofitTool.getAPIWithAuthorizationToken(Constants.accessToken).getItem(itemId)
                .enqueue(MainRetrofitTool.getCallback(new getItemDetailsCallback()));
    }

    @Override
    public void getUserInfoCallback(Long userId) {
        RetrofitTool.getAPIWithAuthorizationToken(Constants.accessToken).userDetails(userId)
                .enqueue(MainRetrofitTool.getCallback(new getUserDetailsCallback()));
    }

    @Override
    public void deleteItem(Long itemId) {
        RetrofitTool.getAPIWithAuthorizationToken(Constants.accessToken).deleteItem(itemId)
                .enqueue(MainRetrofitTool.getCallback(new DeleteItemCallback()));
    }

    private class DeleteItemCallback implements MainRetrofitCallback<DefaultResponse> {
        @Override
        public void onSuccessResponse(Response<DefaultResponse> response) {
            Toast.makeText(context, ItemConstants.EItemServiceImpl.eDeleted.getValue(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.d(TAG, "retrofit success: ");
        }

        @Override
        public void onFailResponse(Response<DefaultResponse> response) throws IOException, JSONException {
            errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
            Log.d(TAG, "errorMessage"+errorMessageParser.getParsedErrorMessage());
            Log.d(TAG, "onFailResponse");
        }

        @Override
        public void onConnectionFail(Throwable t) {
            Log.e("연결실패", t.getMessage());
        }
    }

    private class deleteScrapCallback implements MainRetrofitCallback<DefaultResponse> {
        @Override
        public void onSuccessResponse(Response<DefaultResponse> response) {
            binding.isheart.setImageResource(R.drawable.heartx);
            isHeart = false;
            scrapId = null;
            Log.d(TAG, "retrofit success: ");
        }

        @Override
        public void onFailResponse(Response<DefaultResponse> response) throws IOException, JSONException {
            errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
            Log.d(TAG, "onFailResponse");
        }

        @Override
        public void onConnectionFail(Throwable t) {
            Log.e("연결실패", t.getMessage());
        }
    }

    private class createScrapCallback implements MainRetrofitCallback<ScrapRegisterResponse> {
        @Override
        public void onSuccessResponse(Response<ScrapRegisterResponse> response) {
            binding.isheart.setImageResource(R.drawable.hearto);
            isHeart = true;
            scrapId = response.body().getScrapId();
            Log.d(TAG, "retrofit success: ");
        }

        @Override
        public void onFailResponse(Response<ScrapRegisterResponse> response) throws IOException, JSONException {
            errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
            Log.d(TAG, "onFailResponse");
        }

        @Override
        public void onConnectionFail(Throwable t) {
            Log.e("연결실패", t.getMessage());
        }
    }

    private class isCheckedHeartCallback implements MainRetrofitCallback<ScrapCheckedResponse> {
        @Override
        public void onSuccessResponse(Response<ScrapCheckedResponse> response) {

            if(response.body().getScrapId()!=null){
                binding.isheart.setImageResource(R.drawable.hearto);
                isHeart = true;
                scrapId = response.body().getScrapId();
            } else{
                binding.isheart.setImageResource(R.drawable.heartx);
                isHeart = false;
                scrapId = null;
            }
            Log.d(TAG, "retrofit success: ");
        }

        @Override
        public void onFailResponse(Response<ScrapCheckedResponse> response) throws IOException, JSONException {
            errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
            Log.d(TAG, "onFailResponse");
        }

        @Override
        public void onConnectionFail(Throwable t) {
            Log.e("연결실패", t.getMessage());
        }
    }

    private class getMaximumPriceCallback implements MainRetrofitCallback<MaximumPriceResponse> {

        @Override
        public void onSuccessResponse(Response<MaximumPriceResponse> response) throws IOException {
            int maxPrice = response.body().getMaximumPrice();
            if(maxPrice == 0) {
                binding.highPrice.setText("ㅡ");
                binding.won.setVisibility(View.INVISIBLE);
            }
            else {
                DecimalFormat myFormatter = new DecimalFormat("###,###");
                binding.highPrice.setText(myFormatter.format(maxPrice));
                binding.won.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "retrofit success, idToken: " + response.body().toString());
        }
        @Override
        public void onFailResponse(Response<MaximumPriceResponse> response) throws IOException, JSONException {
            errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
            Log.d(TAG, "onFailResponse");
        }
        @Override
        public void onConnectionFail(Throwable t) {
            Log.e("연결실패", t.getMessage());
        }
    }

    private class getParticipantsCallback implements MainRetrofitCallback<ParticipantsResponse> {

        @Override
        public void onSuccessResponse(Response<ParticipantsResponse> response) {
            binding.participants.setText(String.valueOf(response.body().getParticipantsCount()));
            Log.d(TAG, "retrofit success, idToken: " + response.body().toString());
        }
        @Override
        public void onFailResponse(Response<ParticipantsResponse> response) throws IOException, JSONException {
            errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
            Log.d(TAG, "onFailResponse");
        }
        @Override
        public void onConnectionFail(Throwable t) {
            Log.e("연결실패", t.getMessage());
        }
    }

    private class getUserDetailsCallback implements MainRetrofitCallback<UserInfoResponse> {

        @Override
        public void onSuccessResponse(Response<UserInfoResponse> response) {
            if(response.body().getUserId() == 100) {
                binding.sellerName.setText("admin (관리자)");
            } else
                binding.sellerName.setText(response.body().getUsername());
            if(response.body().getPicture() != null){
                Glide.with(context).load(response.body().getPicture()).into(binding.sellerImage);
            } else {
                Glide.with(context).load(R.drawable.profile).into(binding.sellerImage);
            }
            //delete item
            Log.d(TAG, "retrofit success, idToken: " + response.body().toString());
        }
        @Override
        public void onFailResponse(Response<UserInfoResponse> response) throws IOException, JSONException {
            errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
            Log.d(TAG, "onFailResponse");
        }
        @Override
        public void onConnectionFail(Throwable t) {
            Log.e("연결실패", t.getMessage());
        }
    }

    // update time with handler and timertask
    Handler mHandler;
    Runnable mUpdateTimeTask;
    class MainTimerTask extends TimerTask {
        public void run() {
            mHandler.post(mUpdateTimeTask);
        }
    }

    public class getItemDetailsCallback implements MainRetrofitCallback<ItemDetailsResponse> {
        @Override
        public void onSuccessResponse(Response<ItemDetailsResponse> response) {
            getUserInfoCallback(response.body().getUserId());
            binding.itemName.setText(response.body().getItemName());
            binding.itemContent.setText(response.body().getDescription());
            binding.category.setText(response.body().getCategory().getName());
            if(!(response.body().getFileNames().isEmpty())){
                for (int i=0; i<response.body().getFileNames().size(); i++) {
                    itemImageList.add(response.body().getFileNames().get(i));
                }
            }
            // item image viewpager set
            itemDetailViewPagerAdapter = new ItemDetailViewPagerAdapter(context, itemImageList);
            binding.itemDetailViewPager.setAdapter(itemDetailViewPagerAdapter);
            // timer
            MainTimerTask timerTask = new MainTimerTask();
            Timer mTimer = new Timer();
            mTimer.schedule(timerTask, 500, 1000);
            //handler
            mHandler = new Handler();
            mUpdateTimeTask = new Runnable() {
                public void run() {
                    LocalDateTime startDateTime = LocalDateTime.now();
                    LocalDateTime endDateTime = response.body().getAuctionClosingDate();
                    String days = String.valueOf(ChronoUnit.DAYS.between(startDateTime, endDateTime));
                    String hours = String.valueOf(ChronoUnit.HOURS.between(startDateTime, endDateTime));
                    String minutes = String.valueOf(ChronoUnit.MINUTES.between(startDateTime, endDateTime)%60);
                    String second = String.valueOf(ChronoUnit.SECONDS.between(startDateTime, endDateTime)%60);

                    GetTime getTime = new GetTime(binding.itemLeftTime, days, hours, minutes, second, mTimer);
                }
            };

            if(response.body().getUserId().equals(myId)) {
                binding.deleteButton.setVisibility(View.VISIBLE);
                //button inactivated
                binding.participateButton.setEnabled(false);
                binding.participateButton.setBackgroundColor(Color.GRAY);
            }else {
                binding.deleteButton.setVisibility(View.GONE);
            }
            Log.d(TAG, "retrofit success, idToken: " + response.body().toString());
        }
        @Override
        public void onFailResponse(Response<ItemDetailsResponse> response) throws IOException, JSONException {
            errorMessageParser = new ErrorMessageParser(response.errorBody().string(), context);
            Log.d(TAG, "onFailResponse");
        }
        @Override
        public void onConnectionFail(Throwable t) {
            Log.e("연결실패", t.getMessage());
        }
    }
}
