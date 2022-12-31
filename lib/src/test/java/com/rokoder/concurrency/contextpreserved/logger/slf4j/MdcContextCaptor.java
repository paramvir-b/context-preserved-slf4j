package com.rokoder.concurrency.contextpreserved.logger.slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.MDC;

class MdcContextCaptor implements Runnable, Callable<Map<String, String>> {
  private final AtomicReference<Map<String, String>> capturedContext = new AtomicReference<>();
  private final Runnable userCommand;
  private CountDownLatch countDownLatch = new CountDownLatch(1);

  MdcContextCaptor(Runnable userCommand) {
    this.userCommand = Objects.requireNonNull(userCommand, "userCommand cannot be null");
  }

  MdcContextCaptor() {
    this(() -> {
    });
  }

  Map<String, String> getCapturedContext() {
    return capturedContext.get();
  }

  String getCapturedContext(String key) {
    return capturedContext.get().get(key);
  }

  @Override
  public void run() {
    capturedContext.set(MDC.getCopyOfContextMap());
    userCommand.run();
    countDownLatch.countDown();
  }

  @Override
  public Map<String, String> call() {
    run();
    return capturedContext.get();
  }

  void reset() {
    countDownLatch = new CountDownLatch(1);
  }

  boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
    return countDownLatch.await(timeout, unit);
  }
}
