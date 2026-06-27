package com.devsu.fintech.banking_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "persons")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Gender is mandatory")
    @Pattern(regexp = "M|F|O", message = "Gender must be M, F, or O")
    @Column(nullable = false, length = 1)
    private String gender;

    @NotNull(message = "Age is mandatory")
    @Min(value = 0, message = "Age must be positive")
    @Max(value = 150, message = "Age must be less than or equal to 150")
    @Column(nullable = false)
    private Integer age;

    @NotBlank(message = "Identification is mandatory")
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String identification;

    @NotBlank(message = "Address is mandatory")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String address;

    @NotBlank(message = "Phone is mandatory")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String phone;

}
