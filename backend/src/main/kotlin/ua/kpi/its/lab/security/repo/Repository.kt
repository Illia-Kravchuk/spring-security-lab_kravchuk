package ua.kpi.its.lab.security.repo

import org.springframework.data.jpa.repository.JpaRepository
import ua.kpi.its.lab.security.entity.Discipline
import ua.kpi.its.lab.security.entity.EducationalInstitution

interface EducationalInstitutionRepository : JpaRepository<EducationalInstitution, Long>

interface DisciplineRepository : JpaRepository<Discipline, Long>
