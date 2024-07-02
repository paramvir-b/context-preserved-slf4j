package org.slf4j.impl;

import com.rokoder.concurrency.contextpreserved.logger.slf4j.TestMdcAdapter;
import org.slf4j.spi.MDCAdapter;

// CHECKSTYLE.OFF: AbbreviationAsWordInName

/**
 * Test class to override the default MDC adapter.
 */
public class StaticMDCBinder {
  public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

  private StaticMDCBinder() {
  }

  public MDCAdapter getMDCA() {
    return new TestMdcAdapter();
  }

  public String getMDCAdapterClassStr() {
    return TestMdcAdapter.class.getName();
  }
}
// CHECKSTYLE.ON: AbbreviationAsWordInName
