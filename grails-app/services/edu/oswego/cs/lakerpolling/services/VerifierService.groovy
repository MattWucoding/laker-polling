package edu.oswego.cs.lakerpolling.services

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import edu.oswego.cs.lakerpolling.domains.AuthToken
import edu.oswego.cs.lakerpolling.domains.User
import edu.oswego.cs.lakerpolling.util.Pair
import grails.core.GrailsApplication
import grails.transaction.Transactional
import org.springframework.http.HttpStatus

@Transactional
class VerifierService {

    class AuthErrors {
        final static String TOKEN_VERIFICATION = 'Token verification error'
        final static String TOKEN_INTEGRITY = 'Token integrity error'
        final static String UNVERIFIED_EMAIL = 'Unverified user email'
        final static String NON_OSWEGO_EMAIL = 'Non @oswego.edu email'
    }

    class VerifiedData {

        private boolean success = true
        private int errorCode
        private String message

        private GoogleIdToken idToken

        boolean getSuccess() {
            return success
        }

        int getErrorCode() {
            return errorCode
        }

        String getMessage() {
            return message
        }

        GoogleIdToken getIdToken() {
            return idToken
        }
    }

    GrailsApplication grailsApplication

    /**
     * Method to get verification results for a given id token string.
     * @param idTokenString The raw id token string to be verified.
     * @return An object containing information on the verification of the candidate.
     */
    VerifiedData getVerifiedResults(String idTokenString) {
        VerifiedData data = new VerifiedData()
        verifyIdToken(idTokenString, data)
        verifyIdTokenIntegrity(data)
        verifyEmail(data)
        data
    }

    /***
     * Method to verify the validity of the given idToken string via google's api.
     * @param idTokenString The raw id to check.
     * @param data The data object to push results onto.
     */
    private void verifyIdToken(String idTokenString, VerifiedData data) {

        if (!data.success) {
            return
        }

        if (idTokenString == null) {
            data.success = false
            data.message = "Missing parameter id token"
            data.errorCode = HttpStatus.BAD_REQUEST.value()
            return
        }

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new JacksonFactory())
                .setAudience([grailsApplication.config.getProperty("googleauth.clientId")])
                .build()

        GoogleIdToken temp

        try {
            temp = verifier.verify(idTokenString)
        } catch (Exception e) {
            temp = null
        }

        if (temp == null) {
            data.success = false
            data.message = AuthErrors.TOKEN_VERIFICATION
            data.errorCode = HttpStatus.BAD_REQUEST.value()
        } else {
            data.idToken = temp
        }

    }

    /**
     * Method to check the integrity of the google id token data by checking against
     * expected issuer and audience.
     * @param data The data object to push results onto.
     */
    private void verifyIdTokenIntegrity(VerifiedData data) {

        if (!data.success || data.idToken == null) {
            return
        }

        GoogleIdToken token = data.idToken
        def passed = false

        if (token.verifyAudience([grailsApplication.config.getProperty("googleauth.clientId")])) {
            if (token.verifyIssuer(grailsApplication.config.getProperty("googleauth.issuer"))) {
                passed = true
            }
        }

        if (!passed) {
            data.success = false
            data.message = AuthErrors.TOKEN_INTEGRITY
            data.errorCode = HttpStatus.BAD_REQUEST.value()
        }

    }

    /**
     * Verifies the email on the profile
     * @param data The data object to push results onto.
     */
    private void verifyEmail(VerifiedData data) {
        if (!data.success || data.idToken == null) {
            return
        }
        Payload payload = data.idToken.payload
        Boolean passed = false

        if (payload.getEmailVerified()) {
            if (payload.getEmail().indexOf("oswego.edu") == -1)
                data.message = AuthErrors.NON_OSWEGO_EMAIL
            else
                passed = true
        } else {
            data.message = AuthErrors.UNVERIFIED_EMAIL
        }

        if (!passed) {
            data.success = passed
            data.errorCode = HttpStatus.BAD_REQUEST.value()
        }
    }


}
