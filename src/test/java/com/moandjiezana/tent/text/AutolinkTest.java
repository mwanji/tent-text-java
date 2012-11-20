
package com.moandjiezana.tent.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.moandjiezana.tent.text.Extractor.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class AutolinkTest {
  private Autolink linker;

  @Before
  public void before() {
    linker = new Autolink();
  }

  @Test
  public void noFollowByDefault() {
    String tweet = "This has a #hashtag";
    String expected = "This has a <a href=\"https://skate.io/search?q=%23hashtag\" title=\"#hashtag\" class=\"hashtag\" rel=\"nofollow\">#hashtag</a>";
    assertAutolink(expected, linker.autoLinkHashtags(tweet));
  }

  @Test
  public void noFollowDisabled() {
    linker.setNoFollow(false);
    String tweet = "This has a #hashtag";
    String expected = "This has a <a href=\"https://skate.io/search?q=%23hashtag\" title=\"#hashtag\" class=\"hashtag\">#hashtag</a>";
    assertAutolink(expected, linker.autoLinkHashtags(tweet));
  }

  /** See Also: http://github.com/mzsanford/twitter-text-rb/issues#issue/5 */
  @Test
  public void blogspotWithDash() {
    linker.setNoFollow(false);
    String tweet = "Url: http://samsoum-us.blogspot.com/2010/05/la-censure-nuit-limage-de-notre-pays.html";
    String expected = "Url: <a href=\"http://samsoum-us.blogspot.com/2010/05/la-censure-nuit-limage-de-notre-pays.html\">http://samsoum-us.blogspot.com/2010/05/la-censure-nuit-limage-de-notre-pays.html</a>";
    assertAutolink(expected, linker.autoLinkURLs(tweet));
  }

  /** See also: https://github.com/mzsanford/twitter-text-java/issues/8 */
  @Test
  public void urlWithDollarThatLooksLikeARegex() {
    linker.setNoFollow(false);
    String text = "Url: http://example.com/$ABC";
    String expected = "Url: <a href=\"http://example.com/$ABC\">http://example.com/$ABC</a>";
    assertAutolink(expected, linker.autoLinkURLs(text));
  }

  @Test
  public void urlWithoutProtocol() {
    linker.setNoFollow(false);
    String text = "Url: www.twitter.com http://www.twitter.com";
    String expected = "Url: www.twitter.com <a href=\"http://www.twitter.com\">http://www.twitter.com</a>";
    assertAutolink(expected, linker.autoLinkURLs(text));
  }

  @Test
  public void urlEntities() {
    Entity entity = new Entity(0, 19, "http://t.co/0JG5Mcq", Entity.Type.URL);
    entity.setDisplayURL("blog.twitter.com/2011/05/twitte…");
    entity.setExpandedURL("http://blog.twitter.com/2011/05/twitter-for-mac-update.html");
    List<Entity> entities = new ArrayList<Entity>();
    entities.add(entity);
    String tweet = "http://t.co/0JG5Mcq";
    String expected = "<a href=\"http://t.co/0JG5Mcq\" title=\"http://blog.twitter.com/2011/05/twitter-for-mac-update.html\" rel=\"nofollow\">blog.twitter.com/2011/05/twitte…</a>";

    assertAutolink(expected, linker.autoLinkEntities(tweet, entities));
  }

  @Test
  public void withAngleBrackets() {
    linker.setNoFollow(false);
    String tweet = "(Debugging) <3 #idol2011";
    String expected = "(Debugging) &lt;3 <a href=\"https://skate.io/search?q=%23idol2011\" title=\"#idol2011\" class=\"hashtag\">#idol2011</a>";
    assertAutolink(expected, linker.autoLink(tweet));

    tweet = "<link rel='true'>http://example.com</link>";
    expected = "<link rel='true'><a href=\"http://example.com\">http://example.com</a></link>";
    assertAutolink(expected, linker.autoLinkURLs(tweet));
  }

  @Test
  public void usernameIncludeSymbol() {
    linker.setMentionIncludeSymbol(true);
    String mention = "Testing ^mention";// and ^http://mention.tent.is";
    String expected = "Testing <a class=\"username\" href=\"https://mention.tent.is\" rel=\"nofollow\">^mention</a>";
    assertAutolink(expected, linker.autoLink(mention));
  }
  
  @Test @Ignore
  public void should_link_fully_qualified_entity() {
    linker.setMentionIncludeSymbol(true);
    assertAutolink("Testing <a class=\"username\" href=\"https://mention.tent.is\" rel=\"nofollow\">^https://mention.tent.is</a>", linker.autoLink("Testing ^https://mention.tent.is"));
  }

  @Test
  public void urlClass() {
    linker.setNoFollow(false);

    String tweet = "http://twitter.com";
    String expected = "<a href=\"http://twitter.com\">http://twitter.com</a>";
    assertAutolink(expected, linker.autoLink(tweet));

    linker.setUrlClass("testClass");
    expected = "<a href=\"http://twitter.com\" class=\"testClass\">http://twitter.com</a>";
    assertAutolink(expected, linker.autoLink(tweet));

    tweet = "#hash ^tw";
    String result = linker.autoLink(tweet);
    assertTrue(result.contains("class=\"hashtag\""));
    assertTrue(result.contains("class=\"username\""));
    assertFalse(result.contains("class=\"testClass\""));
  }

  @Test
  public void symbolTag() {
    linker.setSymbolTag("s");
    linker.setTextWithSymbolTag("b");
    linker.setNoFollow(false);

    String text = "#hash";
    String expected = "<a href=\"https://skate.io/search?q=%23hash\" title=\"#hash\" class=\"hashtag\"><s>#</s><b>hash</b></a>";
    assertAutolink(expected, linker.autoLink(text));

    text = "^mention";
    expected = "<s>^</s><a class=\"username\" href=\"https://mention.tent.is\"><b>mention</b></a>";
    assertAutolink(expected, linker.autoLink(text));

    linker.setMentionIncludeSymbol(true);
    expected = "<a class=\"username\" href=\"https://mention.tent.is\"><s>^</s><b>mention</b></a>";
    assertAutolink(expected, linker.autoLink(text));
  }

  @Test
  public void urlTarget() {
    linker.setUrlTarget("_blank");

    String tweet = "http://test.com";
    String result = linker.autoLink(tweet);
    assertFalse("urlTarget shouldn't be applied to auto-linked hashtag", Pattern.matches(".*<a[^>]+hashtag[^>]+target[^>]+>.*", result));
    assertFalse("urlTarget shouldn't be applied to auto-linked mention", Pattern.matches(".*<a[^>]+username[^>]+target[^>]+>.*", result));
    assertTrue("urlTarget should be applied to auto-linked URL", Pattern.matches(".*<a[^>]+test.com[^>]+target=\"_blank\"[^>]*>.*", result));
    assertFalse("urlClass should not appear in HTML", result.toLowerCase().contains("urlclass"));
  }

  @Test
  public void linkAttributeModifier() {
    linker.setLinkAttributeModifier(new Autolink.LinkAttributeModifier() {
      @Override
      public void modify(Entity entity, Map<String, String> attributes) {
        if (entity.type == Entity.Type.HASHTAG) {
          attributes.put("dummy-hash-attr", "test");
        }
      }
    });

    String result = linker.autoLink("#hash ^mention");
    assertTrue("HtmlAttributeModifier should be applied to hashtag", Pattern.matches(".*<a[^>]+hashtag[^>]+dummy-hash-attr=\"test\"[^>]*>.*", result));
    assertFalse("HtmlAttributeModifier should not be applied to mention", Pattern.matches(".*<a[^>]+username[^>]+dummy-hash-attr=\"test\"[^>]*>.*", result));

    linker.setLinkAttributeModifier(new Autolink.LinkAttributeModifier() {
      public void modify(Entity entity, Map<String, String> attributes) {
        if (entity.type == Entity.Type.URL) {
          attributes.put("dummy-url-attr", entity.value);
        }
      }
    });
    result = linker.autoLink("^mention http://twitter.com/");
    assertFalse("HtmlAttributeModifier should not be applied to mention", Pattern.matches(".*<a[^>]+username[^>]+dummy-url-attr[^>]*>.*", result));
    assertTrue("htmlAttributeBlock should be applied to URL", Pattern.matches(".*<a[^>]+dummy-url-attr=\"http://twitter.com/\".*", result));
  }

  @Test
  public void linkTextModifier() {
    linker.setLinkTextModifier(new Autolink.LinkTextModifier() {
      public CharSequence modify(Entity entity, CharSequence text) {
        return entity.type == Entity.Type.HASHTAG ? "#replaced" : "pre_" + text + "_post";
      }
    });

    String result = linker.autoLink("#hash ^mention");
    assertTrue("LinkTextModifier should modify a hashtag link text", Pattern.matches(".*<a[^>]+>#replaced</a>.*", result));
    assertTrue("LinkTextModifier should modify a username link text", Pattern.matches(".*<a[^>]+>pre_mention_post</a>.*", result));

    linker.setLinkTextModifier(new Autolink.LinkTextModifier() {
      public CharSequence modify(Entity entity, CharSequence text) {
        return "pre_" + text + "_post";
      }
    });
    linker.setSymbolTag("s");
    linker.setTextWithSymbolTag("b");
    linker.setMentionIncludeSymbol(true);
    result = linker.autoLink("#hash ^mention");
    assertTrue("LinkTextModifier should modify a hashtag link text", Pattern.matches(".*<a[^>]+>pre_<s>#</s><b>hash</b>_post</a>.*", result));
    assertTrue("LinkTextModifier should modify a username link text", Pattern.matches(".*<a[^>]+>pre_<s>\\^</s><b>mention</b>_post</a>.*", result));
  }

  protected void assertAutolink(String expected, String linked) {
    assertEquals("Autolinked text should equal the input", expected, linked);
  }
}
