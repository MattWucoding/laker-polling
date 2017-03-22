package edu.oswego.cs.lakerpolling.domains

/**
 * Auth token model
 */
class Answer {

    int[] answer

    static belongsTo = [user: User, question: Question]

    static mapping = {
        version false
    }

    static constraints = {
        answer blank: false
    }
}
