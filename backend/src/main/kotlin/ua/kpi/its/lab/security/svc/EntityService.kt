package ua.kpi.its.lab.security.svc

import ua.kpi.its.lab.security.dto.EducationalInstitutionRequest
import ua.kpi.its.lab.security.dto.EducationalInstitutionResponse

interface EducationalInstitutionService {
    fun create(institution: EducationalInstitutionRequest): EducationalInstitutionResponse
    fun read(): List<EducationalInstitutionResponse>
    fun readById(id: Long): EducationalInstitutionResponse
    fun updateById(id: Long, institution: EducationalInstitutionRequest): EducationalInstitutionResponse
    fun deleteById(id: Long): EducationalInstitutionResponse
}
