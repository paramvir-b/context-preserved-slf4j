package com.rokoder.concurrency.contextpreserved.logger.slf4j;

import com.rokoder.concurrency.contextpreserved.ContextCoordinator;
import com.rokoder.concurrency.contextpreserved.ContextPreservedCallable;
import com.rokoder.concurrency.contextpreserved.ContextPreservedRunnable;
import com.rokoder.concurrency.contextpreserved.DynamicContextPreservedExecutor;
import com.rokoder.concurrency.contextpreserved.DynamicContextPreservedExecutorService;
import com.rokoder.concurrency.contextpreserved.FixedContextPreservedExecutor;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nullable;
import org.slf4j.MDC;

/**
 * Factory class to create MDC preserved java concurrency objects.
 */
public final class Slf4jMdcPreservedFactory {

  // Visible for testing only
  static final ContextCoordinator<Map<String, String>> CONTEXT_COORDINATOR =
      new ContextCoordinator<>() {

        @Nullable
        @Override
        public Map<String, String> get() {
          return MDC.getCopyOfContextMap();
        }

        @SuppressFBWarnings(value = "NP_METHOD_PARAMETER_TIGHTENS_ANNOTATION",
            justification = "It does not change nullness")
        @SuppressWarnings("nullness")
        @Override
        public void set(@Nullable Map<String, String> context) {
          MDC.setContextMap(context);
        }
      };

  private Slf4jMdcPreservedFactory() {
    // Intentionally private as it is a util factory.
  }

  /**
   * Decorates the passed {@link Callable} with a new one which preserves {@link MDC} context across
   * thread boundaries. The new context used is captured at the time of call to this api from the
   * thread that calls it.
   *
   * @param callable Callable to be decorated
   * @param <V> Result type of method {@link Callable#call()}
   * @return Newly created wrapped {@link Callable}
   */
  public static <V> Callable<V> newCallableFrom(Callable<V> callable) {
    Objects.requireNonNull(callable, "callable cannot be null");
    return ContextPreservedCallable.wrap(callable, CONTEXT_COORDINATOR);
  }

  /**
   * Decorates the passed {@link Callable} with a new one which preserves {@link MDC} context across
   * thread boundaries. The new context used is one that is passed at the time of call to this api.
   *
   * @param callable Callable to be decorated
   * @param newContext New context to be used to preserve
   * @param <V> Result type of method {@link Callable#call()}
   * @return Newly created wrapped {@link Callable}
   */
  public static <V> Callable<V> newCallableFrom(Callable<V> callable,
                                                Map<String, String> newContext) {
    Objects.requireNonNull(callable, "callable cannot be null");
    return ContextPreservedCallable.wrap(callable, CONTEXT_COORDINATOR, newContext);
  }

  /**
   * Decorates the passed {@link Runnable} with a new one which preserves {@link MDC} context across
   * thread boundaries. The new context used is captured at the time of call to this api from the
   * thread that calls it.
   *
   * @param runnable Runnable to be decorated
   * @return Newly created wrapped {@link Runnable}
   */
  public static Runnable newRunnableFrom(Runnable runnable) {
    Objects.requireNonNull(runnable, "runnable cannot be null");
    return ContextPreservedRunnable.wrap(runnable, CONTEXT_COORDINATOR);
  }

  /**
   * Decorates the passed {@link Runnable} with a new one which preserves {@link MDC} context across
   * thread boundaries. The new context used is one that is passed at the time of call to this api.
   *
   * @param runnable Runnable to be decorated
   * @param newContext New context to be used to preserve
   * @return Newly created wrapped {@link Runnable}
   */
  public static Runnable newRunnableFrom(Runnable runnable, Map<String, String> newContext) {
    Objects.requireNonNull(runnable, "runnable cannot be null");
    return ContextPreservedRunnable.wrap(runnable, CONTEXT_COORDINATOR, newContext);
  }

  /**
   * Decorates the passed {@link Executor} with a new one which preserves the {@link MDC}
   * context across thread boundaries. The new context used is captured at the time of the call to
   * {@link Executor#execute(Runnable)}. The new context is preserved for passed {@link Runnable} to
   * {@link Executor#execute(Runnable)}
   *
   * @param executor Executor to be decorated
   * @return Newly created wrapped {@link Executor}
   */
  public static Executor newDynamicContextExecutorFrom(Executor executor) {
    Objects.requireNonNull(executor, "executor cannot be null");
    return DynamicContextPreservedExecutor.wrap(executor, CONTEXT_COORDINATOR);
  }

  /**
   * Decorates the passed {@link Executor} with a new one which preserves the {@link MDC}
   * context across thread boundaries. The new context used is captured from calling thread at the
   * time to this call. The new context is preserved for passed {@link Runnable} to
   * {@link Executor#execute(Runnable)}
   *
   * @param executor Executor to be decorated
   * @return Newly created wrapped {@link Executor}
   */
  public static Executor newFixedContextExecutorFrom(Executor executor) {
    Objects.requireNonNull(executor, "executor cannot be null");
    return FixedContextPreservedExecutor.wrap(executor, CONTEXT_COORDINATOR);
  }

  /**
   * Decorates the passed {@link Executor} with a new one which preserves the {@link MDC}
   * context across thread boundaries. The new context used is what passed at the time to this call.
   * The new context is preserved for passed {@link Runnable} to {@link Executor#execute(Runnable)}
   *
   * @param executor Executor to be decorated
   * @return Newly created wrapped {@link Executor}
   */
  public static Executor newFixedContextExecutorFrom(Executor executor,
                                                     Map<String, String> newContext) {
    Objects.requireNonNull(executor, "executor cannot be null");
    return FixedContextPreservedExecutor.wrap(executor, CONTEXT_COORDINATOR, newContext);
  }

  /**
   * Decorates the passed {@link ExecutorService} with a new one which preserves the
   * {@link MDC} context across thread boundaries. The new context used is captured at the time of
   * the call to command execution apis.
   *
   * @param executorService Executor service to be decorated
   * @return Newly created wrapped {@link ExecutorService}
   */
  public static ExecutorService newDynamicContextExecutorServiceFrom(
      ExecutorService executorService) {
    Objects.requireNonNull(executorService, "executorService cannot be null");
    return DynamicContextPreservedExecutorService.wrap(executorService, CONTEXT_COORDINATOR);
  }
}
