package org.acme.hibernate.reactive;

import java.util.concurrent.atomic.AtomicInteger;

public final class AtomicResource {

	//0 - new instance
	//1 - opened
	//2 - closed
	private final AtomicInteger state = new AtomicInteger( 0 );
	private final Thread initialThread = Thread.currentThread();
	private final int requestId;

	public AtomicResource(int requestId) {
		this.requestId = requestId;
	}

	public void open() {
		final boolean done = state.compareAndSet( 0, 1 );
		if ( !done ) {
			throw new IllegalStateException( "Atomic opening of resource failed" );
		}
		checkThreadMatch();
	}

	public void use() {
		final int stateN = state.get();
		if ( stateN != 1 ) {
			throw new IllegalStateException( "Attempt to use() a resource while it wasn't open; current state: " + stateN );
		}
		checkThreadMatch();
	}

	public void close() {
		final boolean done = state.compareAndSet( 1, 2 );
		if ( !done ) {
			throw new IllegalStateException( "Atomic closing of resource failed" );
		}
		checkThreadMatch();
	}

	private void checkThreadMatch() {
		if ( Thread.currentThread() != initialThread ) {
			throw new IllegalStateException( "Thread mismatch" );
		}
	}

	public int getRequestId() {
		return requestId;
	}
}
