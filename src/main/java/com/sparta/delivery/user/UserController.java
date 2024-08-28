package com.sparta.delivery.user;

import com.sparta.delivery.security.UserDetailsImpl;
import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.dto.UserInfoDto;
import com.sparta.delivery.user.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 회원 가입
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

    // 회원 정보 수정 (본인)
    @PutMapping("/users/me")
    public ResponseEntity<UserInfoDto> updateMyInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody UserRequestDto requestDto) {
        Long userId = userDetails.getUser().getId();
        UserInfoDto userInfoDto = userService.updateUser(userId, requestDto);
        return ResponseEntity.ok(userInfoDto);
    }

    // 회원 탈퇴 (본인)
    @DeleteMapping("/users/me")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        userService.deleteUser(userId);
        return ResponseEntity.ok("탈퇴가 완료되었습니다.");
    }





    // MASTER 권한

    // 모든 회원 조회
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    @GetMapping("/users/admin/all")
    public ResponseEntity<List<UserInfoDto>> getAllUserInfos() {
        List<UserInfoDto> userInfoDtoList = userService.getAllUserInfos();
        return new ResponseEntity<>(userInfoDtoList, HttpStatus.OK);
    }

    // 회원 정보 단건 조회
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    @GetMapping("/users/admin/{userId}")
    public ResponseEntity<UserInfoDto> getUserInfo(@PathVariable Long userId) {
        UserInfoDto userInfoDto = userService.getUserInfo(userId);
        return ResponseEntity.ok(userInfoDto);
    }

    // 회원 이름으로 검색
    @Secured({UserRoleEnum.Authority.MASTER, UserRoleEnum.Authority.MANAGER})
    @GetMapping("/users/admin/search")
    public ResponseEntity<Page<UserInfoDto>> searchUsers(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        Page<UserInfoDto> usersPage = userService.searchUsers(username, page, size, sortBy);
        return ResponseEntity.ok(usersPage);
    }



    // 관리자가 회원 탈퇴
    @Secured(UserRoleEnum.Authority.MASTER) // 관리자 권한만 허용
    @DeleteMapping("/users/admin/{userId}")
    public ResponseEntity<String> deactivateUser(@PathVariable Long userId,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long adminId = userDetails.getUser().getId();
        userService.deactivateUser(adminId, userId);
        return ResponseEntity.ok("탈퇴가 완료되었습니다.");
    }

}



