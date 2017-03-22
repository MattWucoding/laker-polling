package edu.oswego.cs.lakerpolling.domains

/**
 * Auth token model
 */
class Quiz {
    int id
    int timeLimit

    static hasMany = [questions: Question]
    static belongsTo = [instructor: User]

    static mapping = {
        id column: 'id'
    }

    static constraints = {
        id unique: true, blank: false
        timeLimit nullable: true
    }
}
