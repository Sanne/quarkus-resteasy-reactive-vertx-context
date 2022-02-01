package org.acme.hibernate.reactive;

import java.util.concurrent.atomic.AtomicInteger;

import io.smallrye.mutiny.Uni;

import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

@ApplicationScoped
public class RequestFilter {

	@Inject
	DelayHelper helper;

	private static final AtomicInteger GLOBAL_REQUEST_ID = new AtomicInteger();

	@ServerRequestFilter
	public Uni<Response> filter(ContainerRequestContext requestContext) {

		final int RID = GLOBAL_REQUEST_ID.incrementAndGet();
		requestContext.getHeaders().add( "RID", String.valueOf( RID ) );

		VertxAssociatedState.checkVertxContext();
		final AtomicResource statefulSpan = VertxAssociatedState.beginStatefulSpan( "Request Filter", RID );
		statefulSpan.open();

		return helper.laterCompletedUni()
				.chain( () -> {
					VertxAssociatedState.checkVertxContext();
					VertxAssociatedState.continueStatefulSpan( statefulSpan ).close();
					VertxAssociatedState.terminateStatefulSpan();
					return Uni.createFrom().nullItem();
				} );

	}

}
