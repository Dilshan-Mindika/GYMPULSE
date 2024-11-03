package com.nexus.GYMPULSE.controllers;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.GYMPULSE.model.person.Member;
import com.nexus.GYMPULSE.model.workoutplan.WorkoutPlan;
import com.nexus.GYMPULSE.requests.MemberRequest;
import com.nexus.GYMPULSE.service.interfaces.MemberService;

@RestController
@RequestMapping("/Members")
public class MemberController {
    @Autowired
    private MemberService memberService;    
    
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        return new ResponseEntity<List<Member>>(memberService.allMembers(), HttpStatus.OK);
    }
    @PostMapping
    public Member createMember(@RequestBody MemberRequest memberRequest) {
        String fullName = memberRequest.getFullName();
        String email = memberRequest.getEmail();
        String address = memberRequest.getAddress();
        String phoneNumber = memberRequest.getPhoneNumber();        
        String memberShipType = memberRequest.getMemberShipType();
        String startDate = memberRequest.getStartDate();
        String endDate = memberRequest.getEndDate();
        WorkoutPlan workoutPlan = memberRequest.getWorkoutPlan();
        return memberService.createMember(fullName, email, address, phoneNumber, memberShipType, startDate, endDate, workoutPlan);
    }
    @GetMapping("/{memberId}")
    public ResponseEntity<Optional<Member>> getMemberById(@PathVariable String memberId) {
        return new ResponseEntity<Optional<Member>>(memberService.memberById(memberId), HttpStatus.OK);
    }
    @PutMapping("/{memberId}")
    public Member updateMember(@PathVariable String memberId, @RequestBody MemberRequest memberRequest) {
        return memberService.updateMember(memberId, memberRequest);
    }
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable String memberId) {
        memberService.deleteByMemberId(memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
