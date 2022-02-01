package org.acme.hibernate.reactive;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import io.smallrye.mutiny.Uni;

@Path("fruits")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FruitMutinyResource {

    @Inject
    DelayHelper helper;

    @GET
    public Uni<String> get(@HeaderParam("RID") int requestId) {

        VertxAssociatedState.checkVertxContext();
        final AtomicResource statefulSpan = VertxAssociatedState.beginStatefulSpan( "FruitsResource", requestId );
        statefulSpan.open();

        return helper.laterCompletedUni()
                .chain( () -> {
                    VertxAssociatedState.checkVertxContext();
                    VertxAssociatedState.continueStatefulSpan(statefulSpan).close();
                    VertxAssociatedState.terminateStatefulSpan();
                    return Uni.createFrom().item( "OK - delayed" );
                } );
    }

}
