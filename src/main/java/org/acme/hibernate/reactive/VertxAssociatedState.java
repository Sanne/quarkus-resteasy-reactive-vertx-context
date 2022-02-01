package org.acme.hibernate.reactive;

import io.vertx.core.Context;
import io.vertx.core.Vertx;

public final class VertxAssociatedState {

	private static final Object CONTEXT_KEY = "K";

	static AtomicResource beginStatefulSpan(final String label, int requestId) {
		final io.vertx.core.Context context = getCurrentContext();
		AtomicResource local = context.getLocal( CONTEXT_KEY );
		//ugly but it helps to show problems with this approach
		if ( local == null ) {
			local = new AtomicResource(requestId);
			context.putLocal( CONTEXT_KEY, local );
		}
		else {
			final int existingRequestId = local.getRequestId();
			throw new IllegalStateException( "Interleaved operation detected! label: " + label + " New Request id: " + requestId + " existing ID: " + existingRequestId);
		}
		return local;
	}

	private static Context getCurrentContext() {
		final Context currentContext = Vertx.currentContext();
		if ( currentContext == null ) {
			throw new IllegalStateException( "Vert.x context lost?! Thread: " + Thread.currentThread().getName() );
		}
		return currentContext;
	}

	static AtomicResource continueStatefulSpan(final AtomicResource expectedCurrentStatefulSpan) {
		final io.vertx.core.Context context = getCurrentContext();
		final AtomicResource storedStateful = context.getLocal( CONTEXT_KEY );
		//ugly but it helps to show problems with this approach
		if ( storedStateful == null ) {
			throw new IllegalStateException( "Stateful resource not found in context! Concurrently removed?" );
		}
		if ( storedStateful != expectedCurrentStatefulSpan ) {
			throw new IllegalStateException( "Stateful span found, but doesn't match the expected one" );
		}
		return storedStateful;
	}

	public static void checkVertxContext() {
		if ( !Context.isOnVertxThread() ) {
			throw new IllegalStateException( "Not on Vert.x Thread" );
		}
		if ( Vertx.currentContext() == null ) {
			throw new IllegalStateException( "No Vert.x context found" );
		}
		if ( !Vertx.currentContext().isEventLoopContext() ) {
			throw new IllegalStateException(
					"Not running on the Vert.x eventloop" );
		}
	}

	public static void terminateStatefulSpan() {
		final io.vertx.core.Context context = getCurrentContext();
		context.removeLocal( CONTEXT_KEY );
	}
}
