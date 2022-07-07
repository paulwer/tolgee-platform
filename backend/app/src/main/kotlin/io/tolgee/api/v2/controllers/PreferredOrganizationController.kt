/*
 * Copyright (c) 2020. Tolgee
 */

package io.tolgee.api.v2.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import io.tolgee.api.v2.hateoas.organization.OrganizationModel
import io.tolgee.api.v2.hateoas.organization.OrganizationModelAssembler
import io.tolgee.model.views.OrganizationView
import io.tolgee.security.AuthenticationFacade
import io.tolgee.service.OrganizationRoleService
import io.tolgee.service.UserPreferencesService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping(value = ["/v2/preferred-organization"])
@Tag(name = "Organizations")
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class PreferredOrganizationController(
  private val authenticationFacade: AuthenticationFacade,
  private val organizationRoleService: OrganizationRoleService,
  private val userPreferencesService: UserPreferencesService,
  private val organizationModelAssembler: OrganizationModelAssembler,
) {
  @GetMapping("")
  @Operation(summary = "Returns preferred organization")
  fun getPreferred(): OrganizationModel? {
    val preferences = userPreferencesService.findOrCreate(authenticationFacade.userAccount.id)
    val preferredOrganization = preferences.preferredOrganization
    val roleType = organizationRoleService.findType(preferredOrganization.id)
    val view = OrganizationView.of(preferredOrganization, roleType)
    return this.organizationModelAssembler.toModel(view)
  }
}