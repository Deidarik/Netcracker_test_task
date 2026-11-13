package com.example.lmslite.controller;

import com.example.lmslite.dto.InstructorResponse;
import com.example.lmslite.dto.RegisterInstructorRequest;
import com.example.lmslite.model.Instructor;
import com.example.lmslite.service.InstructorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instructors")
@RequiredArgsConstructor
public class InstructorController {

    private final InstructorService instructorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InstructorResponse registerInstructor(
           @Valid @RequestBody RegisterInstructorRequest request
    ) {
        Instructor instructor = instructorService.registerInstructor(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.bio()
        );

        return new InstructorResponse(
                instructor.getId(),
                instructor.getFirstName(),
                instructor.getLastName(),
                instructor.getEmail(),
                instructor.getBio()
        );
    }

    @GetMapping("/{id}")
    public InstructorResponse getInstructorById(
            @PathVariable Long id
    ) {
        Instructor instructor = instructorService.getInstructorById(id);

        return new InstructorResponse(
                instructor.getId(),
                instructor.getFirstName(),
                instructor.getLastName(),
                instructor.getEmail(),
                instructor.getBio()
        );
    }
}