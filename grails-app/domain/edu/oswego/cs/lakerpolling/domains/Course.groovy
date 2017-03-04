package edu.oswego.cs.lakerpolling.domains


class Course {
    String name
    String crn

    static hasMany = [user: User]

    static mapping = {
        id column: 'crn'
    }

    static constraints = {
        name nullable: true
        crn nullable: false
    }
}
