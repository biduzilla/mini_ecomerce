package com.example.ms_auth.models

import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import com.example.ms_auth.dtos.UserDTO
import com.example.ms_auth.enums.RoleEnum
import org.hibernate.annotations.SQLDelete
import java.util.*

@Entity
@Table(name = "USERS", uniqueConstraints = [UniqueConstraint(columnNames = ["email", "deleted", "updatedAt"])])
@SQLRestriction("deleted <> true")
@SQLDelete(sql = "UPDATE USERS SET deleted = true, updated_at = NOW() WHERE id = ?")
data class User(
    @Id
    @GeneratedValue(GenerationType.UUID)
    var id: UUID? = null,
    var name: String,
    var email: String,
    var keyword: String = "",
    var role: RoleEnum = RoleEnum.CLIENT
) : BaseModel(), UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority(role.authority))

    override fun getPassword(): String? = keyword
    override fun getUsername(): String = email
}

fun User.toDTO(): UserDTO = UserDTO(
    id = id,
    name = name,
    email = email,
)
