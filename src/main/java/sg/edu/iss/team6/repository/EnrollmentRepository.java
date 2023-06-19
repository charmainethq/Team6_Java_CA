package sg.edu.iss.team6.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sg.edu.iss.team6.model.*;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer>{


    Enrollment findByEnrollmentId(long enrollmentId);
    void deleteByEnrollmentId(long enrollmentId);


    Enrollment findByCourseClassClassIdAndStudentStudentId(long classId, long studentId);
    @Modifying
    @Query("UPDATE Enrollment e SET e.enrollmentStatus = :status WHERE e.enrollmentId = :enrollmentId")
    int updateEnrollmentStatus(@Param("enrollmentId") long enrollmentId, @Param("status") EnrollmentEnum status);

	@Query("SELECT e FROM Enrollment e WHERE e.courseClass.classId = :classId")
	public ArrayList<Enrollment> findEnrollmentByCourseID(@Param("classId") long classId);
}
