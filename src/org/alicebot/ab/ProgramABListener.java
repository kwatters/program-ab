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
   * Callback when category is added from a the result of matching a learnf tag
   * 
   * @param c - the category added
   */
  public void onLearnF(Bot bot, Category c);

  /**
   * Callback when category is added from a the result of matching a learn tag
   * 
   * @param c - the category added
   */
  public void onLearn(Bot bot, Category c);

}
