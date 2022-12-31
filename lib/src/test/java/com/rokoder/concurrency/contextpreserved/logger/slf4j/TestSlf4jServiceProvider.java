package com.rokoder.concurrency.contextpreserved.logger.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * {@link SLF4JServiceProvider} for testing purposes to be able to capture and manage context.
 */
public class TestSlf4jServiceProvider implements SLF4JServiceProvider {
  private static final MDCAdapter mdcAdapter = new TestMdcAdapter();
  // to avoid constant folding by the compiler, this field must *not* be final
  public static String REQUESTED_API_VERSION = "2.0.99"; // !final

  @Override
  public ILoggerFactory getLoggerFactory() {
    throw new IllegalStateException("Not implemented intentionally");
  }

  @Override
  public IMarkerFactory getMarkerFactory() {
    throw new IllegalStateException("Not implemented intentionally");
  }

  @Override
  public MDCAdapter getMDCAdapter() {
    return mdcAdapter;
  }

  @Override
  public String getRequestedApiVersion() {
    return REQUESTED_API_VERSION;
  }

  @Override
  public void initialize() {

  }
}
