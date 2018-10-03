package org.alicebot.ab;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tuple extends HashMap<String, String> {

  private static final Logger log = LoggerFactory.getLogger(Tuple.class);

  public static int index = 0;
  public static HashMap<String, Tuple> tupleMap = new HashMap<String, Tuple>();
  public HashSet<String> visibleVars = new HashSet<String>();
  String name;

  @Override
  public boolean equals(Object o) {
    // log.info("Calling equals");
    // if (this == o) return true;
    /*
     * if (o == null || getClass() != o.getClass()) { log.info("unequal 1");
     * return false; } if (!super.equals(o)) { log.info("unequal 2"); return
     * false; }
     */
    Tuple tuple = (Tuple) o;

    // if (!visibleVars.equals(tuple.visibleVars)) return false;
    if (visibleVars.size() != tuple.visibleVars.size()) {
      // log.info("Tuple: "+name+"!="+tuple.name+" because size
      // "+visibleVars.size()+"!="+tuple.visibleVars.size());
      return false;
    }
    // log.info("Tuple visibleVars = "+visibleVars+" tuple.visibleVars
    // = "+tuple.visibleVars);
    for (String x : visibleVars) {
      // log.info("Tuple:
      // get("+x+")="+get(x)+"tuple.get("+x+")="+tuple.get(x));
      if (!tuple.visibleVars.contains(x)) {
        // log.info("Tuple: "+name+"!="+tuple.name+" because
        // !tuple.visibleVars.contains("+x+")");
        return false;
      } else if (get(x) != null && !get(x).equals(tuple.get(x))) {
        // log.info("Tuple: "+name+"!="+tuple.name+" because
        // get("+x+")="+get(x)+" and tuple.get("+x+")="+tuple.get(x));
        return false;
      }
    }
    // log.info("Tuple: values = "+values());
    // log.info("Tuple: tuple.values = "+tuple.values());
    if (values().contains(MagicStrings.unbound_variable))
      return false;
    if (tuple.values().contains(MagicStrings.unbound_variable))
      return false;
    // log.info("Tuple: "+name+"="+tuple.name);
    return true;
  }

  @Override
  public int hashCode() {
    // log.info("Calling hashCode");
    int result = 1;
    for (String x : visibleVars) {
      result = 31 * result + x.hashCode();
      if (get(x) != null)
        result = 31 * result + get(x).hashCode();
    }

    return result;
  }

  public Tuple(HashSet<String> varSet, HashSet<String> visibleVars, Tuple tuple) {
    super();
    // log.info("varSet="+varSet);
    // log.info("visbileVars="+visibleVars);
    if (visibleVars != null)
      this.visibleVars.addAll(visibleVars);
    if (varSet == null && tuple != null) {
      for (String key : tuple.keySet())
        put(key, tuple.get(key));
      this.visibleVars.addAll(tuple.visibleVars);
    }
    if (varSet != null) {
      for (String key : varSet)
        put(key, MagicStrings.unbound_variable);
    }
    name = "tuple" + index;
    index++;
    tupleMap.put(name, this);
  }

  public Tuple(Tuple tuple) {
    this(null, null, tuple);
  }

  public Tuple(HashSet<String> varSet, HashSet<String> visibleVars) {
    this(varSet, visibleVars, null);
  }

  public Set<String> getVars() {
    return keySet();
  }

  public String printVars() {
    String result = "";
    for (String x : getVars()) {
      if (visibleVars.contains(x))
        result = result + " " + x;
      else
        result = result + " [" + x + "]";
    }
    return result;
  }

  public String getValue(String var) {
    String result = get(var);
    if (result == null)
      return MagicStrings.default_get;
    else
      return result;
  }

  public void bind(String var, String value) {
    if (get(var) != null && !get(var).equals(MagicStrings.unbound_variable))
      log.info(var + " already bound to " + get(var));
    else
      put(var, value);

  }

  public String printTuple() {
    String result = "\n";
    for (String x : keySet()) {
      result += x + "=" + get(x) + "\n";
    }
    return result.trim();
  }
}
