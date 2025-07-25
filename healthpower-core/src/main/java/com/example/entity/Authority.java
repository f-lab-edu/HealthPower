
package com.example.entity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Authority {

    @Column(name = "user_id")
    private String userId;

    private String authority;
}

