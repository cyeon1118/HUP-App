package com.example.auctionapp.domain.user.dto;

import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    @NotEmpty(message = "회원의 유저 Id를 입력해주세요.")
    private Long userId;

    @NotEmpty(message = "수정하고자 하는 로그인 Id를 입력해주세요.")
    @Length(min = 5, max = 11, message = "수정하고자 하는 로그인 Id는 크기가 5에서 11사이여야 합니다.")
    private String loginId;

    @NotEmpty(message = "수정하고자 하는 이름을 입력해 주세요.")
    @Length(min = 2, max = 10, message = "수정하고자 하는 이름은 2글자 이상 10글자 이하여야 합니다.")
    private String username;

    @NotEmpty(message = "수정하고자 하는 비밀번호를 입력해 주세요.")
    @Length(min = 8, max = 16, message = "수정하고자 하는 패스워드는 8글자 이상 16글자 이하여야 합니다.")
    private String password;

    @NotEmpty(message = "수정하고자 하는 전화번호를 입력해주세요.")
    private String phoneNumber;
}
