
package com.moandjiezana.tent.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

public class ExtractorTest {

  /**
   * Helper method for asserting that the List of extracted Strings match the expected values.
   *
   * @param message to display on failure
   * @param expected Array of Strings that were expected to be extracted
   * @param actual List of Strings that were extracted
   */
  protected static void assertList(String message, String[] expected, List<String> actual) {
    List<String> expectedList = Arrays.asList(expected);
    if (expectedList.size() != actual.size()) {
      fail(message + "\n\nExpected list and extracted list are differnt sizes:\n" +
      "  Expected (" + expectedList.size() + "): " + expectedList + "\n" +
      "  Actual   (" + actual.size() + "): " + actual);
    } else {
      for (int i=0; i < expectedList.size(); i++) {
        assertEquals(expectedList.get(i), actual.get(i));
      }
    }
  }
}
