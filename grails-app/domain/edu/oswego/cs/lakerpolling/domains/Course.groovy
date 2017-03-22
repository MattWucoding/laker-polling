package edu.oswego.cs.lakerpolling.domains


class Course {
    String name
    String crn

    static hasMany = [students: User]
    static belongsTo = [instructor: User]

    static mapping = {
    }

    static constraints = {
        name nullable: false
        crn nullable: false
        students nullable: true
    }
}
