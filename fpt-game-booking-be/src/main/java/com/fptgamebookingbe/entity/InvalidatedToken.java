package com.fptgamebookingbe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "invalidated_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvalidatedToken {

    @Id
    @Column(name = "id", length = 255)
    private String id;

    @Column(name = "expiry_time", nullable = false)
    private Date expiryTime;
}
