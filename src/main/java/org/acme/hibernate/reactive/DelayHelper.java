package org.acme.hibernate.reactive;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;

@ApplicationScoped
public class DelayHelper {

	private static final long MS_DELAY = 500l;

	@Inject
	Vertx vertx;

	/**
	 * Creates a Uni which returns
	 * @return
	 */
	Uni<Object> laterCompletedUni() {
		return Uni.createFrom().emitter( em -> vertx.setTimer( MS_DELAY, id -> em.complete( null ) ) );
	}

}
