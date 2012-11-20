package com.moandjiezana.tent.text;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for the extractReplyScreenname method
 */
public class ReplyTest extends ExtractorTest {

  private final Extractor extractor = new Extractor();

  @Test
  public void replyAtTheStart() {
    String extracted = extractor.extractReplyScreenname("^user reply");
    assertEquals("Failed to extract reply at the start", "user", extracted);
  }

  @Test
  public void replyWithLeadingSpace() {
    String extracted = extractor.extractReplyScreenname(" ^user reply");
    assertEquals("Failed to extract reply with leading space", "user", extracted);
  }
}