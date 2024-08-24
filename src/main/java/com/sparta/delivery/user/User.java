package com.sparta.delivery.user;

import com.sparta.delivery.address.UserAddress;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // username은  최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0~9)로 구성
    @Size(min = 4, max = 10, message = "Username must be between 4 and 10 characters.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "Username must contain only lowercase letters and numbers.")
    @Column(nullable = false, unique = true)
    private String username;

    // password는  최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자
    @Size(min = 8, max = 15, message = "Password must be between 8 and 15 characters.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character.")
    @Column(nullable = false)
    private String password;

    // 사용자 권한 (CUSTOMER, OWNER, MANAGER, MASTER)
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;



    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddress> addressList = new ArrayList<>();

}