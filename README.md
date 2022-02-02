# Reproducer

A single execution of the integration tests will pass.

But hitting the endpoint under load (with parallel requests) will have it fail with error messages such as:

Request failed: java.lang.IllegalStateException: Interleaved operation detected! label: FruitsResource New Request id: 204 existing ID: 150
Request failed: java.lang.IllegalStateException: Interleaved operation detected! label: FruitsResource New Request id: 202 existing ID: 148

