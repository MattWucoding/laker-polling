package edu.oswego.cs.lakerpolling.domains


String name
    String crn

    static hasMany = [users: User]
    static belongsTo = [instructor: User]

    static mapping = {
        id column: 'crn'
    }

    static constraints = {
        name nullable: true
        crn nullable: false
        users nullable: true
    }
}
