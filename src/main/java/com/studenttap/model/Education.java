


package com.studenttap.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "education")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Values: "10TH", "INTERMEDIATE", "DEGREE", "PG", "DIPLOMA"
    @Column(name = "education_type", nullable = false, length = 50)
    private String educationType;

    @Column(name = "institution_name", nullable = false)
    private String institutionName;

    @Column(name = "board_university")
    private String boardUniversity;

    @Column(name = "field_of_study", length = 150)
    private String fieldOfStudy;

    @Column(name = "percentage_cgpa", length = 20)
    private String percentageCgpa;

    @Column(name = "year_of_passing", length = 10)
    private String yearOfPassing;

    @Column(name = "display_order")
    private Integer displayOrder = 0;
}