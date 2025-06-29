package com.nexus.GYMPULSE.service.implementations;

import java.util.List;
// import java.util.NoSuchElementException; // No longer needed
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.GYMPULSE.exception.MaxMemberLimitReachedException; // Import new exception
import com.nexus.GYMPULSE.exception.MemberNotFoundException; // Import new exception
import com.nexus.GYMPULSE.model.person.Member;
// import com.nexus.GYMPULSE.model.workoutplan.WorkoutPlan; // Not directly used here
import com.nexus.GYMPULSE.repositories.MemberRepository;
import com.nexus.GYMPULSE.requests.MemberRequest;
import com.nexus.GYMPULSE.service.interfaces.MemberService;
// import com.nexus.GYMPULSE.utils.GymLogger; // Will be removed
import org.slf4j.Logger; // Import SLF4J Logger
import org.slf4j.LoggerFactory; // Import SLF4J LoggerFactory

@Service
public class MemberServiceImpl implements MemberService {
    // Replace GymLogger with SLF4J Logger
    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Member createMember(MemberRequest memberRequest) {
        // Generate a unique member ID
        String memberId = generateNextMemberId();
        if (memberId != null) {
            // Create and save the new Member
            Member member = new Member(
                    memberRequest.getFullName(),
                    memberRequest.getEmail(),
                    memberRequest.getAddress(),
                    memberRequest.getPhoneNumber(),
                    memberId, // Generated ID
                    memberRequest.getMemberShipType(),
                    memberRequest.getStartDate(),
                    memberRequest.getEndDate(),
                    memberRequest.getWorkoutPlan()
            );
            memberRepository.insert(member);
            logger.info("New Member added, Member ID: {}", memberId); // Changed to SLF4J style
            return member;
        } else {
            // Throw custom exception for member limit
            throw new MaxMemberLimitReachedException("Member limit reached (max 9999). Cannot create more members.");
        }
    }

    @Override
    public List<Member> allMembers() {
        // Retrieve all members from the repository
        return memberRepository.findAll();
    }

    // Removed findMemberById as it's redundant with memberById
    // @Override
    // public Optional<Member> findMemberById(String memberId) {
    //     return memberRepository.findByMemberId(memberId);
    // }

    @Override
    public Optional<Member> memberById(String memberId) {
        // Find a member by their member ID
        return memberRepository.findByMemberId(memberId);
    }

    @Override
    public Member updateMember(String memberId, MemberRequest memberRequest) {
        // Update an existing Member's details
        // Find the member or throw MemberNotFoundException
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found for ID: " + memberId));

        member.setAddress(memberRequest.getAddress());
        member.setEmail(memberRequest.getEmail());
        member.setEndDate(memberRequest.getEndDate());
        member.setFullName(memberRequest.getFullName());
        member.setMemberShipType(memberRequest.getMemberShipType());
        member.setPhoneNumber(memberRequest.getPhoneNumber());
        member.setStartDate(memberRequest.getStartDate());
        // Assuming workout plan update is handled like this, if not, this needs adjustment
        member.setWorkoutPlan(memberRequest.getWorkoutPlan());
        logger.info("Member updated, ID: {}", memberId); // Changed to SLF4J style
        return memberRepository.save(member);
    }

    @Override
    public void deleteByMemberId(String memberId) {
        // Delete a member by their member ID
        // Find the member or throw MemberNotFoundException
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found for ID: " + memberId));

        logger.info("Member deleted, ID: {}", memberId); // Changed to SLF4J style
        memberRepository.delete(member);
    }

    private String generateNextMemberId() {
        // Generate the next unique member ID.
        // Current implementation iterates from 1 to 9999.
        // LIMITATIONS:
        // 1. Performance: This can be inefficient if the number of members is large,
        //    as it fetches all members and iterates.
        // 2. Scalability: Hardcoded limit of 9999 members.
        // 3. Concurrency: Not safe for concurrent requests trying to create members,
        //    as multiple requests might get the same ID before one is saved.
        // For a production system, consider using MongoDB's ObjectIds, a dedicated
        // sequence generator, or a UUID-based approach if the 4-digit numeric ID
        // is not a strict business requirement.
        List<Member> members = allMembers();
        Set<String> usedIds = members.stream().map(Member::getMemberId).collect(Collectors.toSet());

        for (int i = 1; i <= 9999; i++) {
            String candidateId = String.format("%04d", i);
            if (!usedIds.contains(candidateId)) {
                return candidateId; // Return the first unused ID
            }
        }
        return null; // No available ID found
    }
}
