package org.alicebot.ab;

public interface ProgramABListener {
  
    public void onAddCategory(Category category);

    public void onChangePredicate(String predicateName, String result);

}
