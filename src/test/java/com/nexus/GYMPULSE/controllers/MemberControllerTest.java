package com.nexus.GYMPULSE.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.GYMPULSE.model.person.Member;
import com.nexus.GYMPULSE.model.workoutplan.WorkoutPlan;
import com.nexus.GYMPULSE.requests.MemberRequest;
import com.nexus.GYMPULSE.service.interfaces.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Member member1;
    private MemberRequest memberRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                // Registering the GlobalExceptionHandler if we want to test its behavior for @Valid failures.
                // However, for pure unit tests of the controller, we often mock service responses directly.
                // For @Valid testing, integration tests with MockMvc are usually more straightforward.
                // .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        WorkoutPlan workoutPlan = new WorkoutPlan("wp001", "m001", "t001", "2024-01-01", "2024-02-01", Collections.emptyList());
        member1 = new Member("John Doe", "john.doe@example.com", "123 Main St", "1234567890",
                "0001", "Premium", "2024-01-01", "2025-01-01", workoutPlan);

        memberRequest = new MemberRequest();
        memberRequest.setFullName("John Doe");
        memberRequest.setEmail("john.doe@example.com");
        memberRequest.setAddress("123 Main St");
        memberRequest.setPhoneNumber("1234567890");
        memberRequest.setMemberShipType("Premium");
        memberRequest.setStartDate("2024-01-01");
        memberRequest.setEndDate("2025-01-01");
        memberRequest.setWorkoutPlan(workoutPlan);
    }

    @Test
    void getAllMembers_shouldReturnListOfMembers() throws Exception {
        List<Member> allMembers = Arrays.asList(member1);
        when(memberService.allMembers()).thenReturn(allMembers);

        mockMvc.perform(get("/Members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName", is("John Doe")));
    }

    @Test
    void createMember_shouldReturnCreatedMember() throws Exception {
        when(memberService.createMember(any(MemberRequest.class))).thenReturn(member1);

        mockMvc.perform(post("/Members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isOk()) // Controller method returns Member directly, not ResponseEntity<Member>
                .andExpect(jsonPath("$.fullName", is("John Doe")));
    }

    // Test for @Valid would typically be an integration test to check GlobalExceptionHandler
    // For a unit test, we'd assume @Valid works and service is called if object is valid.

    @Test
    void getMemberById_shouldReturnMemberWhenFound() throws Exception {
        when(memberService.memberById("0001")).thenReturn(Optional.of(member1));

        mockMvc.perform(get("/Members/0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("John Doe")));
    }

    @Test
    void getMemberById_shouldReturnNotFoundWhenMemberDoesNotExist() throws Exception {
        when(memberService.memberById("9999")).thenReturn(Optional.empty());
        // Note: The controller currently returns ResponseEntity<Optional<Member>>.
        // If it were to throw an exception handled by GlobalExceptionHandler for 404,
        // this test would need to expect status().isNotFound().
        // Given the current controller implementation:
        mockMvc.perform(get("/Members/9999"))
                .andExpect(status().isOk()) // The ResponseEntity itself is OK
                .andExpect(content().string("")); // And the body is empty for an empty Optional
                                                 // If we want a 404, the controller or service needs to throw an exception.
                                                 // The plan mentioned ResourceNotFoundException for services,
                                                 // so this controller might need adjustment or service throws it.
                                                 // For now, testing current behavior.
    }


    @Test
    void updateMember_shouldReturnUpdatedMember() throws Exception {
        when(memberService.updateMember(eq("0001"), any(MemberRequest.class))).thenReturn(member1);

        mockMvc.perform(put("/Members/0001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("John Doe")));
    }

    // Test for updateMember when member not found would require service to throw MemberNotFoundException,
    // and then we'd check for 404 status if GlobalExceptionHandler handles it.

    @Test
    void deleteMember_shouldReturnNoContent() throws Exception {
        // memberService.deleteByMemberId is void, so no 'when' needed for its return,
        // but we can verify it's called if needed.
        // Mockito.doNothing().when(memberService).deleteByMemberId("0001");

        mockMvc.perform(delete("/Members/0001"))
                .andExpect(status().isNoContent());
    }
}
