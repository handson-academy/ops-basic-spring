package com.handson.basic.repo;


import com.handson.basic.model.StudentGrade;
import org.springframework.data.repository.CrudRepository;

public interface StudentGradeRepository extends CrudRepository<StudentGrade,Long> {

}
