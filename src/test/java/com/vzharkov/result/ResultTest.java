package com.vzharkov.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {
    @Test
    void successShouldReturnSuccess() {
        Result<String, Throwable> result = Result.success("test");

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertNotNull(result.getValue());
        assertNull(result.getError());
    }

    @Test
    void successShouldReturnItsValue() {
        Result<String, Throwable> result = Result.success("test");

        assertEquals("test", result.getValue());
    }

    @Test
    void errorShouldReturnError() {
        Result<String, Throwable> result = Result.error(new Throwable());

        assertTrue(result.isFailure());
        assertFalse(result.isSuccess());
        assertNull(result.getValue());
        assertNotNull(result.getError());
    }

    @Test
    void errorShouldReturnItsValue() {
        final Throwable error = new Throwable();
        Result<String, Throwable> result = Result.error(error);

        assertEquals(error, result.getError());
    }

    @Test
    void mapShouldReturnSuccessWhenItsAnSuccess() {
        final Result<Integer, Throwable> result1 = Result.success(5);
        final Result<String, Throwable> result2 = result1.map(v -> Integer.toString(v));

        assertEquals("5", result2.getValue());
    }

    @Test
    void mapShouldReturnErrorWhenItsAnError() {
        final Throwable error = new Throwable();
        final Result<Integer, Throwable> result1 = Result.error(error);
        final Result<String, Throwable> result2 = result1.map(v -> Integer.toString(v));

        assertEquals(error, result2.getError());
    }

    @Test
    void mapShouldNotCallMapperWhenItsAnError() {
        final Result<Integer, Throwable> result = Result.error(new Throwable());
        result.map(v -> {
            throw new RuntimeException("should not have been called!");
        });
    }

    @Test
    void mapErrorShouldReturnErrorWhenItsAnError() {
        final Result<Integer, Integer> result1 = Result.error(10);
        final Result<Integer, String> result2 = result1.mapError(e -> Integer.toString(e));

        assertEquals("10", result2.getError());
    }

    @Test
    void mapErrorShouldReturnSuccessWhenItsAnSuccess() {
        final Result<String, Integer> result1 = Result.success("10");
        final Result<String, String> result2 = result1.mapError(e -> Integer.toString(e));

        assertEquals("10", result2.getValue());
    }

    @Test
    void andThenShouldReturnResult() {
        final Result<Integer, Throwable> result1 = Result.success(5);
        final Result<String, Throwable> result2 = result1.andThen(v -> Result.success(Integer.toString(v)));

        assertEquals("5", result2.getValue());
    }
}