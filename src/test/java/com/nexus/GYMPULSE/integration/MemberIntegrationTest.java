package com.nexus.GYMPULSE.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexus.GYMPULSE.model.person.Member;
import com.nexus.GYMPULSE.model.workoutplan.WorkoutPlan;
import com.nexus.GYMPULSE.repositories.MemberRepository;
import com.nexus.GYMPULSE.requests.MemberRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Assuming a test profile can be used for specific test configurations (e.g., in-memory DB)
class MemberIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    private MemberRequest validMemberRequest;
    private WorkoutPlan sharedWorkoutPlan;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll(); // Clean up before each test

        sharedWorkoutPlan = new WorkoutPlan("wpTest001", "mTest001", "tTest001", "2024-01-01", "2024-12-31", Collections.emptyList());
        // Note: WorkoutPlan might need to be saved if it's a separate entity that Member references by ID from another collection.
        // For simplicity here, assuming it's embedded or its ID doesn't require prior DB existence for this test flow.

        validMemberRequest = new MemberRequest();
        validMemberRequest.setFullName("Integration Test User");
        validMemberRequest.setEmail("integration.test@example.com");
        validMemberRequest.setAddress("100 Integration Test Ave");
        validMemberRequest.setPhoneNumber("1231231234");
        validMemberRequest.setMemberShipType("Platinum");
        validMemberRequest.setStartDate("2024-01-01");
        validMemberRequest.setEndDate("2025-01-01");
        validMemberRequest.setWorkoutPlan(sharedWorkoutPlan); // Or null if not required
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAll(); // Clean up after each test
    }

    @Test
    void createMember_withValidData_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/Members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMemberRequest)))
                .andExpect(status().isCreated()) // Expect 201 Created
                .andExpect(jsonPath("$.fullName", is(validMemberRequest.getFullName())))
                .andExpect(jsonPath("$.email", is(validMemberRequest.getEmail())))
                .andExpect(jsonPath("$.memberId", matchesPattern("\\d{4}"))); // Check if memberId is a 4-digit string
    }

    @Test
    void createMember_withInvalidData_shouldReturnBadRequest() throws Exception {
        MemberRequest invalidRequest = new MemberRequest(); // Missing required fields
        invalidRequest.setEmail("notanemail"); // Invalid email

        mockMvc.perform(post("/Members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()) // Expect 400 Bad Request
                .andExpect(jsonPath("$.errors", hasItem("Full name cannot be blank")))
                .andExpect(jsonPath("$.errors", hasItem("Email should be valid")))
                .andExpect(jsonPath("$.errors", hasItem("Address cannot be blank")));
    }

    @Test
    void getMemberById_whenMemberExists_shouldReturnMember() throws Exception {
        // First, create a member to fetch
        Member member = new Member(
                "Fetch Test", "fetch@example.com", "Fetch Address", "12345",
                "9999", "TestType", "2024-01-01", "2025-01-01", null
        );
        member = memberRepository.save(member); // Save and get the actual ID if it's DB generated for the main _id

        mockMvc.perform(get("/Members/" + member.getMemberId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Fetch Test")))
                .andExpect(jsonPath("$.memberId", is(member.getMemberId())));
    }

    @Test
    void getMemberById_whenMemberDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/Members/nonExistentId"))
                .andExpect(status().isNotFound()); // Expect 404 Not Found
    }

    @Test
    void getAllMembers_shouldReturnListOfMembers() throws Exception {
        memberRepository.save(new Member("User One", "one@example.com", "Addr1", "1", "0001", "T1", "d1", "d2", null));
        memberRepository.save(new Member("User Two", "two@example.com", "Addr2", "2", "0002", "T2", "d3", "d4", null));

        mockMvc.perform(get("/Members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].memberId", is("0001")))
                .andExpect(jsonPath("$[1].memberId", is("0002")));
    }


    @Test
    void updateMember_whenMemberExistsAndDataIsValid_shouldReturnOk() throws Exception {
        Member existingMember = new Member("Original Name", "original@example.com", "Original Address", "123",
                                           "0001", "OriginalType", "2023-01-01", "2024-01-01", null);
        memberRepository.save(existingMember);

        validMemberRequest.setFullName("Updated Integration Test User");

        mockMvc.perform(put("/Members/" + existingMember.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMemberRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Updated Integration Test User")))
                .andExpect(jsonPath("$.memberId", is(existingMember.getMemberId())));

        // Verify in DB
        Optional<Member> updatedInDb = memberRepository.findByMemberId(existingMember.getMemberId());
        assertTrue(updatedInDb.isPresent());
        assertEquals("Updated Integration Test User", updatedInDb.get().getFullName());
    }

    @Test
    void updateMember_whenMemberDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(put("/Members/nonExistentId00")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validMemberRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMember_withInvalidData_shouldReturnBadRequest() throws Exception {
        Member existingMember = new Member("Original Name", "original@example.com", "Original Address", "123",
                                           "0001", "OriginalType", "2023-01-01", "2024-01-01", null);
        memberRepository.save(existingMember);

        MemberRequest invalidUpdateRequest = new MemberRequest();
        invalidUpdateRequest.setFullName(""); // Invalid: blank name
        invalidUpdateRequest.setEmail("not-an-email");

        mockMvc.perform(put("/Members/" + existingMember.getMemberId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("Full name cannot be blank")))
                .andExpect(jsonPath("$.errors", hasItem("Email should be valid")));
    }

    @Test
    void deleteMember_whenMemberExists_shouldReturnNoContent() throws Exception {
        Member memberToDelete = new Member("To Delete", "delete@example.com", "Delete Addr", "456",
                                           "0003", "DelType", "2023-01-01", "2024-01-01", null);
        memberRepository.save(memberToDelete);

        mockMvc.perform(delete("/Members/" + memberToDelete.getMemberId()))
                .andExpect(status().isNoContent());

        assertTrue(memberRepository.findByMemberId(memberToDelete.getMemberId()).isEmpty());
    }

    @Test
    void deleteMember_whenMemberDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/Members/nonExistentId01"))
                .andExpect(status().isNotFound());
    }
}
