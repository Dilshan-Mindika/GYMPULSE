package com.nexus.GYMPULSE.controllers;

import com.nexus.GYMPULSE.model.attendance.Attendance;
import com.nexus.GYMPULSE.requests.AttendanceRequest;
import com.nexus.GYMPULSE.service.interfaces.AttendanceService;
import jakarta.validation.Valid; // Import @Valid
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Import HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<Attendance> recordAttendance(@Valid @RequestBody AttendanceRequest attendanceRequest) { // Added @Valid
        Attendance createdAttendance = attendanceService.recordAttendance(attendanceRequest);
        return new ResponseEntity<>(createdAttendance, HttpStatus.CREATED); // Return 201 Created
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendanceRecords() {
        List<Attendance> attendanceRecords = attendanceService.getAllAttendanceRecords();
        return ResponseEntity.ok(attendanceRecords);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable String id) {
        // Service should throw ResourceNotFoundException if not found.
        return ResponseEntity.ok(attendanceService.getAttendanceById(id)
                .orElseThrow(() -> new com.nexus.GYMPULSE.exception.ResourceNotFoundException("Attendance", "id", id)));
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Attendance>> getAttendanceByMemberId(@PathVariable String memberId) {
        List<Attendance> attendanceRecords = attendanceService.getAttendanceByMemberId(memberId);
        return ResponseEntity.ok(attendanceRecords);
    }

    @GetMapping("/timeslot/{timeSlotId}")
    public ResponseEntity<List<Attendance>> getAttendanceByTimeSlotId(@PathVariable String timeSlotId) {
        List<Attendance> attendanceRecords = attendanceService.getAttendanceByTimeSlotId(timeSlotId);
        return ResponseEntity.ok(attendanceRecords);
    }
}
