package com.vzharkov.result;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void successShouldReturnValue() {
        Result<String> result = Result.success("test");

        assertTrue(result.isSuccess());
        assertNotNull(result.value());
        assertEquals("test", result.value());
    }

    @Test
    void failureShouldReturnError() {
        Result<?> result = Result.failure(new Throwable());

        assertTrue(result.isFailure());
        assertNotNull(result.error());
    }

    @Test
    void failureShouldReturnErrorWhenMessageIsGiven() {
        Result<?> result = Result.failure("Error message");

        assertTrue(result.isFailure());
        assertNotNull(result.error());
    }

    @Test
    void successShouldReturnValueWhenGet() {
        Result<String> result = Result.success("test");

        assertTrue(result.isSuccess());
        assertEquals("test", result.get());
    }

    @Test
    void failureShouldThrowErrorWhenGet() {
        Result<?> result = Result.failure(new Throwable());

        assertTrue(result.isFailure());
        assertThrows(Throwable.class, result::get);
    }

    @Test
    void attemptShouldReturnSuccessWhenSupplierReturnsValue() {
        Result<String> result = Result.attempt(() -> "test");

        assertTrue(result.isSuccess());
        assertEquals("test", result.value());
    }

    @Test
    void attemptShouldReturnFailureWhenSupplierThrows() {
        Result<Integer> result = Result.attempt(() -> Integer.valueOf("abc"));

        assertTrue(result.isFailure());
        assertNotNull(result.error());
    }

    @Test
    void callShouldReturnSuccessWhenSupplierReturnsValue() {
        Result<String> result = Result.call(() -> "test");

        assertTrue(result.isSuccess());
        assertEquals("test", result.value());
    }

    @Test
    void callShouldReturnFailureWhenSupplierThrows() {
        Result<Integer> result = Result.call(() -> Integer.valueOf("abc"));

        assertTrue(result.isFailure());
        assertNotNull(result.error());
    }

    @Test
    void successShouldReturnNonEmptyOptional() {
        Result<Integer> result = Result.success(5);

        assertNotEquals(result.toOptional(), Optional.empty());
    }

    @Test
    void failureShouldReturnEmptyOptional() {
        Result<?> result = Result.failure(new Throwable());

        assertEquals(result.toOptional(), Optional.empty());
    }

    @Test
    void successShouldReturnItsValueWhenGetOrElse() {
        Result<Integer> result = Result.success(5);
        Integer i = result.getOrElse(6);

        assertEquals(result.value(), i);
    }

    @Test
    void failureShouldReturnGivenValueWhenGetOrElse() {
        Result<Integer> result = Result.failure(new Throwable());
        Integer i = result.getOrElse(7);

        assertEquals(Integer.valueOf(7), i);
    }

    @Test
    void mapShouldReturnSuccessWhenItsAnSuccess() {
        Result<Integer> result1 = Result.success(5);
        Result<String> result2 = result1.map(v -> Integer.toString(v));

        assertTrue(result2.isSuccess());
        assertEquals("5", result2.value());
    }

    @Test
    void mapShouldReturnFailureWhenItsAnFailure() {
        Result<Integer> result1 = Result.failure(new Throwable());
        Result<String> result2 = result1.map(v -> Integer.toString(v));

        assertTrue(result2.isFailure());
        assertEquals(result1.error(), result2.error());
    }

    @Test
    void mapShouldNotCallMapperWhenItsAnFailure() {
        Result<?> result = Result.failure(new Throwable());
        result.map(v -> {
            throw new RuntimeException("should not have been called!");
        });
    }

    @Test
    void mapShouldReturnFailureWhenMapperThrows() {
        Result<String> result1 = Result.success("abc");
        Result<Integer> result2 = result1.map(Integer::valueOf);

        assertTrue(result2.isFailure());
        assertNotNull(result2.error());
    }

    @Test
    void andThenShouldReturnResultWhenItsAnSuccess() {
        Result<Integer> result1 = Result.success(5);
        Result<String> result2 = result1.andThen(v -> Result.success(Integer.toString(v)));

        assertTrue(result2.isSuccess());
        assertEquals("5", result2.value());
    }

    @Test
    void mapShouldReturnFailureWhenOpThrows() {
        Result<String> result1 = Result.success("abc");
        Result<Integer> result2 = result1.andThen(v -> Result.success(Integer.valueOf(v)));

        assertTrue(result2.isFailure());
        assertNotNull(result2.error());
    }
}