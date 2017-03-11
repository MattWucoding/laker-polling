package edu.oswego.cs.lakerpolling.services

import edu.oswego.cs.lakerpolling.domains.AuthToken
import edu.oswego.cs.lakerpolling.domains.Course
import edu.oswego.cs.lakerpolling.domains.Role
import edu.oswego.cs.lakerpolling.domains.User
import edu.oswego.cs.lakerpolling.util.QueryResult
import edu.oswego.cs.lakerpolling.util.RoleType
import grails.transaction.Transactional
import org.springframework.http.HttpStatus

/**
 * Service to perform transactional operations relating to {@link Course} model.
 */
@Transactional
class CourseService {

    /**
     * Lists students in a specified course
     * @param token - The token to use to retrieve the course-list.
     * @param courseId - The id of the course from which to list students.
     * @return The results of the operations.
     */
    QueryResult<List<User>> getAllStudents(AuthToken token, String courseId) {
        QueryResult<List<User>> res = new QueryResult<>()
        User requestingUser = token?.user
        long cid = courseId.isLong() ? courseId.toLong() : -1

        if (requestingUser != null && isInstructorOrAdmin(requestingUser.role) && cid != -1) {
            Course course = Course.findById(cid)
            if (course != null) {

                // if this is an admin performing the action
                if (requestingUser.role.type == RoleType.ADMIN) {
                    res.data = course.students
                } else {
                    // make sure the requesting user is the instructor
                    if (isInstructorOf(requestingUser, course)) {
                        res.data = course.students
                    } else {
                        QueryResult.fromHttpStatus(HttpStatus.UNAUTHORIZED, res)
                    }
                }
            } else {
                QueryResult.fromHttpStatus(HttpStatus.BAD_REQUEST, res)
            }
        } else {
            QueryResult.fromHttpStatus(HttpStatus.UNAUTHORIZED, res)
        }
        return res
    }

    /**
     * Deletes a specified course. The role of the requesting user is taken into consideration. Only admin and
     * instructors can delete courses and instructors can only delete their own courses.
     * @param token - The token to use to retrieve the requesting user.
     * @param courseId - The id of the course to delete.
     * @return The results of the operations.
     */
    QueryResult<Course> deleteCourse(AuthToken token, String courseId) {
        QueryResult<Course> res = new QueryResult<>()
        User requestingUser = token?.user
        long cid = courseId.isLong() ? courseId.toLong() : -1

        if (requestingUser != null && isInstructorOrAdmin(requestingUser.role) && cid != -1) {
            Course course = Course.findById(cid)
            if (course != null) {
                // if this is an admin performing the action
                if (requestingUser.role.type == RoleType.ADMIN) {
                    doDelete(course, res)
                } else {
                    //make sure the requesting user is the instructor
                    if (isInstructorOf(requestingUser, course)) {
                        doDelete(course, res)
                    } else {
                        QueryResult.fromHttpStatus(HttpStatus.UNAUTHORIZED, res)
                    }
                }
            } else {
                QueryResult.fromHttpStatus(HttpStatus.BAD_REQUEST, res)
            }
        } else {
            QueryResult.fromHttpStatus(HttpStatus.UNAUTHORIZED, res)
        }

        return res
    }

    /**
     * Removes a list of students from a given course. The request is allowed if the requesting user's role
     * is ADMIN or is instructor of the course.
     * @param token - The token identifying the requesting user.
     * @param courseId - The id of the course to delete from.
     * @param userIds - The list of user ids to remove.
     * @return A query result object.
     */
    QueryResult deleteStudentCourse(AuthToken token, long courseId, List<String> userIds) {
        QueryResult res = new QueryResult()
        User requestingUser = token?.user
        Course course = Course.findById(courseId)

        // user and course must exist. check if role is admin or is instructor of course
        if (requestingUser != null && course != null && (requestingUser.role.type == RoleType.ADMIN
                || isInstructorOf(requestingUser, course))) {
            try {
                userIds.each { id ->
                    course.removeFromStudents(User.get(id as Long))
                }
            } catch (Exception e) {
                e.printStackTrace()
                QueryResult.fromHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR, res)
            }
        } else {
            QueryResult.fromHttpStatus(HttpStatus.UNAUTHORIZED, res)
        }

