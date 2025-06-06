package com.courseapp.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseapp.entities.Course;
import com.courseapp.entities.Instructor;
import com.courseapp.repositories.CourseRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class CourseService {
	
	@NonNull
	private final CourseRepository courseRepo;
	
	@NonNull
	private final InstructorService instructorService;
	
	@Transactional(readOnly = true)
	public List<Course> getCourses() {
		log.info("Fetching all courses");
		return courseRepo.findAll();
	}
	
	@Transactional(readOnly = true)
	public Optional<Course> getCourseById(final Long id) {
		log.info("Fetching course with id={}", id);
		return courseRepo.findById(id);
	}
	
	@Transactional
	public Course addCourse(@NonNull final Course course) {
		final Course savedCourse = courseRepo.save(course);
		log.info("Saved a course with id={}", savedCourse.getCourseId());
		return savedCourse;
	}
	
	@Transactional
	public List<Course> addCourses(@NonNull final List<Course> courses) {
		final List<Course> savedCourses = courseRepo.saveAll(courses);
		log.info("Saved {} courses", courses.size());
		
		return savedCourses;
	}
	
	@Transactional
	public Course assignInstructorToCourse(final Long courseId, final Long instructorId) {
		final Course course = this.getCourseById(courseId)
				.orElse(null);
		
		final Instructor instructor = this.instructorService.getInstructor(instructorId)
				.orElse(null);
		
		if (Objects.isNull(course)) {
			log.error("Course with id={} not found", courseId);
			return null;
		}
		
		if (Objects.isNull(instructor)) {
			log.error("Instructor with id={} not found", instructorId);
			return null;
		}
		
		course.setInstructor(instructor);
		
		if (!instructor.getCourses().contains(course)) {
			instructor.getCourses().add(course);
		}
		
		final Course updatedCourse = courseRepo.save(course);
		
		log.info("Assigned instructor id={} to {}.", instructorId, updatedCourse);
		
		return course;
	}
}
