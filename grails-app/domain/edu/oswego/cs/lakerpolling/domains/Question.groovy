package edu.oswego.cs.lakerpolling.domains

/**
 * Auth token model
 */
class Question {

    String question
    int[] answers

    static hasMany = [answers: Answer]
    static belongsTo = [quiz: Quiz]

    static mapping = {
        version false
    }

    static constraints = {
        answers blank: false
        question blank: false
    }
}
