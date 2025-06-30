package com.nexus.GYMPULSE.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nexus.GYMPULSE.exception.GlobalExceptionHandler;
import com.nexus.GYMPULSE.exception.ResourceNotFoundException;
import com.nexus.GYMPULSE.model.attendance.Attendance;
import com.nexus.GYMPULSE.requests.AttendanceRequest;
import com.nexus.GYMPULSE.service.interfaces.AttendanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AttendanceControllerTest {

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private AttendanceController attendanceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Attendance attendance1;
    private AttendanceRequest attendanceRequest;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(attendanceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // For LocalDate serialization

        today = LocalDate.now();
        attendance1 = new Attendance("ATT001", "M001", "TS001", today, true);

        attendanceRequest = new AttendanceRequest();
        attendanceRequest.setMemberId("M001");
        attendanceRequest.setTimeSlotId("TS001");
        attendanceRequest.setDate(today);
        attendanceRequest.setAttended(true);
    }

    @Test
    void recordAttendance_shouldReturnCreatedAttendance() throws Exception {
        when(attendanceService.recordAttendance(any(AttendanceRequest.class))).thenReturn(attendance1);

        mockMvc.perform(post("/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attendanceRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId", is("M001")));
    }

    @Test
    void getAllAttendanceRecords_shouldReturnListOfRecords() throws Exception {
        when(attendanceService.getAllAttendanceRecords()).thenReturn(Arrays.asList(attendance1));
        mockMvc.perform(get("/attendance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].memberId", is("M001")));
    }

    @Test
    void getAttendanceById_shouldReturnRecordWhenFound() throws Exception {
        when(attendanceService.getAttendanceById("ATT001")).thenReturn(Optional.of(attendance1));
        mockMvc.perform(get("/attendance/ATT001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId", is("M001")));
    }

    @Test
    void getAttendanceById_shouldReturnNotFoundWhenDoesNotExist() throws Exception {
        when(attendanceService.getAttendanceById("ATT999")).thenReturn(Optional.empty());
        mockMvc.perform(get("/attendance/ATT999"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }

    @Test
    void getAttendanceByMemberId_shouldReturnListOfRecords() throws Exception {
        when(attendanceService.getAttendanceByMemberId("M001")).thenReturn(Arrays.asList(attendance1));
        mockMvc.perform(get("/attendance/member/M001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].memberId", is("M001")));
    }

    @Test
    void getAttendanceByMemberId_shouldReturnEmptyListWhenNoneFound() throws Exception {
        when(attendanceService.getAttendanceByMemberId("M999")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/attendance/member/M999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getAttendanceByTimeSlotId_shouldReturnListOfRecords() throws Exception {
        when(attendanceService.getAttendanceByTimeSlotId("TS001")).thenReturn(Arrays.asList(attendance1));
        mockMvc.perform(get("/attendance/timeslot/TS001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].timeSlotId", is("TS001")));
    }

    @Test
    void getAttendanceByTimeSlotId_shouldReturnEmptyListWhenNoneFound() throws Exception {
        when(attendanceService.getAttendanceByTimeSlotId("TS999")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/attendance/timeslot/TS999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
