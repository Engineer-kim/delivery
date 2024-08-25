package com.sparta.delivery.user;

import com.sparta.delivery.user.dto.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

}



