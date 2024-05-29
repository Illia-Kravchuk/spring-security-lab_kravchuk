package ua.kpi.its.lab.security.dto

import kotlinx.serialization.Serializable

@Serializable
data class EducationalInstitutionRequest(
    var name: String,
    var accreditationLevel: String,
    var address: String,
    var foundationDate: String,
    var facultiesCount: Int,
    var website: String,
    var hasMilitaryDepartment: Boolean,
    var disciplines: DisciplineRequest
)
@Serializable

data class EducationalInstitutionResponse(
    var id: Long,
    var name: String,
    var accreditationLevel: String,
    var address: String,
    var foundationDate: String,
    var facultiesCount: Int,
    var website: String,
    var hasMilitaryDepartment: Boolean,
    var disciplines: DisciplineResponse
)
@Serializable

data class DisciplineRequest(
    var name: String,
    var institution: String,
    var specialityCode: String,
    var semester: Int,
    var hoursCount: Int,
    var approvalDate: String,
    var hasExam: Boolean
)
@Serializable

data class DisciplineResponse(
    var id: Long,
    var name: String,
    var institution: String,
    var specialityCode: String,
    var semester: Int,
    var hoursCount: Int,
    var approvalDate: String,
    var hasExam: Boolean
)
