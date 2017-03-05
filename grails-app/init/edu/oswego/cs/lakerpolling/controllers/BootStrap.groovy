package edu.oswego.cs.lakerpolling.controllers

class BootStrap {

    def init = { servletContext ->
        // 5 Example Users
        new User(firstName: "Josh", lastName: "Post", email: "jpost@oswego.edu",
                imageUrl: "http://cdn.business2community.com/wp-content/uploads/2016/03/Vd3MJo.jpg",
                role: RoleType.ADMIN,authToken: null).save()
        new User(firstName: "Keith", lastName: "Martin", email: "kmartin5@oswego.edu",
                imageUrl: null,role: RoleType.INSTRUCTOR,authToken: null).save()
        new User(firstName: "Akeem", lastName: "Davis", email: "adavis20@oswego.edu",
                imageUrl: null,role: RoleType.INSTRUCTOR,authToken: null).save()
        new User(firstName: "Ricky", lastName: "Rojas", email: "rrojas@oswego.edu",
                imageUrl: null, role: null,authToken: null,course: Course.findAllByName("csc480")).save()
        new User(firstName: "Matt", lastName: "Wu", email: "jwu5@oswego.edu",
                imageUrl: null,role: null,authToken: null,course: Course.findAllByName("csc480")).save()

        //Example Semester
        new Semester(year: 2017, season: Season.SPRING).save()

        //Example Course
        new Course(name: csc480, crn: 12345, users: User.findAllByRole(null), semester: Semester.findBySeasonAndYear(Season.SPRING,2017)).save()


    }
    def destroy = {
    }
}
