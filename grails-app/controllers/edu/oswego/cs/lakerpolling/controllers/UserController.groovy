package edu.oswego.cs.lakerpolling.controllers

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import edu.oswego.cs.lakerpolling.domains.AuthToken
import edu.oswego.cs.lakerpolling.domains.User
import edu.oswego.cs.lakerpolling.services.UserService
import edu.oswego.cs.lakerpolling.services.VerifierService
import edu.oswego.cs.lakerpolling.util.Pair

class UserController {

    VerifierService verifierService
    UserService userService

    static responseFormats = ['json', 'xml']

    def all(String accessToken) {
        render accessToken
    }

    def findUser(String accessToken, String id) {
        render accessToken.concat(id)
    }

    /**
     * Verifies and takes action based on the given idToken query parameter.
     */
    def auth(String idToken) {
        VerifierService.VerifiedData data = verifierService.getVerifiedResults(idToken)

        if (data.success) {
            GoogleIdToken.Payload payload = data.idToken.payload
            String subj = payload.getSubject()
            String first = payload.get("given_name").toString()
            String last = payload.get("family_name").toString()
            String imageUrl = payload.get("picture").toString()
            String accessTokenHash = payload.getAccessTokenHash()
            String email = payload.getEmail()

            Optional<Pair<User, AuthToken>> optionalInfo = userService.getMakeOrUpdate(
                    subj, first, last, imageUrl, accessTokenHash, email
            )

            if (optionalInfo.isPresent()) {
                Pair<User, AuthToken> info = optionalInfo.get()
                render(view: 'authUser', model: [user: info.key, token: info.value])
            } else {
                render(view: '../error')
            }
        } else {
            render(view: '../failure', model: [errorCode: data.errorCode, message: data.message])
        }
    }


}
