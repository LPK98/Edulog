package com.edulog.controller;

import com.edulog.model.Student;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @GetMapping
    public List<Student> list(){
        List<Student> list = new ArrayList<>();
        list.add(new Student(1L, "SC/2022/12879", "A.D.V.Chandrasekara", 2.7));
        list.add(new Student(2L, "SC/2022/12880", "Jineth Bosilu", 3.75));
        list.add(new Student(3L, "SC/2022/12881", "Geethika Mabula", 3.2));
        return list;
    }
}
