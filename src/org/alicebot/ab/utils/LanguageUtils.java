package org.alicebot.ab.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
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
    String language = locale.getLanguage();
    if (language.equals(Locale.ENGLISH.getLanguage())) {
      // TODO: consider the standard analyzer to include things like stop words.
      // no language specific tokenization right now.
      return sentence;
    } else if (language.equals(Locale.JAPANESE.getLanguage())) {
      // TODO: implement a performance improvement that we don't re-create this each time.  I suspect this is expensive
      JapaneseAnalyzer jaT = new JapaneseAnalyzer(null, JapaneseTokenizer.DEFAULT_MODE, null, new HashSet<String>());
      return LanguageUtils.tokenizeString(jaT, sentence);
    } else if (language.equals(Locale.CHINESE.getLanguage()) || language.equals(Locale.KOREAN.getLanguage())) {
      // use this for chinese and korean?
      // TODO: implement a performance improvement that we don't re-create this each time.  I suspect this is expensive
      CJKAnalyzer cjkTokenizer = new CJKAnalyzer();
      return LanguageUtils.tokenizeString(cjkTokenizer, sentence);
    } else {
      // default behavior for all other languages until we need to do something special
      // perhaps do word de-compounding for german  ? or other?
      return sentence;
    }
  }
  
  private static String tokenizeString(Analyzer analyzer, String string) {
    // we need to preserve the * and _ for wildcard matching in aiml. 
    // normally lucene tokenizers will strip those away.
    String preProcessed = string.replaceAll("\\*", " AIMLSTARAIML ");
    preProcessed = preProcessed.replaceAll("\\_", " AIMLUNDERSCOREAIML ");
    List<String> result = new ArrayList<String>();
    try {
      TokenStream stream  = analyzer.tokenStream(null, new StringReader(preProcessed));
      stream.reset();
      while (stream.incrementToken()) {
        String token = stream.getAttribute(CharTermAttribute.class).toString();
        if (token.equalsIgnoreCase("AIMLSTARAIML")) {
          result.add("*");
        } else if (token.equalsIgnoreCase("AIMLUNDERSCOREAIML")) {
          result.add("_");
        } else {
          result.add(token);
        }
      }
    } catch (IOException e) {
      // not thrown b/c we're using a string reader...
      throw new RuntimeException(e);
    }
    return String.join(" " , result);
  }
  
}
