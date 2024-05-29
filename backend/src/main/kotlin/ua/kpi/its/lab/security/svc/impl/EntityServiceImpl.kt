package ua.kpi.its.lab.security.svc.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua.kpi.its.lab.security.dto.DisciplineResponse
import ua.kpi.its.lab.security.dto.EducationalInstitutionRequest
import ua.kpi.its.lab.security.dto.EducationalInstitutionResponse
import ua.kpi.its.lab.security.entity.Discipline
import ua.kpi.its.lab.security.entity.EducationalInstitution
import ua.kpi.its.lab.security.repo.EducationalInstitutionRepository
import ua.kpi.its.lab.security.svc.EducationalInstitutionService
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class EducationalInstitutionServiceImpl @Autowired constructor(
    private val repository: EducationalInstitutionRepository
) : EducationalInstitutionService {
    override fun create(institution: EducationalInstitutionRequest): EducationalInstitutionResponse {
        val newDiscipline = Discipline(
            name = institution.disciplines.name,
            institution = institution.disciplines.institution,
            specialityCode = institution.disciplines.specialityCode,
            semester = institution.disciplines.semester,
            hoursCount = institution.disciplines.hoursCount,
            approvalDate = stringToDate(institution.disciplines.approvalDate),
            hasExam = institution.disciplines.hasExam,
            educationalInstitution = null // This will be set later
        )
        var newInstitution = EducationalInstitution(
            name = institution.name,
            accreditationLevel = institution.accreditationLevel,
            address = institution.address,
            foundationDate = stringToDate(institution.foundationDate),
            facultiesCount = institution.facultiesCount,
            website = institution.website,
            hasMilitaryDepartment = institution.hasMilitaryDepartment,
            disciplines = newDiscipline
        )
        newDiscipline.educationalInstitution = newInstitution
        newInstitution = this.repository.save(newInstitution)
        return this.institutionEntityToDto(newInstitution)
    }

    override fun read(): List<EducationalInstitutionResponse> {
        return this.repository.findAll().map(this::institutionEntityToDto)
    }

    override fun readById(id: Long): EducationalInstitutionResponse {
        val institution = this.getInstitutionById(id)
        return this.institutionEntityToDto(institution)
    }

    override fun updateById(id: Long, institution: EducationalInstitutionRequest): EducationalInstitutionResponse {
        val oldInstitution = this.getInstitutionById(id)
        val newDiscipline = Discipline(
            name = institution.disciplines.name,
            institution = institution.disciplines.institution,
            specialityCode = institution.disciplines.specialityCode,
            semester = institution.disciplines.semester,
            hoursCount = institution.disciplines.hoursCount,
            approvalDate = stringToDate(institution.disciplines.approvalDate),
            hasExam = institution.disciplines.hasExam,
            educationalInstitution = oldInstitution
        )
        oldInstitution.apply {
            name = institution.name
            accreditationLevel = institution.accreditationLevel
            address = institution.address
            foundationDate = stringToDate(institution.foundationDate)
            facultiesCount = institution.facultiesCount
            website = institution.website
            hasMilitaryDepartment = institution.hasMilitaryDepartment
            disciplines = newDiscipline
        }
        val updatedInstitution = this.repository.save(oldInstitution)
        return this.institutionEntityToDto(updatedInstitution)
    }

    override fun deleteById(id: Long): EducationalInstitutionResponse {
        val institution = this.getInstitutionById(id)
        this.repository.delete(institution)
        return this.institutionEntityToDto(institution)
    }

    private fun getInstitutionById(id: Long): EducationalInstitution {
        return this.repository.findById(id).orElseThrow {
            IllegalArgumentException("Educational Institution not found by id = $id")
        }
    }

    private fun institutionEntityToDto(institution: EducationalInstitution): EducationalInstitutionResponse {
        return EducationalInstitutionResponse(
            id = institution.id,
            name = institution.name,
            accreditationLevel = institution.accreditationLevel,
            address = institution.address,
            foundationDate = dateToString(institution.foundationDate),
            facultiesCount = institution.facultiesCount,
            website = institution.website,
            hasMilitaryDepartment = institution.hasMilitaryDepartment,
            disciplines = this.disciplineEntityToDto(institution.disciplines)
        )
    }

    private fun disciplineEntityToDto(discipline: Discipline): DisciplineResponse {
        return DisciplineResponse(
            id = discipline.id,
            name = discipline.name,
            institution = discipline.institution,
            specialityCode = discipline.specialityCode,
            semester = discipline.semester,
            hoursCount = discipline.hoursCount,
            approvalDate = dateToString(discipline.approvalDate),
            hasExam = discipline.hasExam
        )
    }

    private fun dateToString(date: Date): String {
        val instant = date.toInstant()
        val dateTime = instant.atOffset(ZoneOffset.UTC).toLocalDateTime()
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
    }

    private fun stringToDate(date: String): Date {
        return try {
            val dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
            val instant = dateTime.toInstant(ZoneOffset.UTC)
            Date.from(instant)
        } catch (e: Exception) {
            Date() // Return current date as fallback
        }
    }
}
