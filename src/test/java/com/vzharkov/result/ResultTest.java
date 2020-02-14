package com.vzharkov.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class ResultTest {

    @Test
    void successShouldReturnValue() {
        Result<String, Throwable> result = Result.success("test");

        assertTrue(result.isSuccess());
        assertTrue(result.value().isPresent());
        assertNotNull(result.value().get());
    }

    @Test
    void successShouldReturnItsValue() throws Throwable {
        Result<String, Throwable> result = Result.success("test");
        assertEquals("test", result.get());
    }

    @Test
    void failureShouldReturnError() {
        Result<String, Throwable> result = Result.failure(new Throwable());

        assertTrue(result.isFailure());
        assertTrue(result.error().isPresent());
        assertNotNull(result.error().get());
    }

    @Test
    void failureShouldThrowItsErrorWhenGet() {
        Result<String, Throwable> result = Result.failure(new Throwable());
        assertThrows(Throwable.class, result::get);
    }

    @Test
    void ofShouldReturnSuccessWhenValueIsNotNull() {
        Result<String, Throwable> result = Result.of("test");

        assertTrue(result.isSuccess());
        assertTrue(result.value().isPresent());
        assertEquals("test", result.value().get());
    }

    @Test
    void ofShouldThrowWhenValueIsNull() {
        assertThrows(Throwable.class, () -> {
            Result<String, Throwable> result =Result.of(null);
        });
    }

    @Test
    void ofNullableShouldReturnSuccessWhenValueIsNotNull() {
        Result<String, ? extends Throwable> result = Result.ofNullable("test");

        assertTrue(result.isSuccess());
        assertTrue(result.value().isPresent());
        assertEquals("test", result.value().get());
    }

    @Test
    void ofNullableShouldReturnFailureWhenValueIsNull() {
        Result<String, ? extends Throwable> result = Result.ofNullable(null);

        assertTrue(result.isFailure());
        assertTrue(result.error().isPresent());
    }

    @Test
    void mapShouldReturnSuccessWhenItsAnSuccess() {
        Result<Integer, Throwable> result1 = Result.success(5);
        Result<String, Throwable> result2 = result1.map(v -> Integer.toString(v));

        assertTrue(result2.isSuccess());
        assertEquals("5", result2.value().get());
    }

    @Test
    void mapShouldReturnFailureWhenItsAnFailure() {
        Result<Integer, Throwable> result1 = Result.failure(new Throwable());
        Result<String, Throwable> result2 = result1.map(v -> Integer.toString(v));

        assertTrue(result2.isFailure());
        assertEquals(result1.error().get(), result2.error().get());
    }

    @Test
    void mapShouldNotCallMapperWhenItsAnFailure() {
        Result<Integer, Throwable> result = Result.failure(new Throwable());
        result.map(v -> {
            throw new RuntimeException("should not have been called!");
        });
    }

    @Test
    void mapErrorShouldReturnFailureWhenItsAnFailure() {
        Result<Integer, Throwable> result1 = Result.failure(new Throwable());
        Result<Integer, Exception> result2 = result1.mapError(Exception::new);

        assertTrue(result2.isFailure());
        assertEquals(result1.error().get(), result2.error().get().getCause());
    }

    @Test
    void mapErrorShouldReturnSuccessWhenItsAnSuccess() throws Exception {
        Result<String, Throwable> result1 = Result.success("10");
        Result<String, Exception> result2 = result1.mapError(Exception::new);

        assertTrue(result2.isSuccess());
        assertEquals("10", result2.get());
    }

    @Test
    void andShouldReturnResultWhenItsAnSuccess() {
        Result<Integer, Throwable> result1 = Result.success(5);
        Result<String, Throwable> result2 = result1.and(Result.success("test"));

        assertNotNull(result2);

        assertTrue(result2.isSuccess());
        assertEquals("test", result2.value().get());
    }

    @Test
    void andThenShouldReturnResultWhenItsAnSuccess() {
        Result<Integer, Throwable> result1 = Result.success(5);
        Result<String, Throwable> result2 = result1.andThen(v -> Result.success(Integer.toString(v)));

        assertNotNull(result2);

        assertTrue(result2.isSuccess());
        assertEquals("5", result2.value().get());
    }
}