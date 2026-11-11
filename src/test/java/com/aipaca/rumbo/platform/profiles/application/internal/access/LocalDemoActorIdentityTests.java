package com.aipaca.rumbo.platform.profiles.application.internal.access;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import static org.junit.jupiter.api.Assertions.*;

class LocalDemoActorIdentityTests {
    private final LocalDemoActorIdentity identity=new LocalDemoActorIdentity();
    @AfterEach void clear() { RequestContextHolder.resetRequestAttributes(); }
    @Test void locallySelectedTutorIsNotAdministrator() {
        var request=new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Rumbo-Demo-Role","tutor");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertEquals(LocalDemoActorIdentity.TUTOR_ACCOUNT_ID,identity.currentAccountId());
        assertTrue(identity.hasRole("TUTOR"));
        assertFalse(identity.hasRole("ADMIN"));
    }
    @Test void remoteRequestsCannotImpersonateDemoRoles() {
        var request=new MockHttpServletRequest();
        request.setRemoteAddr("192.0.2.4");
        request.addHeader("X-Rumbo-Demo-Role","driver");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThrows(ProfilesAccessException.class,identity::currentAccountId);
    }
    @Test void missingRoleIsRejected() {
        var request=new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThrows(ProfilesAccessException.class,identity::currentAccountId);
    }
}
