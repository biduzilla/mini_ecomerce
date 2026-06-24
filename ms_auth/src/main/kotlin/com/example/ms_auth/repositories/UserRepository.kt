package com.example.ms_auth.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import com.example.ms_auth.models.User
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    @Query("select u from User u where u.email = :email")
    fun findByEmail(@Param("email") email: String): User?
    @Query("""
       select 
            case 
                when count(u) > 0 
                then true 
                else false 
            end 
        from User u 
        where u.email = :email 
    """)
    fun existsByEmail(email: String): Boolean
}