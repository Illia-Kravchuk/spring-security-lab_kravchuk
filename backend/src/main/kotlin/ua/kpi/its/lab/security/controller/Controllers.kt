package ua.kpi.its.lab.security.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.web.bind.annotation.*
import ua.kpi.its.lab.security.dto.EducationalInstitutionRequest
import ua.kpi.its.lab.security.dto.EducationalInstitutionResponse
import ua.kpi.its.lab.security.svc.EducationalInstitutionService
import java.time.Instant

@RestController
@RequestMapping("/institutions")
class EducationalInstitutionController @Autowired constructor(
    private val institutionService: EducationalInstitutionService
) {
    @GetMapping(path = ["", "/"])
    fun institutions(): List<EducationalInstitutionResponse> = institutionService.read()

    @GetMapping("{id}")
    fun readInstitution(@PathVariable("id") id: Long): ResponseEntity<EducationalInstitutionResponse> {
        return wrapNotFound { institutionService.readById(id) }
    }

    @PostMapping(path = ["", "/"])
    fun createInstitution(@RequestBody institution: EducationalInstitutionRequest): EducationalInstitutionResponse {
        return institutionService.create(institution)
    }

    @PutMapping("{id}")
    fun updateInstitution(
        @PathVariable("id") id: Long,
        @RequestBody institution: EducationalInstitutionRequest
    ): ResponseEntity<EducationalInstitutionResponse> {
        return wrapNotFound { institutionService.updateById(id, institution) }
    }

    @DeleteMapping("{id}")
    fun deleteInstitution(
        @PathVariable("id") id: Long
    ): ResponseEntity<EducationalInstitutionResponse> {
        return wrapNotFound { institutionService.deleteById(id) }
    }

    fun <T> wrapNotFound(call: () -> T): ResponseEntity<T> {
        return try {
            val result = call()
            ResponseEntity.ok(result)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.notFound().build()
        }
    }
}
@RestController
@RequestMapping("/auth")
class AuthenticationTokenController @Autowired constructor(
    private val encoder: JwtEncoder
) {
    private val authTokenExpiry: Long = 3600L // in seconds

    @PostMapping("token")
    fun token(auth: Authentication): String {
        val now = Instant.now()
        val scope = auth
            .authorities
            .joinToString(" ", transform = GrantedAuthority::getAuthority)
        val claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(authTokenExpiry))
            .subject(auth.name)
            .claim("scope", scope)
            .build()
        return encoder.encode(JwtEncoderParameters.from(claims)).tokenValue
    }
}
