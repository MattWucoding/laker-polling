package edu.oswego.cs.lakerpolling.services

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import edu.oswego.cs.lakerpolling.domains.AuthToken
import edu.oswego.cs.lakerpolling.domains.User
import edu.oswego.cs.lakerpolling.util.Pair
import grails.transaction.Transactional

@Transactional
class UserService {

    /**
     * Method to get, update or insert a user from the given payload.
     * @param payload - The payload to grab data from.
     * @return A pair containing a User and their associated AuthToken should all operations go successfully.
     */
    Optional<Pair<User, AuthToken>> getMakeOrUpdate(String subj, String first, String last, String imageUrl,
                                                    String accessTokenHash, String email) {
        User user
        AuthToken token

        token = AuthToken.findBySubject(subj)

        // We have a token for this user.
        if (token != null) {

            user = token.user
            if (user.email != email) {
                user.email = email
            }

            if (token.accessToken != accessTokenHash) {
                token.accessToken = accessTokenHash
            }

            user.save(flush: true, failOnError: true)
            token.save(flush: true, failOnError: true)

        } else {
            // this may be a pre-loaded account or this is a new user.

            //attempt to find them in the db by email
            user = User.findByEmail(email)

            if (user == null) {// didn't find it, so this is a new user.
                user = new User(firstName: first, lastName: last, imageUrl: imageUrl, email: email).save(flush: true, failOnError: true)
            } else {//found the user by email, this must be a pre-loaded account
                if (user.imageUrl == null) {
                    user.imageUrl = imageUrl
                }

                if (user.firstName == null) {
                    user.firstName = first
                }

                if (user.lastName == null) {
                    user.lastName = last
                }

                user.save(flush: true, failOnError: true)
            }

            //associate a token with the user
            token = new AuthToken(user: user, subject: subj, accessToken: accessTokenHash).save(flush: true, failOnError: true)
        }

        user != null ? Optional.of(new Pair<User, AuthToken>(user, token))
                : Optional.empty()

    }

}
