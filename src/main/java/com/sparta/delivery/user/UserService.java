package com.sparta.delivery.user;

import com.sparta.delivery.address.UserAddress;
import com.sparta.delivery.address.UserAddressRepository;
import com.sparta.delivery.address.UserAddressService;
import com.sparta.delivery.address.dto.UserAddressResponseDto;
import com.sparta.delivery.user.dto.SignupRequestDto;
import com.sparta.delivery.user.dto.UserInfoDto;
import com.sparta.delivery.user.dto.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final PasswordEncoder passwordEncoder;


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


    // 회원 탈퇴 메서드
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // 주소의 삭제 여부 설정
        user.getAddressList().forEach(address -> address.setDeleted(true));
        userAddressRepository.saveAll(user.getAddressList());

        // 삭제 필드 설정
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(getCurrentUser());

        // 사용자 비활성화
        user.setDeleted(true);
        userRepository.save(user);
    }

    // 회원 정보 수정 메서드
    @Transactional
    public UserInfoDto updateUser(Long userId, UserRequestDto updateRequestDto) {
        User user = findUserById(userId);

        // 수정할 필드가 있는 경우에만 업데이트합니다.
        if (updateRequestDto.getUsername() != null && !updateRequestDto.getUsername().isEmpty()) {
            user.setUsername(updateRequestDto.getUsername());
        }

        if (updateRequestDto.getPassword() != null && !updateRequestDto.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updateRequestDto.getPassword());
            user.setPassword(encodedPassword);
        }

        User upadtedUser = userRepository.save(user);

        return convertToUserInfoDto(upadtedUser);
    }



    // MASTER

    // 전체 사용자 조회
    public List<UserInfoDto> getAllUserInfos() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(this::convertToUserInfoDto)
                .collect(Collectors.toList());
    }


    // 유저 이름으로 검색하고 정렬 및 페이징 처리
    public Page<UserInfoDto> searchUsers(String username, int page, int size, String sortBy) {
        // 기본 정렬은 생성일 순으로 하고, 수정일 순으로 변경 가능
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy.equals("updatedAt") ? "updatedAt" : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        // 검색과 페이징 처리된 결과
        Page<User> usersPage = userRepository.findByUsernameContainingIgnoreCase(username, pageable);

        // DTO 변환 및 반환
        return usersPage.map(this::convertToUserInfoDto);
    }


    // 관리자가 사용자 비활성화 메서드
    public void deactivateUser(Long adminId, Long userId) {
        // 관리자 권한 확인 로직 추가
        if (!isAdmin(adminId)) {
            throw new AccessDeniedException("Only admins can deactivate users");
        }

        User user = findUserById(userId);
        // 주소의 삭제 여부 설정
        user.getAddressList().forEach(address -> address.setDeleted(true));
        userAddressRepository.saveAll(user.getAddressList());

        // 삭제 필드 설정
        user.setDeletedAt(LocalDateTime.now());
        user.setDeletedBy(getCurrentUser());

        // 사용자 비활성화
        user.setDeleted(true);
        userRepository.save(user);
    }





    // 공용 메서드

    // 아이디로 유저 조회
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found ID : " + userId));
    }

    // 엔티티 -> dto 변환
    public UserInfoDto convertToUserInfoDto(User user) {
        List<UserAddressResponseDto> addressList = getAddressesForUser(user.getId());
        return new UserInfoDto(user.getId(), user.getUsername(), user.getRole(), addressList);
    }

    private List<UserAddressResponseDto> getAddressesForUser(Long userId) {
        List<UserAddress> userAddressList = userAddressRepository.findByUserIdAndIsDeletedFalse(userId);
        return userAddressList.stream()
                .map(this::convertToAddressDto)
                .collect(Collectors.toList());
    }

    private UserAddressResponseDto convertToAddressDto(UserAddress userAddress) {
        return new UserAddressResponseDto(
                userAddress.getId(),
                userAddress.getAddressName(),
                userAddress.getLine1(),
                userAddress.getLine2()
        );
    }

    // 관리자 여부 확인
    private boolean isAdmin(Long adminId) {
        User admin = userRepository.findById(adminId).orElseThrow(() -> new IllegalArgumentException("Admin not found"));
        return admin.getRole() == UserRoleEnum.MASTER; // 예시로 MASTER 권한 체크
    }


    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "unknown";
        }
        return authentication.getName();
    }
}