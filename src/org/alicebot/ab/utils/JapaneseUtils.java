package org.alicebot.ab.utils;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JapaneseUtils {

  private static final Logger log = LoggerFactory.getLogger(JapaneseUtils.class);
  
  /**
   * Tokenize a fragment of the input that contains only text
   *
   * @param fragment
   *          fragment of input containing only text and no XML tags
   * @return tokenized fragment
   */
  public static String tokenizeFragment(String fragment) {
    // log.info("buildFragment "+fragment);
    // String result = "";
    // for(Morpheme e : Tagger.parse(fragment)) {
    // result += e.surface+" ";
    // //
    // // log.info("Feature "+e.feature+" Surface="+e.surface);
    // }
    // return result.trim();
    // TODO: re-implement language specific / japanese tokenization
    return fragment.trim();
  }

  /**
   * Morphological analysis of an input sentence that contains an AIML pattern.
   *
   * @param sentence
   * @return morphed sentence with one space between words, preserving XML
   *         markup and AIML $ operation
   */
  public static String tokenizeSentence(String sentence) {
    // log.info("tokenizeSentence "+sentence);
    if (!MagicBooleans.jp_tokenize)
      return sentence;
    String result = "";
    result = tokenizeXML(sentence);
    while (result.contains("$ "))
      result = result.replace("$ ", "$");
    while (result.contains("  "))
      result = result.replace("  ", " ");
    while (result.contains("anon "))
      result = result.replace("anon ", "anon"); // for Triple Store
    result = result.trim();
    // if (MagicBooleans.trace_mode) log.info("tokenizeSentence
    // '"+sentence+"'-->'"+result+"'");
    return result;
  }

  public static String tokenizeXML(String xmlExpression) {
    // log.info("tokenizeXML "+xmlExpression);
    String response = MagicStrings.template_failed;
    try {
      xmlExpression = "<sentence>" + xmlExpression + "</sentence>";
      Node root = DomUtils.parseString(xmlExpression);
      response = recursEval(root);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return AIMLProcessor.trimTag(response, "sentence");
  }

  private static String recursEval(Node node) {
    try {
      String nodeName = node.getNodeName();
      // log.info("recursEval "+nodeName);
      if (nodeName.equals("#text"))
        return tokenizeFragment(node.getNodeValue());
      else if (nodeName.equals("sentence"))
        return evalTagContent(node);
      else
        return (genericXML(node));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return "JP Morph Error";
  }

  public static String genericXML(Node node) {
    // log.info("genericXML "+node.getNodeName());
    String result = evalTagContent(node);
    return unevaluatedXML(result, node);
  }

  public static String evalTagContent(Node node) {
    String result = "";
    // log.info("evalTagContent "+node.getNodeName());
    try {
      NodeList childList = node.getChildNodes();
      for (int i = 0; i < childList.getLength(); i++) {
        Node child = childList.item(i);
        result += recursEval(child);
      }
    } catch (Exception ex) {
      log.info("Something went wrong with evalTagContent");
      ex.printStackTrace();
    }
    return result;
  }

  private static String unevaluatedXML(String result, Node node) {
    String nodeName = node.getNodeName();
    String attributes = "";
    if (node.hasAttributes()) {
      NamedNodeMap XMLAttributes = node.getAttributes();
      for (int i = 0; i < XMLAttributes.getLength(); i++)

      {
        attributes += " " + XMLAttributes.item(i).getNodeName() + "=\"" + XMLAttributes.item(i).getNodeValue() + "\"";
      }
    }
    if (result.equals(""))
      return " <" + nodeName + attributes + "/> ";
    else
      return " <" + nodeName + attributes + ">" + result + "</" + nodeName + "> "; // add
                                                                                   // spaces
  }
}
