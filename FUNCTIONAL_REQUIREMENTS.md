# GYMPULSE Application - Functional Requirements and Use Cases

## 1. Overview

This document outlines the functional requirements and key use cases for the GYMPULSE application. It is derived from the analysis of the application's API and intended capabilities for managing gym operations, members, trainers, workouts, equipment, feedback, and attendance.

## 2. Actors

The primary actors interacting with or managed by the GYMPULSE system are:

*   **Member:** Clients of the gym who use its services, follow workout plans, and provide feedback.
*   **Trainer:** Gym staff responsible for creating workout plans, managing exercises, and potentially guiding members.
*   **Administrator (Gym Staff/Manager):** Personnel responsible for overall system management, including user accounts, inventory, and master data like exercises. This role is largely inferred from the available API functionalities that imply privileged access.
*   **System (Implicit):** Represents automated processes within the application, such as ID generation or timestamping.

## 3. Functional Areas

The GYMPULSE application's functionality can be grouped into the following areas:

1.  **User Management (Members & Trainers):** Registration, profile management, and administration.
2.  **Workout Management:** Creation, modification, and assignment of exercises, daily routines, and workout plans.
3.  **Equipment Inventory Management:** Tracking and managing gym equipment.
4.  **Feedback System:** Collection and management of feedback.
5.  **Attendance Tracking:** Recording and viewing member attendance.

## 4. Functional Requirements

### 4.1 User Management (Members & Trainers)

#### 4.1.1 Members
*   `FR-MEM-001`: The system shall allow for the creation of new member profiles with details such as full name, email, address, phone number, membership type, start date, and end date.
*   `FR-MEM-002`: The system shall allow retrieval of a list of all registered members.
*   `FR-MEM-003`: The system shall allow retrieval of a specific member's profile by their unique member ID.
*   `FR-MEM-004`: The system shall allow updating an existing member's profile information.
*   `FR-MEM-005`: The system shall allow for the deletion of a member's profile.
*   `FR-MEM-006`: The system shall associate a workout plan with a member.
*   `FR-MEM-007`: The system shall generate a unique 4-digit numeric ID for each new member, up to a limit of 9999 members.

#### 4.1.2 Trainers
*   `FR-TRN-001`: The system shall allow for the creation of new trainer profiles with details such as full name, email, address, phone number, specialty, salary, and certification number.
*   `FR-TRN-002`: The system shall allow retrieval of a list of all registered trainers.
*   `FR-TRN-003`: The system shall allow retrieval of a specific trainer's profile by their unique trainer ID.
*   `FR-TRN-004`: The system shall allow updating an existing trainer's profile information.
*   `FR-TRN-005`: The system shall allow for the deletion of a trainer's profile.
*   `FR-TRN-006`: The system shall generate a unique 4-digit numeric ID for each new trainer, up to a limit of 9999 trainers.

### 4.2 Workout Management

#### 4.2.1 Exercises
*   `FR-EXR-001`: The system shall allow for the creation of new exercises with attributes such as name, quantity of sets, quantity of repetitions, and rest time in seconds.
*   `FR-EXR-002`: The system shall allow retrieval of a list of all available exercises.
*   `FR-EXR-003`: The system shall allow retrieval of a specific exercise by its unique ID.
*   `FR-EXR-004`: The system shall allow updating the attributes of an existing exercise.
*   `FR-EXR-005`: The system shall allow for the deletion of an exercise.
*   `FR-EXR-006`: The system shall allow an existing exercise to be cloned to create a new exercise with the same attributes.

#### 4.2.2 Daily Workouts (Templates)
*   `FR-DW-001`: The system shall allow for the creation of daily workout templates, specifying the day of the week and a list of exercises.
*   `FR-DW-002`: The system shall allow retrieval of a list of all daily workout templates.
*   `FR-DW-003`: The system shall allow retrieval of a specific daily workout template by its unique ID.
*   `FR-DW-004`: The system shall allow retrieval of a daily workout template by the day of the week.
*   `FR-DW-005`: The system shall allow updating an existing daily workout template.
*   `FR-DW-006`: The system shall allow for the deletion of a daily workout template.

#### 4.2.3 Workout Plans
*   `FR-WP-001`: The system shall allow for the creation of a workout plan for a specific member, assigned by a specific trainer, including a start date, end date, and a list of daily workouts.
*   `FR-WP-002`: The system shall allow retrieval of a list of all workout plans.
*   `FR-WP-003`: The system shall allow retrieval of a specific workout plan by its unique ID.
*   `FR-WP-004`: The system shall allow retrieval of all workout plans associated with a specific member ID.
*   `FR-WP-005`: The system shall allow updating an existing workout plan.
*   `FR-WP-006`: The system shall allow for the deletion of a workout plan by its unique ID.
*   `FR-WP-007`: The system shall allow for the creation of a workout plan using a predefined strategy (e.g., Cardio, Strength), which automatically generates the daily workouts.

### 4.3 Equipment Inventory Management
*   `FR-EQP-001`: The system shall allow for adding new equipment to the inventory with details such as name, type, brand, quantity, and availability status.
*   `FR-EQP-002`: The system shall allow retrieval of a list of all equipment in the inventory.
*   `FR-EQP-003`: The system shall allow retrieval of specific equipment details by its unique ID.
*   `FR-EQP-004`: The system shall allow updating the details of existing equipment.
*   `FR-EQP-005`: The system shall allow for the deletion of equipment from the inventory.

