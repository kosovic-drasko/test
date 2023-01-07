package tender.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tender.domain.Student;
import tender.repository.StudentRepository;

/**
 * Service Implementation for managing {@link Student}.
 */
@Service
@Transactional
public class StudentService {

    private final Logger log = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Save a student.
     *
     * @param student the entity to save.
     * @return the persisted entity.
     */
    public Student save(Student student) {
        log.debug("Request to save Student : {}", student);
        return studentRepository.save(student);
    }

    /**
     * Update a student.
     *
     * @param student the entity to save.
     * @return the persisted entity.
     */
    public Student update(Student student) {
        log.debug("Request to update Student : {}", student);
        return studentRepository.save(student);
    }

    /**
     * Partially update a student.
     *
     * @param student the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Student> partialUpdate(Student student) {
        log.debug("Request to partially update Student : {}", student);

        return studentRepository
            .findById(student.getId())
            .map(existingStudent -> {
                if (student.getName() != null) {
                    existingStudent.setName(student.getName());
                }
                if (student.getAge() != null) {
                    existingStudent.setAge(student.getAge());
                }

                return existingStudent;
            })
            .map(studentRepository::save);
    }

    /**
     * Get all the students.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Student> findAll(Pageable pageable) {
        log.debug("Request to get all Students");
        return studentRepository.findAll(pageable);
    }

    /**
     * Get one student by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Student> findOne(Long id) {
        log.debug("Request to get Student : {}", id);
        return studentRepository.findById(id);
    }

    /**
     * Delete the student by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Student : {}", id);
        studentRepository.deleteById(id);
    }
}
