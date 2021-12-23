package com.example.oauth.entry;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(indexes = @Index(columnList = "email"))
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String email;
    @Column
    private String name;
    @Column
    private String password;

    public User() {
    }
}