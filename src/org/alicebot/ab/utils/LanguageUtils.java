package org.alicebot.ab.utils;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanguageUtils {

  private static final Logger log = LoggerFactory.getLogger(LanguageUtils.class);
  
  
  /**
   * Language aware 
   * Morphological analysis of an input sentence that contains an AIML pattern.
   *
   * @param sentence
   * @param locale
   * @return morphed sentence with one space between words, preserving XML
   *         markup and AIML $ operation
   */
  public static String tokenizeSentence(String sentence, Locale locale) {
 
    // TODO: wire in as many languages as we care about here.
    // Here we have a registry of locales to chose from
    if (Locale.JAPANESE.equals(locale)) {
      String tokenized = JapaneseUtils.tokenizeSentence(sentence);
      return tokenized;
    } else {
      // no special processing
      return sentence;
    }
    
    
  }

  
}
