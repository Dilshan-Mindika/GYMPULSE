package com.nexus.GYMPULSE.model.person;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import com.nexus.GYMPULSE.model.workoutplan.WorkoutPlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Document(collection = "members")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Member extends Person {
    @Id
    private String memberId;
    private String memberShipType;
    private String startDate;
    private String endDate;
    @DocumentReference
    private WorkoutPlan workoutPlan;
    
    public Member(String fullName, String email, String address, String phoneNumber, String memberId,
            String memberShipType, String startString, String endString, WorkoutPlan workoutPlan) {
        super(fullName, email, address, phoneNumber);
        this.memberId = memberId;
        this.memberShipType = memberShipType;
        this.startDate = startString;
        this.endDate = endString;
        this.workoutPlan = workoutPlan;
    }
    public Member(String fullName, String email, String address, String phoneNumber, String memberId,
        String memberShipType, String startString, String endString) {
    super(fullName, email, address, phoneNumber);
    this.memberId = memberId;
    this.memberShipType = memberShipType;
    this.startDate = startString;
    this.endDate = endString;
    this.workoutPlan = null;
    }    
}
