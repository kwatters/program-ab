package org.alicebot.ab;

/**
 * Base interface to get callbacks when things change on a bot.
 *
 */
public interface ProgramABListener {
  
  /**
   * A callback when a category is added
   * @param category
   */
  public void onAddCategory(Category category);

  /**
   * A callback when a predicate is changed for a bot session
   * @param predicateName
   * @param result
   */
  public void onChangePredicate(String predicateName, String result);

}
