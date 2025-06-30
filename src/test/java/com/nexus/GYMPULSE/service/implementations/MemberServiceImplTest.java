package com.nexus.GYMPULSE.service.implementations;

import com.nexus.GYMPULSE.exception.MaxMemberLimitReachedException;
import com.nexus.GYMPULSE.exception.MemberNotFoundException;
import com.nexus.GYMPULSE.model.person.Member;
import com.nexus.GYMPULSE.model.workoutplan.WorkoutPlan;
import com.nexus.GYMPULSE.repositories.MemberRepository;
import com.nexus.GYMPULSE.requests.MemberRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member1;
    private Member member2;
    private MemberRequest memberRequest;

    @BeforeEach
    void setUp() {
        // Initialize SLF4J logger for the service, if needed for tests (e.g. to verify log messages)
        // ReflectionTestUtils.setField(memberService, "logger", LoggerFactory.getLogger(MemberServiceImpl.class));


        WorkoutPlan workoutPlan = new WorkoutPlan("wp001", "m001", "t001", "2024-01-01", "2024-02-01", Collections.emptyList());
        member1 = new Member("John Doe", "john.doe@example.com", "123 Main St", "1234567890",
                "0001", "Premium", "2024-01-01", "2025-01-01", workoutPlan);
        member2 = new Member("Jane Smith", "jane.smith@example.com", "456 Oak St", "0987654321",
                "0002", "Basic", "2024-02-01", "2025-02-01", null);

        memberRequest = new MemberRequest();
        memberRequest.setFullName("Test User");
        memberRequest.setEmail("test.user@example.com");
        memberRequest.setAddress("789 Pine St");
        memberRequest.setPhoneNumber("1122334455");
        memberRequest.setMemberShipType("Gold");
        memberRequest.setStartDate("2024-03-01");
        memberRequest.setEndDate("2025-03-01");
        memberRequest.setWorkoutPlan(workoutPlan);
    }

    @Test
    void createMember_success() {
        // Mock generateNextMemberId to return a valid ID
        // This is tricky because generateNextMemberId is private and calls allMembers()
        // We can mock allMembers() to control the used IDs
        when(memberRepository.findAll()).thenReturn(Collections.emptyList()); // No existing members
        when(memberRepository.insert(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Member createdMember = memberService.createMember(memberRequest);

        assertNotNull(createdMember);
        assertEquals("Test User", createdMember.getFullName());
        assertEquals("0001", createdMember.getMemberId()); // Expecting the first generated ID
        verify(memberRepository, times(1)).insert(any(Member.class));
    }

    @Test
    void createMember_maxLimitReached() {
        // Mock generateNextMemberId to simulate no available IDs
        // This requires mocking allMembers() to return a list of 9999 members
        List<Member> fullMemberList = new java.util.ArrayList<>();
        for (int i = 1; i <= 9999; i++) {
            fullMemberList.add(new Member(null, null, null, null, String.format("%04d", i), null, null, null, null));
        }
        when(memberRepository.findAll()).thenReturn(fullMemberList);

        Exception exception = assertThrows(MaxMemberLimitReachedException.class, () -> {
            memberService.createMember(memberRequest);
        });

        assertEquals("Member limit reached (max 9999). Cannot create more members.", exception.getMessage());
        verify(memberRepository, never()).insert(any(Member.class));
    }

    @Test
    void allMembers_success() {
        when(memberRepository.findAll()).thenReturn(Arrays.asList(member1, member2));
        List<Member> members = memberService.allMembers();
        assertEquals(2, members.size());
        verify(memberRepository, times(1)).findAll();
    }

    @Test
    void memberById_found() {
        when(memberRepository.findByMemberId("0001")).thenReturn(Optional.of(member1));
        Optional<Member> foundMember = memberService.memberById("0001");
        assertTrue(foundMember.isPresent());
        assertEquals("John Doe", foundMember.get().getFullName());
    }

    @Test
    void memberById_notFound() {
        when(memberRepository.findByMemberId("9999")).thenReturn(Optional.empty());
        Optional<Member> foundMember = memberService.memberById("9999");
        assertFalse(foundMember.isPresent());
    }

    @Test
    void updateMember_success() {
        when(memberRepository.findByMemberId("0001")).thenReturn(Optional.of(member1));
        when(memberRepository.save(any(Member.class))).thenReturn(member1);

        memberRequest.setFullName("Johnathan Doe Updated");
        Member updatedMember = memberService.updateMember("0001", memberRequest);

        assertNotNull(updatedMember);
        assertEquals("Johnathan Doe Updated", updatedMember.getFullName());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void updateMember_notFound() {
        when(memberRepository.findByMemberId("9999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(MemberNotFoundException.class, () -> {
            memberService.updateMember("9999", memberRequest);
        });
        assertEquals("Member not found for ID: 9999", exception.getMessage());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void deleteByMemberId_success() {
        when(memberRepository.findByMemberId("0001")).thenReturn(Optional.of(member1));
        doNothing().when(memberRepository).delete(member1);

        memberService.deleteByMemberId("0001");

        verify(memberRepository, times(1)).delete(member1);
    }

    @Test
    void deleteByMemberId_notFound() {
        when(memberRepository.findByMemberId("9999")).thenReturn(Optional.empty());

        Exception exception = assertThrows(MemberNotFoundException.class, () -> {
            memberService.deleteByMemberId("9999");
        });
        assertEquals("Member not found for ID: 9999", exception.getMessage());
        verify(memberRepository, never()).delete(any(Member.class));
    }

    // Test generateNextMemberId indirectly via createMember or make it package-private/use ReflectionTestUtils if direct test is crucial
    // For now, createMember_success and createMember_maxLimitReached cover its main paths.
}
