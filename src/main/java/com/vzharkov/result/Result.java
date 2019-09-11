package com.vzharkov.result;

import java.util.Objects;
import java.util.function.Function;

/**
 * Result is a type that represents either success or failure.
 * Methods could return Result whenever errors are expected and recoverable.
 *
 * @param <V> Success value type
 * @param <E> Error value type
 */
public abstract class Result<V, E> {
    /**
     * Returns a new Success instance containing the given value.
     *
     * @param value Success value
     * @param <V> Success value type
     * @param <E> Error value type
     *
     * @return see above
     */
    public static <V, E> Result<V, E> success(final V value) {
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
    public static <V, E> Result<V, E> error(final E error) {
        return new Error<>(error);
    }

    /**
     * @return success value if the result is Success.
     */
    abstract public V getValue();

    /**
     * @return error value if the result is Error.
     */
    abstract public E getError();

    /**
     * @return true if the result is Success.
     */
    public boolean isSuccess() {
        return null != getValue() && null == getError();
    }

    /**
     * @return true if the result is Error.
     */
    public boolean isFailure() {
        return null != getError() && null == getValue();
    }

    /**
     * Maps a Result<V, E> to Result<U, E> by applying a function to a contained Success value,
     * leaving an Error value untouched.
     *
     * @param mapper The {@link Function} to call with the value of this.
     * @param <U> The new value type.
     * @return see above.
     */
    public <U> Result<U, E> map(final Function<V, U> mapper) {
        Objects.requireNonNull(mapper);

        if (isSuccess()) {
            return Result.success(mapper.apply(getValue()));
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
    public <F> Result<V, F> mapError(final Function<E, F> mapper) {
        Objects.requireNonNull(mapper);

        if (isFailure()) {
            return Result.error(mapper.apply(getError()));
        }
        return (Result<V, F>)this;
    }

    /**
     * If this is an Success value andThen() returns the Result of the given {@link Function},
     * otherwise returns this.
     *
     * @param op The {@link Function} to be called with the value of this.
     * @param <U> The new value type.
     * @return see above.
     */
    public <U> Result<U, E> andThen(final Function<V, Result<U, E>> op) {
        Objects.requireNonNull(op);

        if (isFailure()) {
            return (Result<U, E>)this;
        }

        return op.apply(getValue());
    }

    @Override
    public String toString() {
        if (isSuccess()) {
            return getValue().toString();
        }
        return  "Error: " + getError().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Result result = (Result<?,?>)o;
        if (isSuccess())
            return getValue().equals(result.getValue());

        return getError().equals(result.getError());
    }

    @Override
    public int hashCode() {
        if (isSuccess())
            return getValue().hashCode();

        return getError().hashCode();
    }

    public static final class Success<V, E> extends Result<V, E>  {
        private final V value;

        private Success(V value) {
            this.value = value;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public E getError() {
            return null;
        }
   }

    public static final class Error<V, E> extends Result<V, E>  {
        private final E error;

        private Error(E error) {
            this.error = error;
        }

        @Override
        public V getValue() {
            return null;
        }

        @Override
        public E getError() {
            return error;
        }
    }
}

