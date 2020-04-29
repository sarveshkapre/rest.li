package com.linkedin.test.util;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;


public class SingleRetry implements IRetryAnalyzer {
  private boolean hasBeenRun;

  @Override
  public boolean retry(ITestResult result) {
    if (!hasBeenRun) {
      hasBeenRun = true;
      return true;
    }
    return false;
  }
}
