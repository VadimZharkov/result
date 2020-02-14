package com.vzharkov.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Result is a type that represents either success or failure.
 * Methods could return Result whenever errors are expected and recoverable.
 *
 * @param <V> Success value type
 * @param <E> Failure value type
 */
@SuppressWarnings({"OptionalGetWithoutIsPresent", "unchecked"})
public abstract class Result<V, E extends Throwable> {
    /**
     * Returns a new Success instance containing the given value.
     *
     * @param value Success value
     * @param <V> Success value type
     * @param <E> Error value type
     *
     * @return see above
     */
    public static <V, E extends Throwable> Result<V, E> success(V value) {
        return new Success<>(value);
    }

    /**
     * Returns a new Error instance containing the given value.
     *
     * @param error Error value
     * @param <V> Success value type
     * @param <E> Error value type
     * @return see above
     */
    public static <V, E extends Throwable> Result<V, E> failure(E error) {
        return new Failure<>(error);
    }

    /**
     * Returns a new Success instance containing the given value.
     *
     * @param value Success value
     * @param <V> Success value type
     * @param <E> Error value type
     *
     * @return see above
     * @throws NullPointerException if value is null
     */
    public static <V, E extends Throwable> Result<V, E> of(V value) {
        return new Success<>(value);
    }

    /**
     * Returns an Success with the specified value, if non-null,
     * otherwise returns Failure with NullPointerException.
     *
     * @param value the possibly-null value
     * @param <V> the type of the value
     * @return see above
     */
    public static <V> Result<V, ?> ofNullable(V value) {
        if (value != null)
            return of(value);

        return Result.failure(new NullPointerException());
    }

    /**
     * @return Optional value if the result is Success.
     */
    abstract public Optional<V> value();

    /**
     * @return Optional error if the result is Failure.
     */
    abstract public Optional<E> error();

    /**
     * @return value if the result is Success.
     * @throws E if result is Failure.
     */
    abstract public V get() throws E;

    /**
     * @return true if the result is Success.
     */
    public boolean isSuccess() {
        return value().isPresent() && !error().isPresent();
    }

    /**
     * @return true if the result is Failure.
     */
    public boolean isFailure() {
        return !isSuccess();
    }

    /**
     * Maps a Result<V, E> to Result<U, E> by applying a function to a contained Ok value,
     * leaving an Error value untouched.
     *
     * @param mapper The {@link Function} to call with the value of this.
     * @param <U> The new value type.
     * @return see above.
     */
    public <U> Result<U, E> map(Function<V, U> mapper) {
        Objects.requireNonNull(mapper);

        if (isSuccess()) {
             return Result.success(mapper.apply(value().get()));
        }
        return (Result<U, E>)this;
    }

    /**
     * Maps a Result<V, E> to Result<V, F> by applying a function to a contained Error value,
     * leaving an Success value untouched.
     *
     * @param mapper The {@link Function} to call with the error of this.
     * @param <F>  The new error type.
     * @return see above.
     */
    public <F extends Throwable> Result<V, F> mapError(Function<E, F> mapper) {
        Objects.requireNonNull(mapper);

        if (isFailure()) {
            return Result.failure(mapper.apply(error().get()));
        }
        return (Result<V, F>)this;
    }

    /**
     * Returns Result with new value if the result is Success, otherwise returns this.
     *
     * @param result The result.
     * @param <U> The new value type.
     * @return see above.
     */
    public <U> Result<U, E> and(Result<U, E> result) {
        Objects.requireNonNull(result);

        return isFailure() ? (Result<U, E>)this : result;
    }

    /**
     * If this is an Success value andThen() returns the Result of the given {@link Function},
     * otherwise returns this.
     *
     * @param op The {@link Function} to be called with the value of this.
     * @param <U> The new value type.
     * @return see above.
     */
    public <U> Result<U, E> andThen(Function<V, Result<U, E>> op) {
        Objects.requireNonNull(op);

        return isFailure() ? (Result<U, E>)this : op.apply(value().get());
    }

    public static final class Success<V, E extends Throwable> extends Result<V, E>  {
        private final V value;

        private Success(V value) {
            this.value = Objects.requireNonNull(value, () -> "Value cannot be null");
        }

        @Override
        public Optional<V> value() {
            return Optional.of(value);
        }

        @Override
        public Optional<E> error() {
            return Optional.empty();
        }

        @Override
        public V get() throws E {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Success<?, ?> success = (Success<?, ?>)o;
            return value.equals(success.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return "[Success: value=" + value + ']';
        }
    }

    public static final class Failure<V, E extends Throwable> extends Result<V, E>  {
        private final E error;

        private Failure(E error) {
            this.error = Objects.requireNonNull(error, () -> "Error cannot be null");;
        }

        @Override
        public Optional<V> value() {
            return Optional.empty();
        }

        @Override
        public Optional<E> error() {
            return Optional.of(error);
        }

        @Override
        public V get() throws E {
            throw error;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Failure<?, ?> failure = (Failure<?, ?>)o;
            return error.equals(failure.error);
        }

        @Override
        public int hashCode() {
            return error.hashCode();
        }

        @Override
        public String toString() {
            return "[Failure: error=" + error + ']';
        }
    }
}

