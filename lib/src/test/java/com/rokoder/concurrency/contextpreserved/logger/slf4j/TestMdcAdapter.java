package com.rokoder.concurrency.contextpreserved.logger.slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.spi.MDCAdapter;

/**
 * Test adapter for testing.
 */
public class TestMdcAdapter implements MDCAdapter {
  private volatile Map<String, String> mdcContextMap = new HashMap<>();

  @Override
  public synchronized void put(String key, String val) {
    mdcContextMap.put(key, val);
  }

  @Override
  public synchronized String get(String key) {
    return mdcContextMap.get(key);
  }

  @Override
  public synchronized void remove(String key) {
    mdcContextMap.remove(key);
  }

  @Override
  public synchronized void clear() {
    mdcContextMap.clear();
  }

  @Override
  public synchronized Map<String, String> getCopyOfContextMap() {
    return new HashMap<>(mdcContextMap);
  }

  @Override
  public synchronized void setContextMap(Map<String, String> contextMap) {
    mdcContextMap =
        contextMap == null ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(contextMap);
  }
}
