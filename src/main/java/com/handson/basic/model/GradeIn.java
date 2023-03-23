package com.handson.basic.model;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import static com.handson.basic.model.StudentGrade.StudentGradeBuilder.aStudentGrade;

public class GradeIn {

    @NotEmpty
    @Length(max = 60)
    private String courseName;

    @Min(10)
    @Max(100)
    private Integer courseScore;

    public StudentGrade toGrade(Student student) {
        return aStudentGrade().student(student).courseName(courseName).courseScore(courseScore).build();
    }

    public void updateStudentGrade(StudentGrade studentGrade) {
        studentGrade.setCourseName(courseName);
        studentGrade.setCourseScore(courseScore);
    }

    public String getCourseName() {
        return courseName;
    }

    public Integer getCourseScore() {
        return courseScore;
    }
}
