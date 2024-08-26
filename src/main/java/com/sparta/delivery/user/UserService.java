package com.sparta.delivery.user;

import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    // 아이디로 유저 조회
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found ID : " + userId));
    }


    // 회원 가입
    public void signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 아이디 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = requestDto.getRole();


        // 사용자 등록
        User user = new User(username, password, role);
        userRepository.save(user);
    }


    // 사용자 정보 조회
    public UserInfoDto getUserInfo(Long userId) {
        User user = findUserById(userId);
        return convertToUserInfoDto(user);
    }

    // 전체 사용자 조회
    public List<UserInfoDto> getAllUserInfos() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(this::convertToUserInfoDto)
                .collect(Collectors.toList());
    }




    // 엔티티 -> dto 변환
    public UserInfoDto convertToUserInfoDto(User user) {
        return new UserInfoDto(user.getUsername(), user.getRole());
    }

}