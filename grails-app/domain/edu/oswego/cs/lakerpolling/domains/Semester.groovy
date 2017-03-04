package edu.oswego.cs.lakerpolling.domains

import edu.oswego.cs.lakerpolling.util.Season


class Semester {
    String year
    Season season

    static hasMany = [course: Course]

    static mapping = {
        version false
    }

    static constraints = {
        year nullable: false
        season nullable: false
    }
}

