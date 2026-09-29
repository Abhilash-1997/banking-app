package com.novabank.identity.internal.web;

import com.novabank.identity.api.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
class MeController {

    @GetMapping("/me")
    AuthenticatedUser me(@AuthenticationPrincipal Jwt jwt) {
        return new AuthenticatedUser(jwt.getSubject(), jwt.getClaimAsString("preferred_username"));
    }
}
