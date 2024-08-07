package com.example.patientManageApp.domain.entity

data class PatientEntity(
    val name: String = "",
    val birth: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (javaClass != other.javaClass) return false
        val newPatient = other as PatientEntity
        return name == newPatient.name && birth == newPatient.birth
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + birth.hashCode()
        return result
    }
}

