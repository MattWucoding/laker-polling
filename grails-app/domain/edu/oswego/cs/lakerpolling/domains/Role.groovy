package edu.oswego.cs.lakerpolling.domains

import edu.oswego.cs.lakerpolling.util.RoleType

class Role {
    RoleType type

    static belongsTo = [user: User]
    static constraints = {}
}