### 4.4 Feedback System
*   `FR-FBK-001`: The system shall allow a member to submit feedback, including comments, a rating (1-5), and optionally associate it with a trainer.
*   `FR-FBK-002`: The system shall allow retrieval of all feedback submitted.
*   `FR-FBK-003`: The system shall allow retrieval of specific feedback by its unique ID.
*   `FR-FBK-004`: The system shall allow retrieval of all feedback associated with a specific trainer ID.
*   `FR-FBK-005`: The system shall allow retrieval of all feedback submitted by a specific member ID.
*   `FR-FBK-006`: The system shall allow updating the comments and rating of existing feedback.
*   `FR-FBK-007`: The system shall allow for the deletion of feedback.
*   `FR-FBK-008`: The system shall automatically timestamp feedback upon creation.

### 4.5 Attendance Tracking
*   `FR-ATT-001`: The system shall allow for recording member attendance, including member ID, time slot ID, date, and attended status.
*   `FR-ATT-002`: The system shall allow retrieval of all attendance records.
*   `FR-ATT-003`: The system shall allow retrieval of a specific attendance record by its unique ID.
*   `FR-ATT-004`: The system shall allow retrieval of all attendance records for a specific member ID.
*   `FR-ATT-005`: The system shall allow retrieval of all attendance records for a specific time slot ID.

## 5. Key Use Cases

### 5.1 Use Case: Register New Member
*   **Use Case ID:** UC-MEM-001
*   **Use Case Name:** Register New Member
*   **Actor(s):** Administrator
*   **Description:** This use case describes how an Administrator registers a new member in the GYMPULSE system.
*   **Preconditions:**
    *   The Administrator is logged into the system with appropriate privileges.
    *   The system is able to accept new member registrations (e.g., member limit not reached).
*   **Postconditions:**
    *   A new member profile is created and stored in the system.
    *   The new member is assigned a unique Member ID.
    *   The member's details are saved.
*   **Main Flow (Basic Path):**
    1.  Administrator initiates "Add New Member".
    2.  System prompts for member details (Full Name, Email, Address, Phone, Membership Type, Start/End Dates).
    3.  Administrator enters details.
    4.  Administrator submits.
    5.  System validates data.
    6.  System generates unique Member ID.
    7.  System creates and saves new member record.
    8.  System confirms registration.
*   **Alternative Flows:**
    *   **A1: Invalid Data Entry:** If validation fails, system displays error, Administrator corrects and resubmits.
    *   **A2: Member Limit Reached:** If ID limit reached, system displays error, use case terminates.
    *   **A3: System Error:** If persistence error, system displays error, use case terminates.

### 5.2 Use Case: Trainer Creates Workout Plan for Member
*   **Use Case ID:** UC-WP-001
*   **Use Case Name:** Create Workout Plan for Member
*   **Actor(s):** Trainer
*   **Description:** A Trainer creates a customized workout plan for a member.
*   **Preconditions:**
    *   Trainer is logged in.
    *   Target Member exists.
    *   Relevant Exercises and Daily Workouts (templates) exist or can be defined.
*   **Postconditions:**
    *   New workout plan is created and associated with the Member and Trainer.
    *   Plan includes start/end dates and daily workout routines.
*   **Main Flow (Basic Path):**
    1.  Trainer initiates "Create Workout Plan".
    2.  System prompts for Member ID, Start Date, End Date.
    3.  Trainer enters details.
    4.  System allows Trainer to add/select Daily Workouts.
    5.  Trainer finalizes Daily Workouts.
    6.  Trainer submits.
    7.  System validates data.
    8.  System creates and saves workout plan.
    9.  System confirms creation.
*   **Alternative Flows:**
    *   **A1: Invalid Member/Trainer ID:** System displays error.
    *   **A2: Invalid Date Range:** System displays error.
    *   **A3: No Daily Workouts Added (if mandatory):** System displays error.

### 5.3 Use Case: Member Submits Feedback
*   **Use Case ID:** UC-FBK-001
*   **Use Case Name:** Submit Feedback
*   **Actor(s):** Member
*   **Description:** A Member submits feedback.
*   **Preconditions:**
    *   Member is identified in the system.
*   **Postconditions:**
    *   Feedback is recorded with Member ID, comments, rating, optional Trainer ID, and timestamp.
*   **Main Flow (Basic Path):**
    1.  Member navigates to "Submit Feedback".
    2.  System prompts for Member ID (possibly pre-filled), optional Trainer ID, comments, rating (1-5).
    3.  Member enters details.
    4.  Member submits.
    5.  System validates input.
    6.  System records feedback with timestamp.
    7.  System confirms submission.
*   **Alternative Flows:**
    *   **A1: Invalid Input:** System displays error, Member corrects.

### 5.4 Use Case: Administrator Adds New Equipment
*   **Use Case ID:** UC-EQP-001
*   **Use Case Name:** Add New Equipment
*   **Actor(s):** Administrator
*   **Description:** An Administrator adds new equipment to the inventory.
*   **Preconditions:**
    *   Administrator is logged in.
*   **Postconditions:**
    *   New equipment is added to inventory with a unique ID.
*   **Main Flow (Basic Path):**
    1.  Administrator initiates "Add New Equipment".
    2.  System prompts for Name, Type, Brand, Quantity, Availability.
    3.  Administrator enters details.
    4.  Administrator submits.
    5.  System validates data.
    6.  System saves new equipment and generates ID.
    7.  System confirms addition.
*   **Alternative Flows:**
    *   **A1: Invalid Data:** System displays error, Administrator corrects.

---
This content should provide a good starting point for the functional documentation.
This completes Step 6 of the documentation plan.
