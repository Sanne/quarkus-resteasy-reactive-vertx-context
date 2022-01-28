package org.acme.hibernate.reactive;

import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class RequestFilter {

    @Inject
    Mutiny.SessionFactory sf;

    @ServerRequestFilter
    public Uni<Response> filter(ContainerRequestContext requestContext) {
        return sf.withSession(s ->
                s.createNativeQuery("SELECT 1")
                        .getSingleResult()
                        .replaceWith(() -> null)
        );
    }
}
