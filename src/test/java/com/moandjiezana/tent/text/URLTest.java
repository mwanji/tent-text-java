package com.moandjiezana.tent.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

/**
 * Tests for the extractURLsWithIndices method
 */
public class URLTest extends ExtractorTest {

  private final Extractor extractor = new Extractor();

  @Test
  public void urlWithIndices() {
    List<Extractor.Entity> extracted = extractor.extractURLsWithIndices("http://t.co url https://www.twitter.com ");
    assertEquals(extracted.get(0).getStart().intValue(), 0);
    assertEquals(extracted.get(0).getEnd().intValue(), 11);
    assertEquals(extracted.get(1).getStart().intValue(), 16);
    assertEquals(extracted.get(1).getEnd().intValue(), 39);
  }

  @Test
  public void urlWithoutProtocol() {
    String text = "www.twitter.com, www.yahoo.co.jp, t.co/blahblah, www.poloshirts.uk.com";
    assertList("Failed to extract URLs without protocol", new String[] { "www.twitter.com", "www.yahoo.co.jp", "t.co/blahblah", "www.poloshirts.uk.com" },
        extractor.extractURLs(text));

    List<Extractor.Entity> extracted = extractor.extractURLsWithIndices(text);
    assertEquals(extracted.get(0).getStart().intValue(), 0);
    assertEquals(extracted.get(0).getEnd().intValue(), 15);
    assertEquals(extracted.get(1).getStart().intValue(), 17);
    assertEquals(extracted.get(1).getEnd().intValue(), 32);
    assertEquals(extracted.get(2).getStart().intValue(), 34);
    assertEquals(extracted.get(2).getEnd().intValue(), 47);

    extractor.setExtractURLWithoutProtocol(false);
    assertTrue("Should not extract URLs w/o protocol", extractor.extractURLs(text).isEmpty());
  }

  @Test
  public void uRLFollowedByPunctuations() {
    String text = "http://games.aarp.org/games/mahjongg-dimensions.aspx!!!!!!";
    assertList("Failed to extract URLs followed by punctuations", new String[] { "http://games.aarp.org/games/mahjongg-dimensions.aspx" },
        extractor.extractURLs(text));
  }

  @Test
  public void urlWithPunctuation() {
    String[] urls = new String[] { "http://www.foo.com/foo/path-with-period./", "http://www.foo.org.za/foo/bar/688.1",
        "http://www.foo.com/bar-path/some.stm?param1=foo;param2=P1|0||P2|0", "http://foo.com/bar/123/foo_&_bar/", "http://foo.com/bar(test)bar(test)bar(test)",
        "www.foo.com/foo/path-with-period./", "www.foo.org.za/foo/bar/688.1", "www.foo.com/bar-path/some.stm?param1=foo;param2=P1|0||P2|0",
        "foo.com/bar/123/foo_&_bar/" };

    for (String url : urls) {
      assertEquals(url, extractor.extractURLs(url).get(0));
    }
  }

  @Test
  public void urlnWithSupplementaryCharacters() {
    // insert U+10400 before " http://twitter.com"
    String text = String.format("%c http://twitter.com %c http://twitter.com", 0x00010400, 0x00010400);

    // count U+10400 as 2 characters (as in UTF-16)
    List<Extractor.Entity> extracted = extractor.extractURLsWithIndices(text);
    assertEquals(extracted.size(), 2);
    assertEquals(extracted.get(0).value, "http://twitter.com");
    assertEquals(extracted.get(0).start, 3);
    assertEquals(extracted.get(0).end, 21);
    assertEquals(extracted.get(1).value, "http://twitter.com");
    assertEquals(extracted.get(1).start, 25);
    assertEquals(extracted.get(1).end, 43);

    // count U+10400 as single character
    extractor.modifyIndicesFromUTF16ToToUnicode(text, extracted);
    assertEquals(extracted.size(), 2);
    assertEquals(extracted.get(0).start, 2);
    assertEquals(extracted.get(0).end, 20);
    assertEquals(extracted.get(1).start, 23);
    assertEquals(extracted.get(1).end, 41);

    // count U+10400 as 2 characters (as in UTF-16)
    extractor.modifyIndicesFromUnicodeToUTF16(text, extracted);
    assertEquals(extracted.size(), 2);
    assertEquals(extracted.get(0).start, 3);
    assertEquals(extracted.get(0).end, 21);
    assertEquals(extracted.get(1).start, 25);
    assertEquals(extracted.get(1).end, 43);
  }
}