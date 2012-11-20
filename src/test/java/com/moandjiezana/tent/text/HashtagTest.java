package com.moandjiezana.tent.text;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

/**
 * Tests for the extractHashtags method
 */
public class HashtagTest extends ExtractorTest {
  private final Extractor extractor = new Extractor();
  
  public void hashtagAtTheBeginning() {
    List<String> extracted = extractor.extractHashtags("#hashtag mention");
    assertList("Failed to extract hashtag at the beginning", new String[]{"hashtag"}, extracted);
  }

  @Test
  public void hashtagWithLeadingSpace() {
    List<String> extracted = extractor.extractHashtags(" #hashtag mention");
    assertList("Failed to extract hashtag with leading space", new String[]{"hashtag"}, extracted);
  }

  @Test
  public void hashtagInMidText() {
    List<String> extracted = extractor.extractHashtags("mention #hashtag here");
    assertList("Failed to extract hashtag in mid text", new String[]{"hashtag"}, extracted);
  }

  @Test
  public void testMultipleHashtags() {
    List<String> extracted = extractor.extractHashtags("text #hashtag1 #hashtag2");
    assertList("Failed to extract multiple hashtags", new String[]{"hashtag1", "hashtag2"}, extracted);
  }

  @Test
  public void hashtagWithIndices() {
    List<Extractor.Entity> extracted = extractor.extractHashtagsWithIndices(" #user1 mention #user2 here #user3 ");
    assertEquals(extracted.size(), 3);
    assertEquals(extracted.get(0).getStart().intValue(), 1);
    assertEquals(extracted.get(0).getEnd().intValue(), 7);
    assertEquals(extracted.get(1).getStart().intValue(), 16);
    assertEquals(extracted.get(1).getEnd().intValue(), 22);
    assertEquals(extracted.get(2).getStart().intValue(), 28);
    assertEquals(extracted.get(2).getEnd().intValue(), 34);
  }

  @Test
  public void hashtagWithSupplementaryCharacters() {
    // insert U+10400 before " #hashtag"
    String text = String.format("%c #hashtag %c #hashtag", 0x00010400, 0x00010400);

    // count U+10400 as 2 characters (as in UTF-16)
    List<Extractor.Entity> extracted = extractor.extractHashtagsWithIndices(text);
    assertEquals(extracted.size(), 2);
    assertEquals(extracted.get(0).value, "hashtag");
    assertEquals(extracted.get(0).start, 3);
    assertEquals(extracted.get(0).end, 11);
    assertEquals(extracted.get(1).value, "hashtag");
    assertEquals(extracted.get(1).start, 15);
    assertEquals(extracted.get(1).end, 23);

    // count U+10400 as single character
    extractor.modifyIndicesFromUTF16ToToUnicode(text, extracted);
    assertEquals(extracted.size(), 2);
    assertEquals(extracted.get(0).start, 2);
    assertEquals(extracted.get(0).end, 10);
    assertEquals(extracted.get(1).start, 13);
    assertEquals(extracted.get(1).end, 21);

    // count U+10400 as 2 characters (as in UTF-16)
    extractor.modifyIndicesFromUnicodeToUTF16(text, extracted);
    assertEquals(extracted.size(), 2);
    assertEquals(extracted.get(0).start, 3);
    assertEquals(extracted.get(0).end, 11);
    assertEquals(extracted.get(1).start, 15);
    assertEquals(extracted.get(1).end, 23);
  }
}