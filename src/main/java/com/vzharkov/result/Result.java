package com.vzharkov.result;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Result is a type that represents either success or failure.
 * Methods could return Result whenever errors are expected and recoverable.
 *
 * @param <V> Success value type
 */
@SuppressWarnings({"unchecked"})
public abstract class Result<V> {
    protected Result() {}

    /**
     * Returns a new Success instance containing the given value.
     *
     * @param value Success value
     * @param <V> Success value type
     *
     * @return see above
     */
    public static <V> Result<V> success(V value) {
        return new Success<>(value);
    }

    /**
     * Returns a new Failure instance containing the given Throwable.
     *
     * @param t Throwable
     * @param <V> Success value type
     *
     * @return see above
     */
    public static <V> Result<V> failure(Throwable t) {
        return new Failure<>(t);
    }

    /**
     * Returns a new Failure instance containing the given error message.
     *
     * @param message Error message
     * @param <V> Success value type
     *
     * @return see above
     */
    public static <V> Result<V> failure(String message) {
        return failure(new IllegalStateException(message));
    }

    /**
     * Returns an Success with the specified value,
     * otherwise returns Failure with Throwable.
     *
     * @param fn a computation that might throw an exception
     * @param <V> the type of the value
     *
     * @return see above
     */
    public static <V> Result<V> attempt(Supplier<V> fn) {
        Objects.requireNonNull(fn);

        try {
            return success(fn.get());
        } catch (Throwable t) {
            return failure(t);
        }
    }

    /**
     * Returns an Success with the specified value,
     * otherwise returns Failure with Exception.
     *
     * @param fn a computation that might throw an exception
     * @param <V> the type of the value
     *
     * @return see above
     */
    public static <V> Result<V> call(Callable<V> fn) {
        Objects.requireNonNull(fn);

        V v;
        try {
            v = fn.call();
        } catch (Exception e) {
            return failure(e);
        }
        return success(v);
    }

    /**
     * @return value if the result is Success.
     */
    protected abstract V value();

    /**
     * @return error if the result is Failure.
     */
    protected abstract Throwable error();

    /**
     * @return value if the result is Success.
     * @throws RuntimeException if result is Failure.
     */
    protected abstract V get();

    /**
     * @return true if the result is Success.
     */
    protected abstract boolean isSuccess();

    /**
     * @return true if the result is Failure.
     */
    protected abstract boolean isFailure();

    /**
     * @return Result wrapped in Optional of nullable
     */
    protected abstract Optional<V> toOptional();

    /**
     * Returns the given value if this is a Failure otherwise return this Success.
     *
     * @param value the value
     * @return value  or the result of this Success
     */
    protected abstract V getOrElse(V value);

    /**
     * Maps a Result<V> to Result<U> by applying a function to a contained Success value,
     * leaving an Error value untouched.
     *
     * @param mapper The {@link Function} to call with the value of this.
     * @param <U> The new value type.
     * @return see above.
     */
    protected abstract <U> Result<U> map(Function<? super V, ? extends U> mapper);

    /**
     * If this is an Success value andThen() returns the Result of the given {@link Function},
     * otherwise returns this.
     *
     * @param op The {@link Function} to be called with the value of this.
     * @param <U> The new value type.
     * @return see above.
     */
    protected abstract <U> Result<U> andThen(Function<? super V, Result<U>> op);

    public static final class Success<V> extends Result<V>  {
        private final V value;

        private Success(V value) {
            this.value = value;
        }

        @Override
        public V value() {
            return value;
        }

        @Override
        public Throwable error() {
            return null;
        }

        @Override
        public V get() {
            return value;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public Optional<V> toOptional() {
            return Optional.ofNullable(value);
        }

        @Override
        public V getOrElse(V value) {
            return value();
        }

        @Override
        public <U> Result<U> map(Function<? super V, ? extends U> mapper) {
            Objects.requireNonNull(mapper);

            try {
                return success(mapper.apply(value));
            } catch (Throwable t) {
                return failure(t);
            }
        }

        @Override
        public <U> Result<U> andThen(Function<? super V, Result<U>> op) {
            Objects.requireNonNull(op);

            try {
                return op.apply(value());
            } catch (Throwable t) {
                return failure(t);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Success<?> success = (Success<?>) o;

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

    public static final class Failure<V> extends Result<V>  {
        private final Throwable error;

        private Failure(Throwable error) {
            this.error = error;
        }

        @Override
        public V value() {
            return null;
        }

        @Override
        public Throwable error() {
            return error;
        }

        @Override
        public V get() {
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            }

            throw new RuntimeException(error);
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public Optional<V> toOptional() {
            return Optional.empty();
        }

        @Override
        public V getOrElse(V value) {
            return value;
        }

        @Override
        public <U> Result<U> map(Function<? super V, ? extends U> mapper) {
            return (Result<U>) this;
        }

        @Override
        public <U> Result<U> andThen(Function<? super V, Result<U>> op) {
            return  (Result<U>) this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            Failure<?> failure = (Failure<?>) o;

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

