package org.alicebot.ab;

import java.util.Locale;

public interface SraixHandler {

  public String sraix(Chat chatSession, String input, String defaultResponse, String hint, String host, String botid, String apiKey, String limit, Locale locale);

}
