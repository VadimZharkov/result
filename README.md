# The `Result<V>` monad
The monad `Result<V>` may be **Success** or **Failure**.

Examples of usage where you would prefer Result type instead of Java error handling include:

- results passed between threads;
- results asynchronously delivered to the current thread;
- results retained for any duration;
- results passed into a method rather than out.

This implementation of Result includes all considered "monadic-like" operations.

**unit**

`Result.success(V value)` accepts a value and returns Success.  
`Result.failure(String message)` accepts a string (a message) and returns Failure.  
`Result.failure(Throwable t)` accepts an exception and returns Failure.  

**bind**

`andThen(Function<? super V, Result<U>> op)` applies the given function to a value if Success and then returns new Success, otherwise returns the Failure.

**map**

`map(Function<? super V, ? extends U> mapper)` applies the given function to a value if Success and then returns new Success, or otherwise returns the Failure.

Other useful methods:

`attempt(Supplier<V> fn)` result of computation that might throw an exception.  
`call(Callable<V> fn)` result of computation that might throw an exception.  
`V get()` returns value if the result is Success, throws unchecked exception if result is Failure.  
`toOptional()` returns optional of V if Success, otherwise empty optional.  
`V getOrElse(V value)` returns the given value if this is a Failure, otherwise return this Success.
