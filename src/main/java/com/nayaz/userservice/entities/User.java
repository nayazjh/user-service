package com.nayaz.userservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_info")
public class User {

    @Id
    @Column(name = "ID")
    private  String userId;
    @Column(name = "NAME", length = 20)
    private String name;
    @Column(name = "Email", unique = true)
    private  String email;
    @Column(name = "about")
    private String about;

    @Transient
    private List<Rating> ratings = new ArrayList<>();
}
