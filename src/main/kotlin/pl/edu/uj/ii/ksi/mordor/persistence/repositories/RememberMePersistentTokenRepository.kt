package pl.edu.uj.ii.ksi.mordor.persistence.repositories

import java.util.Date
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.stereotype.Repository
import pl.edu.uj.ii.ksi.mordor.persistence.entities.RememberMeToken

@Repository
class RememberMePersistentTokenRepository(
    val userRepository: UserRepository,
    val rememberMeTokenRepository: RememberMeTokenRepository
) : PersistentTokenRepository {
    override fun updateToken(series: String, tokenValue: String, lastUsed: Date) {
        val tokenOptional = rememberMeTokenRepository.findById(series)
        if (tokenOptional.isPresent) {
            val token = tokenOptional.get()
            token.value = tokenValue
            token.date = lastUsed
            rememberMeTokenRepository.save(token)
        }
    }

    override fun getTokenForSeries(seriesId: String): PersistentRememberMeToken? {
        return rememberMeTokenRepository.findById(seriesId).orElse(null)?.getToken()
    }

    override fun removeUserTokens(username: String) {
        rememberMeTokenRepository.findByUserName(username)
            .forEach { token -> rememberMeTokenRepository.delete(token) }
    }

    override fun createNewToken(token: PersistentRememberMeToken) {
        val tokenEntity = RememberMeToken(token.series, token.tokenValue, token.date, token.username)
        rememberMeTokenRepository.save(tokenEntity)
    }
}
