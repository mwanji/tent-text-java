package com.moandjiezana.tent.text;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for the extractMentionedScreennames{WithIndices} methods
 */
public class MentionTest extends ExtractorTest {
  private final Extractor extractor = new Extractor();

  @Test
  public void mentionAtTheBeginning() {
    List<String> extracted = extractor.extractMentionedScreennames("^user mention");
    assertList("Failed to extract mention at the beginning", new String[]{"user"}, extracted);
  }

  @Test
 public void mentionWithLeadingSpace() {
    List<String> extracted = extractor.extractMentionedScreennames(" ^user mention");
    assertList("Failed to extract mention with leading space", new String[]{"user"}, extracted);
  }

  @Test
  public void mentionInMidText() {
    List<String> extracted = extractor.extractMentionedScreennames("mention ^user here");
    assertList("Failed to extract mention in mid text", new String[]{"user"}, extracted);
  }
  
  @Test
  public void should_not_extract_mention_preceeded_by_letter() {
    List<String> screennames = extractor.extractMentionedScreennames("meet^the beach");
    assertList("Should be empty", new String[0], screennames);
  }
  
  @Test
  public void should_extract_mention_preceeded_by_punctuation() {
    List<String> screennames = extractor.extractMentionedScreennames("great.^username");
    
    assertList("Should contain one mention", new String[] { "username" }, screennames);
  }
  
  @Test
  public void should_extract_mention_followed_by_punctuation() {
    List<String> screennames = extractor.extractMentionedScreennames("^username&^$%^");
    
    assertList("Should contain one mention", new String[] { "username" }, screennames);
  }
  
  @Test
  public void should_extract_mentions_followed_by_colon() {
    List<String> screennames = extractor.extractMentionedScreennames("^foo: ^bar");
    
    assertList("Should contain two mentions", new String[] { "foo", "bar" }, screennames);
  }
  
  @Test @Ignore
  public void should_extract_fully_qualified_entity() {
    List<String> screenname = extractor.extractMentionedScreennames("^https://mention.tent.is");
    assertList("Failed to extract fully-qualified entity", new String[] { "https://mention.tent.is" }, screenname);
  }

  @Test
  public void multipleMentions() {
    List<String> extracted = extractor.extractMentionedScreennames("mention ^user1 here and ^user2 here");
    assertList("Failed to extract multiple mentioned users", new String[]{"user1", "user2"}, extracted);
  }

  @Test
  public void mentionWithIndices() {
    List<Extractor.Entity> extracted = extractor.extractMentionedScreennamesWithIndices(" ^user1 mention ^user2 here ^user3 ");
    assertEquals(extracted.size(), 3);
    assertEquals(extracted.get(0).getStart().intValue(), 1);
    assertEquals(extracted.get(0).getEnd().intValue(), 7);
    assertEquals(extracted.get(1).getStart().intValue(), 16);
    assertEquals(extracted.get(1).getEnd().intValue(), 22);
    assertEquals(extracted.get(2).getStart().intValue(), 28);
    assertEquals(extracted.get(2).getEnd().intValue(), 34);
  }

  @Test
  public void mentionWithSupplementaryCharacters() {
    // insert U+10400 before " @mention"
    String text = String.format("%c ^mention %c ^mention", 0x00010400, 0x00010400);

    // count U+10400 as 2 characters (as in UTF-16)
    List<Extractor.Entity> extracted = extractor.extractMentionedScreennamesWithIndices(text);
    assertEquals(extracted.size(), 2);
    assertEquals(extracted.get(0).value, "mention");
    assertEquals(extracted.get(0).start, 3);
    assertEquals(extracted.get(0).end, 11);
    assertEquals(extracted.get(1).value, "mention");
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
    assertEquals(2, extracted.size());
    assertEquals(3, extracted.get(0).start);
    assertEquals(11, extracted.get(0).end);
    assertEquals(15, extracted.get(1).start);
    assertEquals(23, extracted.get(1).end);
  }
}