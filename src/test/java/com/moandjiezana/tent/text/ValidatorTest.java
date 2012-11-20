package com.moandjiezana.tent.text;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidatorTest {
  protected Validator validator = new Validator();

  @Test
  public void bomCharacter() {
    assertFalse(validator.isValidTweet("test \uFFFE"));
    assertFalse(validator.isValidTweet("test \uFEFF"));
  }

  @Test
  public void invalidCharacter() {
    assertFalse(validator.isValidTweet("test \uFFFF"));
    assertFalse(validator.isValidTweet("test \uFEFF"));
  }

  @Test
  public void directionChangeCharacters() {
    assertFalse(validator.isValidTweet("test \u202A test"));
    assertFalse(validator.isValidTweet("test \u202B test"));
    assertFalse(validator.isValidTweet("test \u202C test"));
    assertFalse(validator.isValidTweet("test \u202D test"));
    assertFalse(validator.isValidTweet("test \u202E test"));
  }

  @Test
  public void accentCharacters() {
    String c = "\u0065\u0301";
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 139; i++) {
      builder.append(c);
    }
    assertTrue(validator.isValidTweet(builder.toString()));
    assertTrue(validator.isValidTweet(builder.append(c).toString()));
    assertFalse(validator.isValidTweet(builder.append(c).toString()));
  }

  @Test
  public void mutiByteCharacters() {
    String c = "\ud83d\ude02";
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 139; i++) {
      builder.append(c);
    }
    assertTrue(validator.isValidTweet(builder.toString()));
    assertTrue(validator.isValidTweet(builder.append(c).toString()));
    assertFalse(validator.isValidTweet(builder.append(c).toString()));
  }
}
