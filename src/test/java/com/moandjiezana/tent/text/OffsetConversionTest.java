package com.moandjiezana.tent.text;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class OffsetConversionTest extends ExtractorTest {

  private final Extractor extractor = new Extractor();

  @Test
  public void convertIndices() {
    assertOffsetConversionOk("abc", "abc");
    assertOffsetConversionOk("\ud83d\ude02abc", "abc");
    assertOffsetConversionOk("\ud83d\ude02abc\ud83d\ude02", "abc");
    assertOffsetConversionOk("\ud83d\ude02abc\ud838\ude02abc", "abc");
    assertOffsetConversionOk("\ud83d\ude02abc\ud838\ude02abc\ud83d\ude02",
            "abc");
    assertOffsetConversionOk("\ud83d\ude02\ud83d\ude02abc", "abc");
    assertOffsetConversionOk("\ud83d\ude02\ud83d\ude02\ud83d\ude02abc",
            "abc");

    assertOffsetConversionOk
            ("\ud83d\ude02\ud83d\ude02\ud83d\ude02abc\ud83d\ude02", "abc");

    // Several surrogate pairs following the entity
    assertOffsetConversionOk
            ("\ud83d\ude02\ud83d\ude02\ud83d\ude02abc\ud83d\ude02\ud83d" +
                    "\ude02\ud83d\ude02", "abc");

    // Several surrogate pairs surrounding multiple entities
    assertOffsetConversionOk
            ("\ud83d\ude02\ud83d\ude02\ud83d\ude02\ud83d\ude02abc\ud83d" +
                    "\ude02\ud83d\ude02\ud83d\ude02\ud83d\ude02abc\ud83d" +
                    "\ude02\ud83d\ude02\ud83d\ude02\ud83d\ude02", "abc");

    // unpaired low surrogate (at start)
    assertOffsetConversionOk
            ("\ude02\ud83d\ude02\ud83d\ude02\ud83d\ude02abc\ud83d" +
                    "\ude02\ud83d\ude02\ud83d\ude02\ud83d\ude02abc\ud83d" +
                    "\ude02\ud83d\ude02\ud83d\ude02\ud83d\ude02", "abc");

    // unpaired low surrogate (at end)
    assertOffsetConversionOk
            ("\ud83d\ude02\ud83d\ude02\ud83d\ude02\ud83d\ude02abc\ud83d" +
                    "\ude02\ud83d\ude02\ud83d\ude02\ud83d\ude02abc\ud83d" +
                    "\ude02\ud83d\ude02\ud83d\ude02\ude02", "abc");

    // unpaired low and high surrogates (at end)
    assertOffsetConversionOk
            ("\ud83d\ude02\ud83d\ude02\ud83d\ude02\ud83d\ude02abc\ud83d" +
                    "\ude02\ud83d\ude02\ud83d\ude02\ud83d\ude02abc\ud83d" +
                    "\ude02\ud83d\ude02\ud83d\ud83d\ude02\ude02", "abc");

    assertOffsetConversionOk("\ud83dabc\ud83d", "abc");

    assertOffsetConversionOk("\ude02abc\ude02", "abc");

    assertOffsetConversionOk("\ude02\ude02abc\ude02\ude02", "abc");

    assertOffsetConversionOk("abcabc", "abc");

    assertOffsetConversionOk("abc\ud83d\ude02abc", "abc");

    assertOffsetConversionOk("aa", "a");

    assertOffsetConversionOk("\ud83d\ude02a\ud83d\ude02a\ud83d\ude02", "a");
  }

  private void assertOffsetConversionOk(String testData, String patStr) {
    // Build an entity at the location of patStr
    final Pattern pat = Pattern.compile(patStr);
    final Matcher matcher = pat.matcher(testData);

    List<Extractor.Entity> entities = new ArrayList<Extractor.Entity>();
    List<Integer> codePointOffsets = new ArrayList<Integer>();
    List<Integer> charOffsets = new ArrayList<Integer>();
    while (matcher.find()) {
      final int charOffset = matcher.start();
      charOffsets.add(charOffset);
      codePointOffsets.add(testData.codePointCount(0, charOffset));
      entities.add(new Extractor.Entity(matcher, Extractor.Entity.Type.HASHTAG, 0, 0));
    }

    extractor.modifyIndicesFromUTF16ToToUnicode(testData, entities);

    for (int i = 0; i < entities.size(); i++) {
      assertEquals(codePointOffsets.get(i), entities.get(i).getStart());
    }

    extractor.modifyIndicesFromUnicodeToUTF16(testData, entities);

    for (int i = 0; i < entities.size(); i++) {
      // This assertion could fail if the entity location is in the middle
      // of a surrogate pair, since there is no equivalent code point
      // offset to that location. It would be pathological for an entity to
      // start at that point, so we can just let the test fail in that case.
      assertEquals(charOffsets.get(i), entities.get(i).getStart());
    }
  }
}