        res
    }
    /**
     * Creates a course for an instructor
     * @param token - The AuthToken of the instructor
     * @param courseId - The crn of the course being created
     * @param name - The name of the course to be created
     * @param result - Optional result to store data in
     * @return query results
     */
    QueryResult<Course> instructorCreateCourse(AuthToken token, String courseId, String name, QueryResult<Course> result = new QueryResult<>(success: true)) {
        User instructor = User.findByAuthToken(token)
        if(isInstructorOrAdmin(instructor.role) && !courseExists(courseId)) {
            result = createCourse(instructor, name, courseId, result)
        } else {
            QueryResult.fromHttpStatus(HttpStatus.BAD_REQUEST, result)
        }
        result
    }

    /**
     * Creates a course for an instructor as an admin
     * @param token - The AuthToken of the admin
     * @param courseId - The crn of the course being created
     * @param name - The name of the course being created
     * @param instructor - The instructor who will own the course
     * @param result - Optional result to store data in
     * @return query results
     */
    QueryResult<Course> adminCreateCourse(AuthToken token, String courseId, String name, String instructor, QueryResult<Course> result = new QueryResult<>(success: true)) {
        User admin = User.findByAuthToken(token)
        User inst = User.findById(Long.parseLong(instructor))
        if(admin.role.type == RoleType.ADMIN && inst.role.type == RoleType.INSTRUCTOR && !courseExists(courseId)) {
            result = createCourse(inst, name, courseId, result)
        } else {
            QueryResult.fromHttpStatus(HttpStatus.BAD_REQUEST, result)
        }
        result
    }

    /**
     * Creates a course
     * @param instructor - The instructor who will own a course
     * @param name - The name of the course
     * @param courseId - The crn of the course
     * @param result - the QueryResult of the request
     * @return query results
     */
    private QueryResult<Course> createCourse(User instructor, String name, String courseId, QueryResult<Course> result) {
        Course course = new Course(name: name, crn: courseId, instructor: instructor)
        course.save(flush: true, failOnError: true)
        result.data = course
        result
    }

    /**
     * Deletes a course.
     * @param course - The course to delete.
     * @param result - Optional result to store data in.
     * @return query results.
     */
    private QueryResult<Course> doDelete(Course course, QueryResult<Course> result = new QueryResult<>(success: true)) {
        try {
            course.delete(flush: true, failOnError: true)
        } catch (Exception e) {
            e.printStackTrace()
            QueryResult.fromHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR, result)
        }
        result
    }

    /**
     * Identified if given role is an instructor or admin role.
     * @param role - The role to check.
     * @return True if conditions are met.
     */
    private boolean isInstructorOrAdmin(Role role) {
        role.type == RoleType.ADMIN || role.type == RoleType.INSTRUCTOR
    }

    private boolean courseExists(String course_id) { Course.findByCrn(course_id) != null }

    /**
     * Removes a student from a course. Catching errors and returning results.
     * @param course - The course to delete from.
     * @param user - The user to remove.
     * @param result - The optional result to store data in.
     * @return A result object.
     */
    private QueryResult<Course> removeFromStudents(Course course, User user, QueryResult<Course> result = new QueryResult<>(success: true)) {
        try {
            course.removeFromStudents(user)
            course.save(flush: true, failOnError: true)
        } catch (Exception e) {
            e.printStackTrace()
            QueryResult.fromHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR, result)
        }
        result
    }

    Course make(User instructor, String courseName, String crn) {
        Course course = new Course(name: courseName, CRN: crn)
        //TODO: connect course with instructor
        course.save(flush: true, failOnError: true)
    }

    void delete(int courseId) {
        Course course = Course.get(courseId)
        if (course != null) {
            course.delete()
        }
    }

    /**
     * Checks if the user has instructor access for the given course
     * @param user - the user to check for instructor access
     * @param course - the course to check
     * @return True if the user has instructor accss to the course
     */
    private boolean hasInstructorAccess(User user, Course course) {
        if (user.role.type == RoleType.ADMIN) {
            return true
        }
        if (user.role.type == RoleType.INSTRUCTOR && isInstructorOf(user, course)) {
            return true
        }
        return false
    }

    /**
     * Checks if the user is an instructor of the course
     * @param user - the user to check
     * @param course - the course to check
     * @return true if the user is an instructor of the course
     */
    boolean isInstructorOf(User user, Course course) {
        user != null && course != null && course.instructorId == user.id
    }

    void addStudent(int courseId, User student) {
        Course course = Course.get(courseId)
        if (course != null) {
            //TODO: add student to course
        }
    }

    boolean containsStudent(int courseId, User student) {
        Course course = Course.get(courseId)
        if (course != null) {
            //TODO: check if course contains student
        }
        return false
    }

}
