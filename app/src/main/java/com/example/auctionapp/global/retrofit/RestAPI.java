package com.example.auctionapp.global.retrofit;

import com.example.auctionapp.domain.item.constant.ItemConstants;
import com.example.auctionapp.domain.item.dto.BestItemResponse;
import com.example.auctionapp.global.dto.DefaultResponse;
import com.example.auctionapp.domain.item.dto.GetAllItemsByStatusRequest;
import com.example.auctionapp.domain.item.dto.ItemDetailsResponse;
import com.example.auctionapp.domain.item.dto.RegisterItemResponse;
import com.example.auctionapp.domain.pricesuggestion.dto.BidderResponse;
import com.example.auctionapp.domain.pricesuggestion.dto.MaximumPriceResponse;
import com.example.auctionapp.domain.pricesuggestion.dto.ParticipantsResponse;
import com.example.auctionapp.domain.pricesuggestion.dto.PriceSuggestionListResponse;
import com.example.auctionapp.domain.scrap.dto.ScrapCheckedRequest;
import com.example.auctionapp.domain.scrap.dto.ScrapCheckedResponse;
import com.example.auctionapp.domain.scrap.dto.ScrapCountResponse;
import com.example.auctionapp.domain.scrap.dto.ScrapDetailsResponse;
import com.example.auctionapp.domain.scrap.dto.ScrapRegisterRequest;
import com.example.auctionapp.domain.scrap.dto.ScrapRegisterResponse;
import com.example.auctionapp.domain.user.dto.CheckAuthCodeRequest;
import com.example.auctionapp.domain.user.dto.EmailAuthCodeRequest;
import com.example.auctionapp.domain.user.dto.LoginRequest;
import com.example.auctionapp.domain.user.dto.OAuth2GoogleLoginRequest;
import com.example.auctionapp.domain.user.dto.LoginResponse;
import com.example.auctionapp.domain.user.dto.OAuth2KakaoLoginRequest;
import com.example.auctionapp.domain.user.dto.OAuth2NaverLoginRequest;
import com.example.auctionapp.domain.user.dto.SignUpRequest;
import com.example.auctionapp.domain.user.dto.SignUpResponse;
import com.example.auctionapp.domain.user.dto.UserInfoResponse;
import com.example.auctionapp.global.dto.PaginationDto;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RestAPI {

    @POST("api/v1/users")   //사용자 생성
    Call<SignUpResponse> signup(@Body SignUpRequest signUpRequest);
    @POST("api/v1/email/send")   //회원가입 시 이메일 인증
    Call<DefaultResponse> sendAuthCode(@Body EmailAuthCodeRequest emailAuthCodeRequest);
    @POST("api/v1/email/verify")   //인증코드 확인
    Call<DefaultResponse> checkAuthCode(@Body CheckAuthCodeRequest checkAuthCodeRequest);

    @POST("api/v1/users/login") //로그인
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @GET("api/v1/users/{id}")      //사용자 정보 조회
    Call<UserInfoResponse> userDetails(@Path("id") Long userId);
    @POST("api/v1/users/google-login")      //구글 로그인
    Call<LoginResponse> googleIdTokenValidation(@Body OAuth2GoogleLoginRequest oAuth2GoogleLoginRequest);
    @POST("api/v1/users/kakao-login")       //카카오 로그인
    Call<LoginResponse> kakaoAccessTokenValidation(@Body OAuth2KakaoLoginRequest oAuth2KakaoLoginRequest);
    @POST("api/v1/users/naver-login")       //네이버 로그인
    Call<LoginResponse> naverAccessTokenValidation(@Body OAuth2NaverLoginRequest oAuth2NaverLoginRequest);

    @Multipart
    @POST("api/v1/items")   //아이템 생성
    Call<RegisterItemResponse> uploadItem(@Part List<MultipartBody.Part> files,
                                          @Part("userId") RequestBody userId,
                                          @Part("itemName") RequestBody itemName,
                                          @Part("category") RequestBody category,
                                          @Part("initPrice") RequestBody initPrice,
                                          @Part("buyDate") RequestBody buyDate,
                                          @Part("itemStatePoint") RequestBody itemStatePoint,
                                          @Part("auctionClosingDate") RequestBody auctionClosingDate,
                                          @Part("description") RequestBody description);
    @GET("api/v1/items/{id}")   //아이템 조회
    Call<ItemDetailsResponse> getItem(@Path("id") Long id);
    @GET("api/v1/items/statuses/{itemSoldStatus}")     //아이템 판매상태별 조회
    Call<PaginationDto<List<ItemDetailsResponse>>> getAllItemsInfo(@Path("itemSoldStatus") ItemConstants.EItemSoldStatus itemSoldStatus);
    @GET("api/v1/items/statuses/hearts/{itemSoldStatus}")  //아이템 판매상태별, 스크랩 많은 순 조회
    Call<List<BestItemResponse>> getBestItems(@Path("itemSoldStatus") ItemConstants.EItemSoldStatus itemSoldStatus);
    @GET("api/v1/items/users")      //유저가 등록한 상품을 상태별로 조회
    Call<PaginationDto<List<ItemDetailsResponse>>> getAllItemsByUserIdAndStatus(@Body GetAllItemsByStatusRequest getAllItemsByStatusRequest);
    @DELETE("/api/v1/items/{id}")   //아이템 삭제
    Call<DefaultResponse> deleteItem(@Path("id") Long id);

    @POST("api/v1/scrap")   //scrap create
    Call<ScrapRegisterResponse> createScrap(@Body ScrapRegisterRequest scrapRegisterRequest);
    @DELETE("api/v1/scrap/{scrapId}")   //scrap delete
    Call<DefaultResponse> deleteHeart(@Path("scrapId") Long scrapId);
    @GET("api/v1/scrap/heart/{itemId}")     //get heart
    Call<ScrapCountResponse> getHeart(@Path("itemId") Long itemId);
    @POST("api/v1/scrap/heart/check")   //check scrap
    Call<ScrapCheckedResponse> isCheckedHeart(@Body ScrapCheckedRequest scrapCheckedRequest);
    @GET("api/v1/scrap/list/{userId}")      //list
    Call<PaginationDto<List<ScrapDetailsResponse>>> getAllScrapsByUserId(@Path("userId") Long userId);

    @GET("api/v1/priceSuggestion/bidder/{itemId}")      //getBidder
    Call<BidderResponse> getBidder(@Path("itemId") Long itemId);
    @GET("api/v1/priceSuggestion/list/item/{itemId}")   //find All by itemId
    Call<PaginationDto<List<PriceSuggestionListResponse>>> getAllPriceSuggestionByItemId(@Path("itemId") Long itemId);
    @GET("api/v1/priceSuggestion/list/user/{userId}")   //find All by userId
    Call<PaginationDto<List<PriceSuggestionListResponse>>> getAllPriceSuggestionByUserId(@Path("userId") Long userId);
    @GET("api/v1/priceSuggestion/maximumPrice/{itemId}")    //find maximum price
    Call<MaximumPriceResponse> getMaximumPrice(@Path("itemId") Long itemId);
    @GET("api/v1/priceSuggestion/participant/{itemId}")     //find participants
    Call<ParticipantsResponse> getParticipants(@Path("itemId") Long itemId);

}