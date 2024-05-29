package ua.kpi.its.lab.security.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "educational_institutions")
class EducationalInstitution(
    @Column
    var name: String,

    @Column
    var accreditationLevel: String,

    @Column
    var address: String,

    @Column
    var foundationDate: Date,

    @Column
    var facultiesCount: Int,

    @Column
    var website: String,

    @Column
    var hasMilitaryDepartment: Boolean,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "discipline_id", referencedColumnName = "id")
    var disciplines: Discipline
) : Comparable<EducationalInstitution> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = -1

    override fun compareTo(other: EducationalInstitution): Int {
        val equal = this.name == other.name && this.foundationDate.time == other.foundationDate.time
        return if (equal) 0 else 1
    }

    override fun toString(): String {
        return "EducationalInstitution(name=$name, foundationDate=$foundationDate, disciplines=$disciplines)"
    }
}

@Entity
@Table(name = "disciplines")
class Discipline(
    @Column
    var name: String,

    @Column
    var institution: String,

    @Column
    var specialityCode: String,

    @Column
    var semester: Int,

    @Column
    var hoursCount: Int,

    @Column
    var approvalDate: Date,

    @Column
    var hasExam: Boolean,

    @OneToOne(mappedBy = "disciplines")
    var educationalInstitution: EducationalInstitution? = null
) : Comparable<Discipline> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = -1

    override fun compareTo(other: Discipline): Int {
        val equal = this.name == other.name && this.approvalDate.time == other.approvalDate.time
        return if (equal) 0 else 1
    }

    override fun toString(): String {
        return "Discipline(name=$name, approvalDate=$approvalDate)"
    }
}
