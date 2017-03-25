package edu.oswego.cs.lakerpolling.domains

import edu.oswego.cs.lakerpolling.util.RoleType

class Role {
    enum RoleType {
        INSTRUCTOR, ADMIN, STUDENT
    }

    RoleType type

    static belongsTo = [user: User]
    static constraints = {
        type blank: false
    }
}
