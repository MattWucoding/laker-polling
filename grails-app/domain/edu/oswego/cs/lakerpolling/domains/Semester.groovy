package edu.oswego.cs.lakerpolling.domains

import edu.oswego.cs.lakerpolling.util.Season


class Semester {
    String year
    Season season

    static hasMany = [courses: Course]

    static mapping = {
        version false
    }

    static constraints = {
        year nullable: false
        season nullable: false
        courses nullable: true
    }
}
