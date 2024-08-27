package com.sparta.delivery.product.ai;

import com.sparta.delivery.common.TimeStamped;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "p_ai")
public class Ai{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(length = 200) // 여기서 길이를 1000자로 설정 (필요에 따라 변경)
    private String requestText;

    @Column(length = 1000) // 여기서 길이를 1000자로 설정 (필요에 따라 변경)
    private String responseText;

}

