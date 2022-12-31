package com.rokoder.concurrency.contextpreserved.logger.slf4j;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

final class Slf4jMdcPreservedFactoryTest {
  private final ExecutorService executorService = Executors.newFixedThreadPool(10);

  @Test
  void testSlf4jContextCordinator() {

    // Run on
    executorService.submit(() -> {
      assertThat(Slf4jMdcPreservedFactory.CONTEXT_COORDINATOR.get(), equalTo(new HashMap<>()));
      Slf4jMdcPreservedFactory.CONTEXT_COORDINATOR.set(null);
      assertThat(Slf4jMdcPreservedFactory.CONTEXT_COORDINATOR.get(), equalTo(new HashMap<>()));
      Slf4jMdcPreservedFactory.CONTEXT_COORDINATOR.set(null);
    });
  }

  @Test
  void testCallable() {
    MDC.put("testCallable-test-key", "test-value");
    MdcContextCaptor contextCaptor = new MdcContextCaptor();
    executorService.submit(Slf4jMdcPreservedFactory.newCallableFrom(contextCaptor));
    submitAndWait(contextCaptor);

    assertThat(contextCaptor.getCapturedContext("testCallable-test-key"), equalTo("test-value"));
  }

  @Test
  void testCallableWithNewContextPassed() {
    String mdcKey = "testCallableWithNewContextPassed-test-key";
    MDC.put(mdcKey, "test-value");
    MdcContextCaptor contextCaptor = new MdcContextCaptor();
    executorService.submit(Slf4jMdcPreservedFactory.newCallableFrom(contextCaptor,
        createContextFor(mdcKey, "new-value")));
    submitAndWait(contextCaptor);

    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("new-value"));
  }

  @Test
  void testRunnable() {
    String mdcKey = "testRunnable-test-key";
    MDC.put(mdcKey, "test-value");
    MdcContextCaptor contextCaptor = new MdcContextCaptor();
    executorService.submit(Slf4jMdcPreservedFactory.newRunnableFrom(contextCaptor));
    submitAndWait(contextCaptor);

    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("test-value"));
  }

  @Test
  void testRunnableWithNewContextPassed() {
    String mdcKey = "testRunnableWithNewContextPassed-test-key";
    MDC.put(mdcKey, "test-value");
    MdcContextCaptor contextCaptor = new MdcContextCaptor();
    executorService.submit(Slf4jMdcPreservedFactory.newRunnableFrom(contextCaptor,
        createContextFor(mdcKey, "new-value")));
    submitAndWait(contextCaptor);

    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("new-value"));
  }

  @Test
  void testDynamicContextExecutorExecuteContextDoChangeLater() {
    String mdcKey = "testDynamicContextExecutorExecuteContextDoChangeLater-test-key";
    MDC.put(mdcKey, "test-value-1");

    MdcContextCaptor contextCaptor = new MdcContextCaptor();
    Executor executor = Slf4jMdcPreservedFactory.newDynamicContextExecutorFrom(executorService);
    executor.execute(contextCaptor);
    submitAndWait(contextCaptor);

    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("test-value-1"));

    MDC.put(mdcKey, "test-value-2");
    contextCaptor.reset();
    executor.execute(contextCaptor);
    submitAndWait(contextCaptor);
    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("test-value-2"));
  }

  @Test
  void testFixedContextExecutorExecuteContextDoNotChangeLater() {
    String mdcKey = "testFixedContextExecutorExecuteContextDoNotChangeLater-test-key";
    MDC.put(mdcKey, "test-value-1");

    MdcContextCaptor contextCaptor = new MdcContextCaptor();
    Executor executor = Slf4jMdcPreservedFactory.newFixedContextExecutorFrom(executorService);
    executor.execute(contextCaptor);
    submitAndWait(contextCaptor);

    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("test-value-1"));

    // changed the context
    MDC.put(mdcKey, "test-context-2");
    contextCaptor.reset();
    executor.execute(contextCaptor);
    submitAndWait(contextCaptor);
    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("test-value-1"));
  }

  @Test
  void testFixedContextExecutorWithPassedContextExecuteContextDoNotChangeLater() {
    String mdcKey =
        "testFixedContextExecutorWithPassedContextExecuteContextDoNotChangeLater-test-key";
    MDC.put(mdcKey, "test-value-1");

    MdcContextCaptor contextCaptor = new MdcContextCaptor();
    Executor executor = Slf4jMdcPreservedFactory.newFixedContextExecutorFrom(executorService,
        createContextFor(mdcKey, "passed-value"));
    executor.execute(contextCaptor);
    submitAndWait(contextCaptor);

    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("passed-value"));

    MDC.put(mdcKey, "test-context-2");
    contextCaptor.reset();
    executor.execute(contextCaptor);
    submitAndWait(contextCaptor);
    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("passed-value"));
  }

  @Test
  void testDynamicContextExecutorService_SubmitRunnable_ContextDoChangeLater() {
    String mdcKey =
        "testDynamicContextExecutorService_SubmitRunnable_ContextDoChangeLater-test-key";
    MDC.put(mdcKey, "test-value-1");

    MdcContextCaptor contextCaptor = new MdcContextCaptor();
    ExecutorService wrappedExecutorService =
        Slf4jMdcPreservedFactory.newDynamicContextExecutorServiceFrom(executorService);
    wrappedExecutorService.submit((Runnable) contextCaptor);
    submitAndWait(contextCaptor);

    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("test-value-1"));

    MDC.put(mdcKey, "test-value-2");
    contextCaptor.reset();
    wrappedExecutorService.submit((Runnable) contextCaptor);
    submitAndWait(contextCaptor);
    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("test-value-2"));
  }

  @Test
  void testDynamicContextExecutorService_SubmitRunnableWithResult_ContextDoChangeLater() {
    String mdcKey =
        "testDynamicContextExecutorService_SubmitRunnableWithResult_ContextDoChangeLater-test-key";
    MDC.put(mdcKey, "test-value-1");

    MdcContextCaptor contextCaptor = new MdcContextCaptor();
    ExecutorService wrappedExecutorService =
        Slf4jMdcPreservedFactory.newDynamicContextExecutorServiceFrom(executorService);
    wrappedExecutorService.submit(contextCaptor, "test-result");
    submitAndWait(contextCaptor);

    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("test-value-1"));

    MDC.put(mdcKey, "test-value-2");
    contextCaptor.reset();
    wrappedExecutorService.submit(contextCaptor, "test-result");
    submitAndWait(contextCaptor);
    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("test-value-2"));
  }

  @Test
  void testDynamicContextExecutorService_SubmitCallable_ContextDoChangeLater() {
    String mdcKey =
        "testDynamicContextExecutorService_SubmitCallable_ContextDoChangeLater-test-key";
    MDC.put(mdcKey, "test-value-1");

    MdcContextCaptor contextCaptor = new MdcContextCaptor();
    ExecutorService wrappedExecutorService =
        Slf4jMdcPreservedFactory.newDynamicContextExecutorServiceFrom(executorService);
    wrappedExecutorService.submit((Callable<Map<String, String>>) contextCaptor);
    submitAndWait(contextCaptor);

    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("test-value-1"));

    MDC.put(mdcKey, "test-value-2");
    contextCaptor.reset();
    wrappedExecutorService.submit((Callable<Map<String, String>>) contextCaptor);
    submitAndWait(contextCaptor);
    assertThat(contextCaptor.getCapturedContext(mdcKey), equalTo("test-value-2"));
  }

  @Test
  void testDynamicContextExecutorService_InvokeAnyCallable_ContextDoChangeLater()
      throws ExecutionException, InterruptedException {
    String mdcKey =
        "testDynamicContextExecutorService_InvokeAnyCallable_ContextDoChangeLater-test-key";
    MDC.put(mdcKey, "test-value-1");

    MdcContextCaptor contextCaptor1 = new MdcContextCaptor();
    MdcContextCaptor contextCaptor2 = new MdcContextCaptor();
    List<MdcContextCaptor> taskList = List.of(contextCaptor1, contextCaptor2);
    ExecutorService wrappedExecutorService =
        Slf4jMdcPreservedFactory.newDynamicContextExecutorServiceFrom(executorService);
    Map<String, String> contextMap = wrappedExecutorService.invokeAny(taskList);
    assertThat(contextMap.get(mdcKey), equalTo("test-value-1"));
    submitAndWait(taskList);

    MDC.put(mdcKey, "test-value-2");
    taskList.forEach(MdcContextCaptor::reset);
    contextMap = wrappedExecutorService.invokeAny(taskList);
    assertThat(contextMap.get(mdcKey), equalTo("test-value-2"));
  }

  @Test
  void testDynamicContextExecutorService_InvokeAnyCallableWithTimeout_ContextDoChangeLater()
      throws ExecutionException, InterruptedException, TimeoutException {
    // CHECKSTYLE.OFF: LineLength
    String mdcKey =
        "testDynamicContextExecutorService_InvokeAnyCallableWithTimeout_ContextDoChangeLater-test-key";
    // CHECKSTYLE.On: LineLength
    MDC.put(mdcKey, "test-value-1");

    MdcContextCaptor contextCaptor1 = new MdcContextCaptor();
    MdcContextCaptor contextCaptor2 = new MdcContextCaptor();
    List<MdcContextCaptor> taskList = List.of(contextCaptor1, contextCaptor2);
    ExecutorService wrappedExecutorService =
        Slf4jMdcPreservedFactory.newDynamicContextExecutorServiceFrom(executorService);
    Map<String, String> contextMap = wrappedExecutorService.invokeAny(taskList, 1,
        TimeUnit.SECONDS);
    assertThat(contextMap.get(mdcKey), equalTo("test-value-1"));
    submitAndWait(taskList);

    MDC.put(mdcKey, "test-value-2");
    taskList.forEach(MdcContextCaptor::reset);
    contextMap = wrappedExecutorService.invokeAny(taskList, 1, TimeUnit.SECONDS);
    assertThat(contextMap.get(mdcKey), equalTo("test-value-2"));
  }

  @Test
  void testDynamicContextExecutorService_InvokeAllCallable_ContextDoChangeLater()
      throws InterruptedException {
    MDC.put("test-key", "test-value-1");

    MdcContextCaptor contextCaptor1 = new MdcContextCaptor();
    MdcContextCaptor contextCaptor2 = new MdcContextCaptor();
    List<MdcContextCaptor> taskList = List.of(contextCaptor1, contextCaptor2);
    ExecutorService wrappedExecutorService =
        Slf4jMdcPreservedFactory.newDynamicContextExecutorServiceFrom(executorService);
    wrappedExecutorService.invokeAll(taskList);
    submitAndWait(taskList);
    assertThat(contextCaptor1.getCapturedContext("test-key"), equalTo("test-value-1"));

    MDC.put("test-key", "test-value-2");
    taskList.forEach(MdcContextCaptor::reset);
    wrappedExecutorService.invokeAll(taskList);
    submitAndWait(taskList);
    assertThat(contextCaptor1.getCapturedContext("test-key"), equalTo("test-value-2"));
  }

  @Test
  void testDynamicContextExecutorService_InvokeAllCallableWithTimeout_ContextDoChangeLater()
      throws InterruptedException {
    MDC.put("test-key", "test-value-1");

    MdcContextCaptor contextCaptor1 = new MdcContextCaptor();
    MdcContextCaptor contextCaptor2 = new MdcContextCaptor();
    List<MdcContextCaptor> taskList = List.of(contextCaptor1, contextCaptor2);
    ExecutorService wrappedExecutorService =
        Slf4jMdcPreservedFactory.newDynamicContextExecutorServiceFrom(executorService);
    wrappedExecutorService.invokeAll(taskList, 1, TimeUnit.SECONDS);
    submitAndWait(taskList);
    assertThat(contextCaptor1.getCapturedContext("test-key"), equalTo("test-value-1"));

    MDC.put("test-key", "test-value-2");
    taskList.forEach(MdcContextCaptor::reset);
    wrappedExecutorService.invokeAll(taskList, 1, TimeUnit.SECONDS);
    submitAndWait(taskList);
    assertThat(contextCaptor1.getCapturedContext("test-key"), equalTo("test-value-2"));
  }

  private Map<String, String> createContextFor(String key, String val) {
    Map<String, String> newContext = new ConcurrentHashMap<>();
    newContext.put(key, val);
    return newContext;
  }

  private void submitAndWait(List<MdcContextCaptor> taskList) {
    taskList.forEach(this::submitAndWait);
  }

  private void submitAndWait(MdcContextCaptor task) {
    try {
      task.awaitCompletion(1, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
