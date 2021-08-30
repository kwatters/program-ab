package org.alicebot.ab;

/**
 * Base interface to get callbacks when things change on a bot.
 *
 */
public interface ProgramABListener {
  
  /**
   * A callback when a predicate is changed for a bot session
   * @param predicateName
   * @param result
   */
  public void onChangePredicate(Chat chat, String predicateName, String result);

  /**
   * Callback when category is added from a the result of matching a learn tag
   * 
   * @param chat - current chat that processed the learn tag
   * @param c - learned category
   */
  public void onLearnF(Chat chat, Category c);

  /**
   * Callback when category is added from a the result of matching a learn tag
   * 
   * @param chat - current chat that processed the learnf tag
   * @param c - learned category
   */
  public void onLearn(Chat chat, Category c);

}
