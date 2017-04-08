package edu.oswego.cs.lakerpolling.controllers

import edu.oswego.cs.lakerpolling.domains.Attendance
import edu.oswego.cs.lakerpolling.domains.Attendee
import edu.oswego.cs.lakerpolling.domains.AuthToken
import edu.oswego.cs.lakerpolling.domains.Course
import edu.oswego.cs.lakerpolling.domains.Role
import edu.oswego.cs.lakerpolling.domains.User
import edu.oswego.cs.lakerpolling.util.RoleType

class BootStrap {

    def init = { servletContext ->

        /* Define students */
        User a = new User(firstName: "Jason", lastName: "Parker", email: "jpark@gmail.com", imageUrl: "Some image")
        a.setRole(new Role(type: RoleType.STUDENT))
        a.setAuthToken(new AuthToken(subject: 'sub-a', accessToken: 'aa'))
        a.save(flush: true, failOnError: true)

        User b = new User(firstName: "Peter", lastName: "Swanson", email: "pswan@coolpeople.com", imageUrl: "coolest")
        b.setRole(new Role(type: RoleType.STUDENT))
        b.setAuthToken(new AuthToken(subject: 'sub-b', accessToken: 'bb'))
        b.save(flush: true, failOnError: true)

        User stu = new User(firstName: "Zack", lastName: "Brown", email: "zb@gmail.com", imageUrl: "The greatest image.jpg")
        stu.setRole(new Role(type: RoleType.STUDENT))
        stu.setAuthToken(new AuthToken(subject: 'sub-stu-1', accessToken: 'cc'))
        stu.save(flush: true, failOnError: true)

        User stu2 = new User(firstName: "Stephen", lastName: "Forgot", email: "steph@gmail.com", imageUrl: "The greatest image.jpg")
        stu2.setRole(new Role(type: RoleType.STUDENT))
        stu2.setAuthToken(new AuthToken(subject: 'sub-stu-2', accessToken: 'dd'))
        stu2.save(flush: true, failOnError: true)

        User michael = new User(firstName: "Michael", lastName: "Cavataio", email: "mcavatai@oswego.edu", imageUrl: "http://media.salon.com/2015/01/chrissy_teigen.jpg")
        michael.setRole(new Role(type: RoleType.INSTRUCTOR))
//        michael.setAuthToken(new AuthToken(subject: "michael-stu", accessToken: "ee"))
        michael.save(flush: true, failOnError: true)

        User max = new User(firstName: "Max", lastName: "Sokolovsky", email: "msokolov@oswego.edu", imageUrl: "http://media.salon.com/2015/01/chrissy_teigen.jpg")
        max.setRole(new Role(type: RoleType.STUDENT))
        max.save(flush: true, failOnError: true)
        
        User matt = new User(firstName: "Jingchi", lastName: "Wu", email: "jwu5@oswego.edu", imageUrl: "http://media.salon.com/2015/01/chrissy_teigen.jpg")
        matt.setRole(new Role(type: RoleType.STUDENT))
        matt.save(flush: true, failOnError: true)
        
        User mike = new User(firstName: "Mike", lastName: "Mekker", email: "mmekker@oswego.edu", imageUrl: "http://media.salon.com/2015/01/chrissy_teigen.jpg")
        mike.setRole(new Role(type: RoleType.INSTRUCTOR))
        mike.save(flush: true, failOnError: true)

        User paul = new User(firstName: "Paul", lastName: "Kwoyelo", email: "pkwoyelo@oswego.edu", imageUrl: "http://media.salon.com/2015/01/chrissy_teigen.jpg")
        paul.setRole(new Role(type: RoleType.INSTRUCTOR))
        paul.save(flush: true, failOnError: true)

        User jeff = new User(email: "jregistr@oswego.edu")
        jeff.setRole(new Role(type: RoleType.ADMIN))
        jeff.save(flush: true, failOnError: true)

        /* End students*/

        /* instructors */
        User inst1 = new User(email: "bastian.tenbergen@oswego.edu")
        inst1.setRole(new Role(type: RoleType.INSTRUCTOR))
        inst1.setAuthToken(new AuthToken(accessToken: "inst-1", subject: "inst-1-subj"))
        inst1.save(flush: true, failOnError: true)

        User inst2 = new User(email: "christopher.harris@oswego.edu")
        inst2.setRole(new Role(type: RoleType.INSTRUCTOR))
        inst2.save(flush: true, failOnError: true)

        User tyler = new User(email: "tmoson@oswego.edu")
        tyler.setRole(new Role(type: RoleType.INSTRUCTOR))
        tyler.save(flush: true, failOnError: true)

        User admin = new User(firstName: "admin", lastName: "admin", email: "cooladmin@gmail.com", imageUrl: "cool")
        admin.setRole(new Role(type: RoleType.ADMIN))
        admin.setAuthToken(new AuthToken(subject: "sub-ad-1", accessToken: "ad1"))
        admin.save(flush: true, failOnError: true)

        /*End instructors*/


        /*Courses*/
        Course csc480 = new Course(name: "CSC 480", crn: 11111, instructor: inst1)
        csc480.addToStudents(a)
        csc480.addToStudents(b)
        csc480.addToStudents(michael);
        csc480.addToStudents(max);
        csc480.save(flush: true, failOnError: true)


        Course hci521 = new Course(name:  "HCI 521", crn: 22222, instructor: inst2)
        hci521.addToStudents(stu)
        hci521.addToStudents(stu2)
        hci521.addToStudents(michael)
        hci521.addToStudents(max)
        hci521.addToStudents(matt)
        hci521.save(flush: true, failOnError: true)

        Course csc212 = new Course(name: "CSC 212", crn: 123456, instructor: tyler)
        csc212.addToStudents(stu)
        csc212.addToStudents(stu2)
        csc212.addToStudents(michael)
        csc212.save(flush: true, failOnError: true)
        /*End courses*/

        /*Attendance*/
        Attendee brandon = new Attendee(attended: true, student: stu)
        Date someDate = new Date("1/22/91")
        Attendance something = new Attendance(date: someDate, course: csc480)
        something.addToAttendees(brandon)
        something.save(flush: true, failOnError: true)
        brandon.save(flusth: true, failOnError: true)

    }

    def destroy = {
    }
}
