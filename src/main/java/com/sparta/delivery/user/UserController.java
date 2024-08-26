package com.sparta.delivery.user;

import com.sparta.delivery.security.UserDetailsImpl;
import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/users/signup")
    public String signUp(@RequestBody SignupRequestDto signupRequestDto) {
        userService.signup(signupRequestDto);
        return "회원가입 완료";
    }

    // 회원 정보 단건 조회(본인)
    @GetMapping("/users/me")
    public ResponseEntity<UserInfoDto> getMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserInfoDto userInfoDto = userService.getUserInfo(userDetails.getUser().getId());
        return ResponseEntity.ok(userInfoDto);
    }


    // MASTER 권한

    // 모든 회원 정보 조회
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    @GetMapping("/users")
    public ResponseEntity<List<UserInfoDto>> getAllUserInfos() {
        List<UserInfoDto> userInfoDtoList = userService.getAllUserInfos();
        return new ResponseEntity<>(userInfoDtoList, HttpStatus.OK);
    }

}



