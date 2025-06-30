package com.nexus.GYMPULSE.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nexus.GYMPULSE.model.person.Member;
// import com.nexus.GYMPULSE.model.workoutplan.WorkoutPlan; // Not directly used in controller methods after refactor
import com.nexus.GYMPULSE.requests.MemberRequest;
import com.nexus.GYMPULSE.service.interfaces.MemberService;

import jakarta.validation.Valid; // Import @Valid

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/Members") // Base URL for member-related endpoints
public class MemberController {

    @Autowired
    private MemberService memberService; // Injecting the MemberService to handle business logic

    // Endpoint to retrieve all members
    @GetMapping
    public ResponseEntity<List<Member>> getAllMembers() {
        return new ResponseEntity<List<Member>>(memberService.allMembers(), HttpStatus.OK); // Return list of members with OK status
    }

    // Endpoint to create a new member
    @PostMapping
    public Member createMember(@Valid @RequestBody MemberRequest memberRequest) { // Added @Valid
        // The service method will now directly accept MemberRequest
        return memberService.createMember(memberRequest);
    }

    // Endpoint to retrieve a specific member by their ID
    @GetMapping("/{memberId}")
    public ResponseEntity<Member> getMemberById(@PathVariable String memberId) {
        Member member = memberService.memberById(memberId)
                .orElseThrow(() -> new com.nexus.GYMPULSE.exception.MemberNotFoundException("Member not found with ID: " + memberId));
        return ResponseEntity.ok(member);
    }

    // Endpoint to update an existing member's details by their ID
    @PutMapping("/{memberId}")
    public Member updateMember(@PathVariable String memberId, @Valid @RequestBody MemberRequest memberRequest) { // Added @Valid
        return memberService.updateMember(memberId, memberRequest); // Update and return the modified member
    }

    // Endpoint to delete a member by their ID
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable String memberId) {
        memberService.deleteByMemberId(memberId); // Delete the member
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Return no content response
    }
}
