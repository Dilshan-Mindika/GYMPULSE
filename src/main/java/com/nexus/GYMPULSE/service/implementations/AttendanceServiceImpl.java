package com.nexus.GYMPULSE.service.implementations;

import com.nexus.GYMPULSE.model.attendance.Attendance;
import com.nexus.GYMPULSE.repositories.AttendanceRepository;
import com.nexus.GYMPULSE.requests.AttendanceRequest;
import com.nexus.GYMPULSE.service.interfaces.AttendanceService;
import org.slf4j.Logger; // Import SLF4J Logger
import org.slf4j.LoggerFactory; // Import SLF4J LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    private static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class); // SLF4J Logger

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Override
    public Attendance recordAttendance(AttendanceRequest attendanceRequest) {
        Attendance attendance = new Attendance();
        attendance.setMemberId(attendanceRequest.getMemberId());
        attendance.setTimeSlotId(attendanceRequest.getTimeSlotId());
        attendance.setDate(attendanceRequest.getDate());
        attendance.setAttended(attendanceRequest.getAttended()); // Use getAttended() for Boolean
        Attendance savedAttendance = attendanceRepository.save(attendance);
        logger.info("Attendance recorded for member ID: {} on date: {}", savedAttendance.getMemberId(), savedAttendance.getDate());
        return savedAttendance;
    }

    @Override
    public List<Attendance> getAllAttendanceRecords() {
        logger.info("Retrieving all attendance records");
        return attendanceRepository.findAll();
    }

    @Override
    public Optional<Attendance> getAttendanceById(String id) {
        logger.info("Retrieving attendance by ID: {}", id);
        // Controller will handle ResourceNotFoundException if Optional is empty.
        return attendanceRepository.findById(id);
    }

    @Override
    public List<Attendance> getAttendanceByMemberId(String memberId) {
        logger.info("Retrieving attendance records for member ID: {}", memberId);
        return attendanceRepository.findByMemberId(memberId);
    }

    @Override
    public List<Attendance> getAttendanceByTimeSlotId(String timeSlotId) {
        logger.info("Retrieving attendance records for time slot ID: {}", timeSlotId);
        return attendanceRepository.findByTimeSlotId(timeSlotId);
    }
}
