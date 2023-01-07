package tender.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import tender.domain.Student;

/**
 * Spring Data JPA repository for the Student entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {}
