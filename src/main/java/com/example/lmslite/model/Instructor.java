package com.example.lmslite.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "instructors")
public class Instructor extends User {

    private String bio;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Course> courses;
}
