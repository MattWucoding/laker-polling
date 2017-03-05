package edu.oswego.cs.lakerpolling.domains


class Course {
    String name
    String crn

    static hasMany = [users: User]
    static belongsTo = [semester: Semester]

    static mapping = {
        id column: 'crn'
    }

    static constraints = {
        name nullable: true
        crn nullable: false
        users nullable: true
    }
}
