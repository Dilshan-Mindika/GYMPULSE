package com.nexus.GYMPULSE.controllers;

import com.nexus.GYMPULSE.model.attendance.Attendance;
import com.nexus.GYMPULSE.requests.AttendanceRequest;
import com.nexus.GYMPULSE.service.interfaces.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<Attendance> recordAttendance(@RequestBody AttendanceRequest attendanceRequest) {
        Attendance createdAttendance = attendanceService.recordAttendance(attendanceRequest);
        return ResponseEntity.ok(createdAttendance);
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendanceRecords() {
        List<Attendance> attendanceRecords = attendanceService.getAllAttendanceRecords();
        return ResponseEntity.ok(attendanceRecords);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getAttendanceById(@PathVariable String id) {
        return attendanceService.getAttendanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
