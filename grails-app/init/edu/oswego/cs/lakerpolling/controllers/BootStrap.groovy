package edu.oswego.cs.lakerpolling.controllers

class BootStrap {
    def init = { servletContext ->
        // 5 Example Users
        new User(firstName: "Josh", lastName: "Post", email: "jpost@oswego.edu",
                imageUrl: "http://cdn.business2community.com/wp-content/uploads/2016/03/Vd3MJo.jpg",
                role: RoleType.ADMIN,authToken: null).save(flush:true)
        new User(firstName: "Keith", lastName: "Martin", email: "kmartin5@oswego.edu",
                imageUrl: null,role: RoleType.INSTRUCTOR,authToken: null).save(flush:true)
        new User(firstName: "Akeem", lastName: "Davis", email: "adavis20@oswego.edu",
                imageUrl: null,role: RoleType.INSTRUCTOR,authToken: null).save(flush:true)
        new User(firstName: "Ricky", lastName: "Rojas", email: "rrojas@oswego.edu",
                imageUrl: null, role: null,authToken: null,course: Course.findAllByCrn(12345)).save(flush:true)
        new User(firstName: "Matt", lastName: "Wu", email: "jwu5@oswego.edu",
                imageUrl: null,role: null,authToken: null,course: Course.findByCrn(12345)).save(flush:true)

        //Example Course
        new Course(name: csc480, crn: 12345, users: User.findAllByRole(null)).save(flush:true)

    }
    def destroy = {
    }
}
