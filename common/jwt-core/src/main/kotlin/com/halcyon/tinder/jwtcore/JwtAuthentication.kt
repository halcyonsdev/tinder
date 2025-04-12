package com.halcyon.tinder.jwtcore

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

data class JwtAuthentication(
    private var authenticated: Boolean = true,
    var phoneNumber: String? = null,
    var token: String? = null,
) : Authentication {

    override fun getName(): String ?= phoneNumber
    override fun getAuthorities(): Collection<GrantedAuthority> = emptySet()
    override fun getCredentials(): Any ?= null
    override fun getDetails(): Any ?= null
    override fun getPrincipal(): Any ?= phoneNumber
    override fun isAuthenticated(): Boolean = authenticated
    override fun setAuthenticated(isAuthenticated: Boolean) {
        authenticated = isAuthenticated
    }

}