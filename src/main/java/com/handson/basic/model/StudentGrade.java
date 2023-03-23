package com.handson.basic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.handson.basic.util.Dates;
import org.hibernate.validator.constraints.Length;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="student_grade")
public class StudentGrade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false, updatable = false)
    private Date createdAt = Dates.nowUTC();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdAt")
    public LocalDateTime calcCreatedAt() {
        return Dates.atLocalTime(createdAt);
    }

    @JsonIgnore
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "studentId")
    private Student student;

    @NotEmpty
    @Length(max = 60)
    private String courseName;


    @Min(10)
    @Max(100)
    private Integer courseScore;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCourseScore() {
        return courseScore;
    }

    public void setCourseScore(Integer courseScore) {
        this.courseScore = courseScore;
    }

    public static final class StudentGradeBuilder {
        private Long id;
        private Date createdAt = Dates.nowUTC();
        private Student student;
        private String courseName;
        private Integer courseScore;

        private StudentGradeBuilder() {
        }

        public static StudentGradeBuilder aStudentGrade() {
            return new StudentGradeBuilder();
        }

        public StudentGradeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public StudentGradeBuilder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public StudentGradeBuilder student(Student student) {
            this.student = student;
            return this;
        }

        public StudentGradeBuilder courseName(String courseName) {
            this.courseName = courseName;
            return this;
        }

        public StudentGradeBuilder courseScore(Integer courseScore) {
            this.courseScore = courseScore;
            return this;
        }

        public StudentGrade build() {
            StudentGrade studentGrade = new StudentGrade();
            studentGrade.student = this.student;
            studentGrade.courseName = this.courseName;
            studentGrade.courseScore = this.courseScore;
            studentGrade.id = this.id;
            studentGrade.createdAt = this.createdAt;
            return studentGrade;
        }
    }
}
