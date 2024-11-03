package com.nexus.GYMPULSE.model.person;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document(collection = "trainers")
@Data
@EqualsAndHashCode(callSuper =  true)
@NoArgsConstructor
@AllArgsConstructor
public class Trainer extends Person {
    @Id
    private String trainerId;
    private String speciality;
    private Double salary;
    private String certificationNumber;
    public Trainer(String trainerId, String speciality, Double salary, String certificationNumber, String fullName, 
    String phoneNumber, String address, String email) {
        super(fullName, email, address, phoneNumber);
        this.trainerId = trainerId;
        this.speciality = speciality;
        this.salary = salary;
        this.certificationNumber = certificationNumber;
    }
    public Trainer(String speciality, Double salary, String certificationNumber, String fullName, 
    String phoneNumber, String address, String email) {
        super(fullName, email, address, phoneNumber);
        this.certificationNumber = certificationNumber;
        this.salary = salary;
        this.speciality = speciality;
    }
    
}
