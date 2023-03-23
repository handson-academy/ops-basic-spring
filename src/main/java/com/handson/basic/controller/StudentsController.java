package com.handson.basic.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handson.basic.model.*;
import com.handson.basic.repo.StudentService;
import com.handson.basic.util.HandsonException;
import org.apache.commons.collections4.IteratorUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.validation.constraints.Min;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.handson.basic.util.Dates.atUtc;
import static com.handson.basic.util.FPS.FPSBuilder.aFPS;
import static com.handson.basic.util.FPSCondition.FPSConditionBuilder.aFPSCondition;
import static com.handson.basic.util.FPSField.FPSFieldBuilder.aFPSField;
import static com.handson.basic.util.Strings.likeLowerOrNull;
import static org.apache.logging.log4j.util.Strings.isEmpty;

@RestController
@RequestMapping("/api/students")
public class StudentsController {

    @Autowired
    StudentService studentService;

    @Autowired
    EntityManager em;

    @Autowired
    ObjectMapper om;



    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<PaginationAndList> search(@RequestParam(required = false) String fullName,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromBirthDate,
                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toBirthDate,
                                                    @RequestParam(required = false) Integer fromSatScore,
                                                    @RequestParam(required = false) Integer toSatScore,
                                                    @RequestParam(required = false) Integer fromAvgScore,
                                                    @RequestParam(required = false) Integer toAvgScore,
                                                    @RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "50") @Min(1) Integer count,
                                                    @RequestParam(defaultValue = "id") StudentSortField sort, @RequestParam(defaultValue = "asc") SortDirection sortDirection) throws JsonProcessingException {

        var res =aFPS().select(List.of(
                aFPSField().field("s.id").alias("id").build(),
                aFPSField().field("s.created_at").alias("createdat").build(),
                aFPSField().field("s.fullname").alias("fullname").build(),
                aFPSField().field("s.birth_date").alias("birthdate").build(),
                aFPSField().field("s.sat_score").alias("satscore").build(),
                aFPSField().field("s.graduation_score").alias("graduationscore").build(),
                aFPSField().field("s.phone").alias("phone").build(),
                aFPSField().field("s.email").alias("email").build(),
                aFPSField().field("s.profile_picture").alias("profilepicture").build(),
                aFPSField().field("(select avg(sg.course_score) from  student_grade sg where sg.student_id = s.id ) ").alias("avgscore").build()
        ))
                .from(List.of(" student s"))
                .conditions(List.of(
                        aFPSCondition().condition("( lower(fullname) like :fullName )").parameterName("fullName").value(likeLowerOrNull(fullName)).build(),
                        aFPSCondition().condition("( s.birth_Date >= :fromBirthDate )").parameterName("fromBirthDate").value(atUtc(fromBirthDate)).build(),
                        aFPSCondition().condition("( s.birth_Date <= :toBirthDate )").parameterName("toBirthDate").value(atUtc(toBirthDate)).build(),
                        aFPSCondition().condition("( sat_score >= :fromSatScore )").parameterName("fromSatScore").value(fromSatScore).build(),
                        aFPSCondition().condition("( sat_score <= :toSatScore )").parameterName("toSatScore").value(toSatScore).build(),
                        aFPSCondition().condition("( (select avg(sg.course_score) from  student_grade sg where sg.student_id = s.id ) >= :fromAvgScore )").parameterName("fromAvgScore").value(fromAvgScore).build(),
                        aFPSCondition().condition("( (select avg(sg.course_score) from  student_grade sg where sg.student_id = s.id ) <= :toAvgScore )").parameterName("toAvgScore").value(toAvgScore).build()
                )).sortField(sort.fieldName).sortDirection(sortDirection).page(page).count(count)
                .itemClass(StudentOut.class)
                .build().exec(em, om);
        res.getData();
        return ResponseEntity.ok(res);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOneStudent(@PathVariable Long id)
    {
        return new ResponseEntity<>(studentService.findById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/highSat", method = RequestMethod.GET)
    public ResponseEntity<?> getHighSatStudents(@RequestParam Integer sat)
    {
        return new ResponseEntity<>(studentService.getStudentWithSatHigherThan(sat), HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> insertStudent(@RequestBody StudentIn studentIn)
    {
        Student student = studentIn.toStudent();
        student = studentService.save(student);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody StudentIn student)
    {
        Optional<Student> dbStudent = studentService.findById(id);
        if (dbStudent.isEmpty()) throw new HandsonException("Student with id: " + id + " not found");
        student.updateStudent(dbStudent.get());
        Student updatedStudent = studentService.save(dbStudent.get());
        return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteStudent(@PathVariable Long id)
    {
        Optional<Student> dbStudent = studentService.findById(id);
        if (dbStudent.isEmpty()) throw new HandsonException("Student with id: " + id + " not found");
        studentService.delete(dbStudent.get());
        return new ResponseEntity<>("DELETED", HttpStatus.OK);
    }
}
