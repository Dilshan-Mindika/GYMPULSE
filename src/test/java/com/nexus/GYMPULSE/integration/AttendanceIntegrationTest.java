package com.nexus.GYMPULSE.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nexus.GYMPULSE.model.attendance.Attendance;
import com.nexus.GYMPULSE.repositories.AttendanceRepository;
import com.nexus.GYMPULSE.requests.AttendanceRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AttendanceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AttendanceRepository attendanceRepository;

    private AttendanceRequest validAttendanceRequest;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        attendanceRepository.deleteAll();
        objectMapper.registerModule(new JavaTimeModule()); // Ensure LocalDate is handled

        testDate = LocalDate.of(2024, 7, 20);

        validAttendanceRequest = new AttendanceRequest();
        validAttendanceRequest.setMemberId("M_INT_ATT_01");
        validAttendanceRequest.setTimeSlotId("TS_INT_ATT_MORNING");
        validAttendanceRequest.setDate(testDate);
        validAttendanceRequest.setAttended(true);
    }

    @AfterEach
    void tearDown() {
        attendanceRepository.deleteAll();
    }

    @Test
    void recordAttendance_withValidData_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAttendanceRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId", is(validAttendanceRequest.getMemberId())))
                .andExpect(jsonPath("$.timeSlotId", is(validAttendanceRequest.getTimeSlotId())))
                .andExpect(jsonPath("$.date", is(testDate.toString()))) // LocalDate serializes to YYYY-MM-DD
                .andExpect(jsonPath("$.attended", is(true)))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void recordAttendance_withInvalidData_shouldReturnBadRequest() throws Exception {
        AttendanceRequest invalidRequest = new AttendanceRequest();
        invalidRequest.setMemberId(""); // Blank memberId
        invalidRequest.setDate(null); // Null date

        mockMvc.perform(post("/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasItem("Member ID cannot be blank")))
                .andExpect(jsonPath("$.errors", hasItem("Time slot ID cannot be blank"))) // Assuming timeslotId is also required
                .andExpect(jsonPath("$.errors", hasItem("Date cannot be null")))
                .andExpect(jsonPath("$.errors", hasItem("Attended status cannot be null")));
    }

    @Test
    void getAttendanceById_whenRecordExists_shouldReturnRecord() throws Exception {
        Attendance attendance = new Attendance(null, "M_INT_ATT_02", "TS_INT_ATT_EVENING", testDate.plusDays(1), false);
        Attendance savedAttendance = attendanceRepository.save(attendance);

        mockMvc.perform(get("/attendance/" + savedAttendance.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId", is("M_INT_ATT_02")))
                .andExpect(jsonPath("$.id", is(savedAttendance.getId())));
    }

    @Test
    void getAttendanceById_whenRecordDoesNotExist_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/attendance/nonExistentAttId"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAttendanceByMemberId_shouldReturnRecordsForMember() throws Exception {
        attendanceRepository.save(new Attendance(null, "M_TARGET", "TS1", testDate, true));
        attendanceRepository.save(new Attendance(null, "M_TARGET", "TS2", testDate.plusDays(1), false));
        attendanceRepository.save(new Attendance(null, "M_OTHER", "TS3", testDate, true));


        mockMvc.perform(get("/attendance/member/M_TARGET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].memberId", is("M_TARGET")))
                .andExpect(jsonPath("$[1].memberId", is("M_TARGET")));
    }

    @Test
    void getAttendanceByMemberId_whenNoRecordsForMember_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/attendance/member/M_NON_EXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    void getAttendanceByTimeSlotId_shouldReturnRecordsForTimeSlot() throws Exception {
        attendanceRepository.save(new Attendance(null, "M1", "TS_TARGET", testDate, true));
        attendanceRepository.save(new Attendance(null, "M2", "TS_TARGET", testDate.plusDays(1), true));
        attendanceRepository.save(new Attendance(null, "M3", "TS_OTHER", testDate, true));

        mockMvc.perform(get("/attendance/timeslot/TS_TARGET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].timeSlotId", is("TS_TARGET")))
                .andExpect(jsonPath("$[1].timeSlotId", is("TS_TARGET")));
    }

     @Test
    void getAllAttendanceRecords_shouldReturnAllRecords() throws Exception {
        attendanceRepository.save(new Attendance(null, "M_ALL_1", "TS_ALL_1", testDate, true));
        attendanceRepository.save(new Attendance(null, "M_ALL_2", "TS_ALL_2", testDate.plusDays(1), false));

        mockMvc.perform(get("/attendance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    // Attendance has no PUT or DELETE endpoints in the controller, so no tests for those.
}
