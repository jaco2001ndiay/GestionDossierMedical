package com.medical.GestionDossierMedical.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @Column(unique = true, nullable = false)
    private String username ;
    @Column(nullable = false)
    private String password ;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles; // deux roles de meme nom ne peuvent exister

    public User() {
    }

    public User(String username, String password, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }



}
