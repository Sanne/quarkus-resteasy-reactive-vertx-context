package org.acme.hibernate.reactive;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class FruitsEndpointTest {

	@Test
	public void testFruitsEndpoint() {
		given()
				.when()
				.get( "/fruits" )
				.then()
				.statusCode( 200 )
				.body(
						containsString( "OK - delayed" )
				);
	}

}
