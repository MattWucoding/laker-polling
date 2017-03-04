package edu.oswego.cs.lakerpolling.domains


class Course {
    String name
    String CRN

    static mapping = {
    }

    static constraints = {
        name nullable: true
    }
}
