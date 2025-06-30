package com.nexus.GYMPULSE.service.implementations;

import com.nexus.GYMPULSE.model.attendance.Attendance;
import com.nexus.GYMPULSE.repositories.AttendanceRepository;
import com.nexus.GYMPULSE.requests.AttendanceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private Attendance attendance1;
    private AttendanceRequest attendanceRequest;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        // Attendance ID is String (MongoDB ObjectId)
        attendance1 = new Attendance("ATT001", "M001", "TS001", today, true);

        attendanceRequest = new AttendanceRequest();
        attendanceRequest.setMemberId("M002");
        attendanceRequest.setTimeSlotId("TS002");
        attendanceRequest.setDate(today.plusDays(1));
        attendanceRequest.setAttended(false);
    }

    @Test
    void recordAttendance_success() {
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> {
            Attendance att = invocation.getArgument(0);
            if (att.getId() == null) {
                att.setId("genAttID"); // Simulate ID generation
            }
            return att;
        });

        Attendance recordedAttendance = attendanceService.recordAttendance(attendanceRequest);

        assertNotNull(recordedAttendance);
        assertEquals("M002", recordedAttendance.getMemberId());
        assertEquals(today.plusDays(1), recordedAttendance.getDate());
        assertFalse(recordedAttendance.isAttended());
        assertNotNull(recordedAttendance.getId());
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    void getAllAttendanceRecords_success() {
        Attendance attendance2 = new Attendance("ATT002", "M003", "TS003", today.minusDays(1), true);
        when(attendanceRepository.findAll()).thenReturn(Arrays.asList(attendance1, attendance2));

        List<Attendance> records = attendanceService.getAllAttendanceRecords();

        assertEquals(2, records.size());
        verify(attendanceRepository, times(1)).findAll();
    }

    @Test
    void getAttendanceById_found() {
        when(attendanceRepository.findById("ATT001")).thenReturn(Optional.of(attendance1));
        Optional<Attendance> foundRecord = attendanceService.getAttendanceById("ATT001");
        assertTrue(foundRecord.isPresent());
        assertEquals("M001", foundRecord.get().getMemberId());
    }

    @Test
    void getAttendanceById_notFound() {
        when(attendanceRepository.findById("ATT999")).thenReturn(Optional.empty());
        Optional<Attendance> foundRecord = attendanceService.getAttendanceById("ATT999");
        assertFalse(foundRecord.isPresent());
    }

    @Test
    void getAttendanceByMemberId_success() {
        when(attendanceRepository.findByMemberId("M001")).thenReturn(Collections.singletonList(attendance1));
        List<Attendance> memberRecords = attendanceService.getAttendanceByMemberId("M001");
        assertFalse(memberRecords.isEmpty());
        assertEquals("M001", memberRecords.get(0).getMemberId());
    }

    @Test
    void getAttendanceByMemberId_noneFound() {
        when(attendanceRepository.findByMemberId("M999")).thenReturn(Collections.emptyList());
        List<Attendance> memberRecords = attendanceService.getAttendanceByMemberId("M999");
        assertTrue(memberRecords.isEmpty());
    }


    @Test
    void getAttendanceByTimeSlotId_success() {
        when(attendanceRepository.findByTimeSlotId("TS001")).thenReturn(Collections.singletonList(attendance1));
        List<Attendance> slotRecords = attendanceService.getAttendanceByTimeSlotId("TS001");
        assertFalse(slotRecords.isEmpty());
        assertEquals("TS001", slotRecords.get(0).getTimeSlotId());
    }

    @Test
    void getAttendanceByTimeSlotId_noneFound() {
        when(attendanceRepository.findByTimeSlotId("TS999")).thenReturn(Collections.emptyList());
        List<Attendance> slotRecords = attendanceService.getAttendanceByTimeSlotId("TS999");
        assertTrue(slotRecords.isEmpty());
    }

    // No update or delete methods in AttendanceService interface, so no tests for those.
}
