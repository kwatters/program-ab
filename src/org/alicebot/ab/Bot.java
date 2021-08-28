package org.alicebot.ab;

/* Program AB Reference AIML 2.0 implementation
        Copyright (C) 2013 ALICE A.I. Foundation
        Contact: info@alicebot.org
        Contact: info@alicebot.org

        This library is free software; you can redistribute it and/or
        modify it under the terms of the GNU Library General Public
        License as published by the Free Software Foundation; either
        version 2 of the License, or (at your option) any later version.

        This library is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
        Library General Public License for more details.

        You should have received a copy of the GNU Library General Public
        License along with this library; if not, write to the
        Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
        Boston, MA  02110-1301, USA.
 */
import org.alicebot.ab.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Class representing the AIML bot
 */
public class Bot {

  private static final Logger log = LoggerFactory.getLogger(Bot.class);

  public final Properties properties = new Properties();
  public final PreProcessor preProcessor;
  public final Graphmaster brain;
  public Graphmaster learnfGraph;
  public Graphmaster learnGraph;

  // public Graphmaster unfinishedGraph;
  // public final ArrayList<Category> categories;

  public String name = MagicStrings.default_bot_name;
  public HashMap<String, AIMLSet> setMap = new HashMap<String, AIMLSet>();
  public HashMap<String, AIMLMap> mapMap = new HashMap<String, AIMLMap>();
  public HashSet<String> pronounSet = new HashSet<String>();
  public String root_path = "c:/ab";
  public String bot_path = root_path + "/bots";
  public String bot_name_path = bot_path + "/super";
  public String aimlif_path = bot_path + "/aimlif";
  public String aiml_path = bot_path + "/aiml";
  public String config_path = bot_path + "/config";
  public String log_path = bot_path + "/log";
  public String sets_path = bot_path + "/sets";
  public String maps_path = bot_path + "/maps";
  private SraixHandler sraixHandler = null;
  public Locale locale = MagicBooleans.defaultLocale;

  public ProgramABListener listener;
  
  /**
   * Set all directory path variables for this bot
   *
   * @param bot_dir_path
   *          root directory of the bot's folder
   */
  public void setAllPaths(String bot_dir_path) {
    if (MagicBooleans.trace_mode)
      log.info("Name = " + name + " Path = " + bot_dir_path);
    aiml_path = bot_dir_path + "/aiml";
    aimlif_path = bot_dir_path + "/aimlif";
    config_path = bot_dir_path + "/config";
    log_path = bot_dir_path + "/logs";
    sets_path = bot_dir_path + "/sets";
    maps_path = bot_dir_path + "/maps";
    if (MagicBooleans.trace_mode) {
      log.info(root_path);
      log.info(bot_path);
      log.info(bot_dir_path);
      log.info(aiml_path);
      log.info(aimlif_path);
      log.info(config_path);
      log.info(log_path);
      log.info(sets_path);
      log.info(maps_path);
    }
  }

  
  /**
   * Set all directory path variables for this bot
   *
   * @param root
   *          root directory of Program AB
   * @param name
   *          name of bot
   */
  public void setAllPaths(String root, String name) {
    bot_path = root + "/bots";
    bot_name_path = bot_path + "/" + name;
    if (MagicBooleans.trace_mode)
      log.info("Name = " + name + " Path = " + bot_name_path);
    aiml_path = bot_name_path + "/aiml";
    aimlif_path = bot_name_path + "/aimlif";
    config_path = bot_name_path + "/config";
    log_path = bot_name_path + "/logs";
    sets_path = bot_name_path + "/sets";
    maps_path = bot_name_path + "/maps";
    if (MagicBooleans.trace_mode) {
      log.info(root_path);
      log.info(bot_path);
      log.info(bot_name_path);
      log.info(aiml_path);
      log.info(aimlif_path);
      log.info(config_path);
      log.info(log_path);
      log.info(sets_path);
      log.info(maps_path);
    }
  }

  /**
   * Constructor (default action, default path, default bot name)
   */
  public Bot() {
    this(MagicStrings.default_bot, (ProgramABListener) null);
  }

  /**
   * Constructor (default action, default path)
   * 
   * @param name
   */
  public Bot(String name, ProgramABListener listener) {
    this(name, MagicStrings.root_path, listener);
  }

  public Bot(String name, String path, ProgramABListener listener) {
    this(name, path, "auto", MagicBooleans.defaultLocale, listener);
  }
  
  /**
   * Constructor (default action)
   *
   * @param name
   * @param path
   */
  public Bot(String name, String path) {
    this(name, path, "auto", MagicBooleans.defaultLocale, null);
  }

  public Bot(String name) {
    this(name, MagicStrings.root_path, "auto", MagicBooleans.defaultLocale, null);
  }
  
  public Bot(String botName, String path, String action, Locale locale) {
    this(botName, path, "auto", locale, null);
  }



  public Bot(String name, String path, Locale locale, ProgramABListener listener) {
    this(name, path, "auto", locale, listener);
  }
  
  public Bot(String name, String path, Locale locale) {
    this(name, path, "auto", locale, null);
  }

  
  /**
   * Constructor
   *
   * @param name
   *          name of bot
   * @param path
   *          root path of Program AB
   * @param action
   *          Program AB action
   */
  public Bot(String name, String path, String action, Locale locale, ProgramABListener listener) {
    int cnt = 0;
    int elementCnt = 0;
    this.name = name;
    this.locale = locale;
    setAllPaths(path);
    this.brain = new Graphmaster(this);
    this.listener = listener;

    this.learnfGraph = new Graphmaster(this, "learnf");
    this.learnGraph = new Graphmaster(this, "learn");
    // this.unfinishedGraph = new Graphmaster(this);
    // this.categories = new ArrayList<Category>();

    preProcessor = new PreProcessor(this);
    addProperties();
    cnt = addAIMLSets();
    if (MagicBooleans.trace_mode)
      log.info("Loaded " + cnt + " set elements.");
    cnt = addAIMLMaps();
    if (MagicBooleans.trace_mode)
      log.info("Loaded " + cnt + " map elements");
    this.pronounSet = getPronouns();
    AIMLSet number = new AIMLSet(MagicStrings.natural_number_set_name, this);
    setMap.put(MagicStrings.natural_number_set_name, number);
    AIMLMap successor = new AIMLMap(MagicStrings.map_successor, this);
    mapMap.put(MagicStrings.map_successor, successor);
    AIMLMap predecessor = new AIMLMap(MagicStrings.map_predecessor, this);
    mapMap.put(MagicStrings.map_predecessor, predecessor);
    AIMLMap singular = new AIMLMap(MagicStrings.map_singular, this);
    mapMap.put(MagicStrings.map_singular, singular);
    AIMLMap plural = new AIMLMap(MagicStrings.map_plural, this);
    mapMap.put(MagicStrings.map_plural, plural);
    // log.info("setMap = "+setMap);
    Date aimlDate = new Date(new File(aiml_path).lastModified());
    Date aimlIFDate = new Date(new File(aimlif_path).lastModified());
    if (MagicBooleans.trace_mode)
      log.info("AIML modified " + aimlDate + " AIMLIF modified " + aimlIFDate);
    // readUnfinishedIFCategories();
    MagicStrings.pannous_api_key = Utilities.getPannousAPIKey(this);
    MagicStrings.pannous_login = Utilities.getPannousLogin(this);
    if (action.equals("aiml2csv"))
      addCategoriesFromAIML();
    else if (action.equals("csv2aiml"))
      addCategoriesFromAIMLIF();
    else if (action.equals("chat-app")) {
      if (MagicBooleans.trace_mode)
        log.info("Loading only AIMLIF files");
      cnt = addCategoriesFromAIMLIF();
    } else if (aimlDate.after(aimlIFDate)) {
      if (MagicBooleans.trace_mode)
        log.info("AIML modified after AIMLIF");
      cnt = addCategoriesFromAIML();
      writeAIMLIFFiles();
    } else {
      addCategoriesFromAIMLIF();
      if (brain.getCategories().size() == 0) {
        log.info("No AIMLIF Files found.  Looking for AIML");
        cnt = addCategoriesFromAIML();
      }
    }
    Category b = new Category(0, "PROGRAM VERSION", "*", "*", MagicStrings.program_name_version, "update.aiml");
    brain.addCategory(b);
    brain.nodeStats();
    learnfGraph.nodeStats();
  }

  HashSet<String> getPronouns() {
    HashSet<String> pronounSet = new HashSet<String>();
    String pronouns = Utilities.getFile(config_path + "/pronouns.txt");
    String[] splitPronouns = pronouns.split("\n");
    for (int i = 0; i < splitPronouns.length; i++) {
      String p = splitPronouns[i].trim();
      if (p.length() > 0)
        pronounSet.add(p);
    }
    if (MagicBooleans.trace_mode)
      log.info("Read pronouns: " + pronounSet);
    return pronounSet;
  }

  /**
   * add an array list of categories with a specific file name
   *
   * @param file
   *          name of AIML file
   * @param moreCategories
   *          list of categories
   */
  void addMoreCategories(String file, ArrayList<Category> moreCategories) {
    if (file.contains(MagicStrings.deleted_aiml_file)) {
      /*
       * for (Category c : moreCategories) {
       * //log.info("Delete "+c.getPattern()); deletedGraph.addCategory(c); }
       */

    } else if (file.contains(MagicStrings.learnf_aiml_file)) {
      if (MagicBooleans.trace_mode)
        log.info("Reading Learnf file");
      for (Category c : moreCategories) {
        brain.addCategory(c);
        learnfGraph.addCategory(c);
        // patternGraph.addCategory(c);
      }
      // this.categories.addAll(moreCategories);
    } else {
      for (Category c : moreCategories) {
        // log.info("Brain size="+brain.root.size());
        // brain.printgraph();
        brain.addCategory(c);
        // patternGraph.addCategory(c);
        // brain.printgraph();
      }
      // this.categories.addAll(moreCategories);
    }
  }

  /**
   * Load all brain categories from AIML directory
   */
  int addCategoriesFromAIML() {
    Timer timer = new Timer();
    timer.start();
    int cnt = 0;
    try {
      // Directory path here
      String file;
      File folder = new File(aiml_path);
      if (folder.exists()) {
        File[] listOfFiles = IOUtils.listFiles(folder);
        if (MagicBooleans.trace_mode)
          log.info("Loading AIML files from " + aiml_path);
        for (File listOfFile : listOfFiles) {
          if (listOfFile.isFile()) {
            file = listOfFile.getName();
            if (file.endsWith(".aiml") || file.endsWith(".AIML")) {
              if (MagicBooleans.trace_mode)
                log.info(file);
              try {
                ArrayList<Category> moreCategories = AIMLProcessor.AIMLToCategories(aiml_path, file, locale);
                addMoreCategories(file, moreCategories);
                cnt += moreCategories.size();
              } catch (Exception iex) {
                log.info("Problem loading " + file);
                iex.printStackTrace();
              }
            }
          }
        }
      } else
        log.info("addCategoriesFromAIML: " + aiml_path + " does not exist.");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    if (MagicBooleans.trace_mode)
      log.info("Loaded " + cnt + " categories in " + timer.elapsedTimeSecs() + " sec");
    return cnt;
  }

  /**
   * load all brain categories from AIMLIF directory
   */
  public int addCategoriesFromAIMLIF() {
    Timer timer = new Timer();
    timer.start();
    int cnt = 0;
    try {
      // Directory path here
      String file;
      File folder = new File(aimlif_path);
      if (folder.exists()) {
        File[] listOfFiles = IOUtils.listFiles(folder);
        if (MagicBooleans.trace_mode)
          log.info("Loading AIML files from " + aimlif_path);
        for (File listOfFile : listOfFiles) {
          if (listOfFile.isFile()) {
            file = listOfFile.getName();
            if (file.endsWith(MagicStrings.aimlif_file_suffix) || file.endsWith(MagicStrings.aimlif_file_suffix.toUpperCase())) {
              if (MagicBooleans.trace_mode)
                log.info(file);
              try {
                ArrayList<Category> moreCategories = readIFCategories(aimlif_path + "/" + file);
                cnt += moreCategories.size();
                addMoreCategories(file, moreCategories);
                // MemStats.memStats();
              } catch (Exception iex) {
                log.info("Problem loading " + file);
                iex.printStackTrace();
              }
            }
          }
        }
      } else
        log.info("addCategoriesFromAIMLIF: " + aimlif_path + " does not exist.");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    if (MagicBooleans.trace_mode)
      log.info("Loaded " + cnt + " categories in " + timer.elapsedTimeSecs() + " sec");
    return cnt;
  }

  /**
   * write all AIML and AIMLIF categories
   */
  public void writeQuit() {
    writeAIMLIFFiles();
    // log.info("Wrote AIMLIF Files");
    writeAIMLFiles();
    // log.info("Wrote AIML Files");
    /*
     * updateUnfinishedCategories(); writeUnfinishedIFCategories();
     */
  }

  /**
   * read categories from specified AIMLIF file into specified graph
   *
   * @param graph
   *          Graphmaster to store categories
   * @param fileName
   *          file name of AIMLIF file
   */
  public int readCertainIFCategories(Graphmaster graph, String fileName) {
    int cnt = 0;
    File file = new File(aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix);
    if (file.exists()) {
      try {
        ArrayList<Category> certainCategories = readIFCategories(aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix);
        for (Category d : certainCategories)
          graph.addCategory(d);
        cnt = certainCategories.size();
        log.info("readCertainIFCategories " + cnt + " categories from " + fileName + MagicStrings.aimlif_file_suffix);
      } catch (Exception iex) {
        log.info("Problem loading " + fileName);
        iex.printStackTrace();
      }
    } else
      log.info("No " + aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix + " file found");
    return cnt;
  }

  /**
   * write certain specified categories as AIMLIF files
   *
   * @param graph
   *          the Graphmaster containing the categories to write
   * @param file
   *          the destination AIMLIF file
   */
  public void writeCertainIFCategories(Graphmaster graph, String file) {
    if (MagicBooleans.trace_mode)
      log.info("writeCertainIFCaegories " + file + " size= " + graph.getCategories().size());
    writeIFCategories(graph.getCategories(), file + MagicStrings.aimlif_file_suffix);
    File dir = new File(aimlif_path);
    dir.setLastModified(new Date().getTime());
  }

  /**
   * write deleted categories to AIMLIF file
   */

  /**
   * write learned categories to AIMLIF file
   */
  public void writeLearnfIFCategories() {
    writeCertainIFCategories(learnfGraph, MagicStrings.learnf_aiml_file);
  }

  /**
   * write unfinished categories to AIMLIF file
   */
  /*
   * public void writeUnfinishedIFCategories() {
   * writeCertainIFCategories(unfinishedGraph,
   * MagicStrings.unfinished_aiml_file); }
   */

  /**
   * write categories to AIMLIF file
   *
   * @param cats
   *          array list of categories
   * @param filename
   *          AIMLIF filename
   */
  public void writeIFCategories(ArrayList<Category> cats, String filename) {
    // log.info("writeIFCategories "+filename);
    BufferedWriter bw = null;
    File existsPath = new File(aimlif_path);
    if (existsPath.exists())
      try {
        // Construct the bw object (force it to be UTF-8)
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aimlif_path + "/" + filename), StandardCharsets.UTF_8));

        for (Category category : cats) {
          bw.write(Category.categoryToIF(category));
          bw.newLine();
        }
      } catch (FileNotFoundException ex) {
        ex.printStackTrace();
      } catch (IOException ex) {
        ex.printStackTrace();
      } finally {
        // Close the bw
        try {
          if (bw != null) {
            bw.flush();
            bw.close();
          }
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
  }

  /**
   * Write all AIMLIF files from bot brain
   */
  public void writeAIMLIFFiles() {
    if (MagicBooleans.disable_aimlif) {
      log.info("AIMLIF has been disabled in ProgramAB, if you want to use it again, adjust the MagicBooleans.disable_aimlif property");
      return;
    }
    if (MagicBooleans.trace_mode)
      log.info("writeAIMLIFFiles");
    File aimlIfPath = new File(aimlif_path);
    if (!aimlIfPath.exists())
      aimlIfPath.mkdirs();
    HashMap<String, BufferedWriter> fileMap = new HashMap<String, BufferedWriter>();
    Category b = new Category(0, "BRAIN BUILD", "*", "*", new Date().toString(), "update.aiml");
    brain.addCategory(b);
    ArrayList<Category> brainCategories = brain.getCategories();
    Collections.sort(brainCategories, Category.CATEGORY_NUMBER_COMPARATOR);
    for (Category c : brainCategories) {
      try {
        BufferedWriter bw;
        String fileName = c.getFilename();
        if (fileMap.containsKey(fileName))
          bw = fileMap.get(fileName);
        else {
          String fn = aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix;
          bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), StandardCharsets.UTF_8));
          fileMap.put(fileName, bw);
        }
        bw.write(Category.categoryToIF(c));
        bw.newLine();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    Set set = fileMap.keySet();
    for (Object aSet : set) {
      BufferedWriter bw = fileMap.get(aSet);
      // Close the bw
      try {
        if (bw != null) {
          bw.flush();
          bw.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    File dir = new File(aimlif_path);
    dir.setLastModified(new Date().getTime());
  }

  /**
   * Write all AIML files. Adds categories for BUILD and DEVELOPMENT ENVIRONMENT
   */
  public void writeAIMLFiles() {
    if (MagicBooleans.trace_mode)
      log.info("writeAIMLFiles");
    HashMap<String, BufferedWriter> fileMap = new HashMap<String, BufferedWriter>();
    Category b = new Category(0, "BRAIN BUILD", "*", "*", new Date().toString(), "update.aiml");
    brain.addCategory(b);
    // b = new Category(0, "PROGRAM VERSION", "*", "*",
    // MagicStrings.program_name_version, "update.aiml");
    // brain.addCategory(b);
    ArrayList<Category> brainCategories = brain.getCategories();
    Collections.sort(brainCategories, Category.CATEGORY_NUMBER_COMPARATOR);
    for (Category c : brainCategories) {

      if (!c.getFilename().equals(MagicStrings.null_aiml_file))
        try {
          // log.info("Writing "+c.getCategoryNumber()+" "+c.inputThatTopic());
          BufferedWriter bw;
          String fileName = c.getFilename();
          if (fileMap.containsKey(fileName))
            bw = fileMap.get(fileName);
          else {
            String copyright = Utilities.getCopyright(this, fileName);
            String fn = aiml_path + "/" + fileName;
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fn), StandardCharsets.UTF_8));
            fileMap.put(fileName, bw);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<aiml>\n");
            bw.write(copyright);
            // bw.newLine();
          }
          bw.write(Category.categoryToAIML(c) + "\n");
          // bw.newLine();
        } catch (Exception ex) {
          ex.printStackTrace();
        }
    }
    Set set = fileMap.keySet();
    for (Object aSet : set) {
      BufferedWriter bw = fileMap.get(aSet);
      // Close the bw
      try {
        if (bw != null) {
          bw.write("</aiml>\n");
          bw.flush();
          bw.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();

      }

    }
    File dir = new File(aiml_path);
    dir.setLastModified(new Date().getTime());
  }

  /**
   * load bot properties
   */
  void addProperties() {
    try {
      properties.getProperties(config_path + "/properties.txt");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * read AIMLIF categories from a file into bot brain
   *
   * @param filename
   *          name of AIMLIF file
   * @return array list of categories read
   */
  public ArrayList<Category> readIFCategories(String filename) {
    ArrayList<Category> categories = new ArrayList<Category>();
    try {
      // Open the file that is the first
      // command line parameter
      // Get the object
      BufferedReader br;
      br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));

      String strLine;
      // Read File Line By Line
      while ((strLine = br.readLine()) != null) {
        try {
          Category c = Category.IFToCategory(strLine);
          categories.add(c);
        } catch (Exception ex) {
          log.info("Invalid AIMLIF in " + filename + " line " + strLine);
        }
      }
      // Close the input stream
      br.close();
    } catch (Exception e) {// Catch exception if any
      System.err.println("Error: " + e.getMessage());
    }
    return categories;
  }

  /**
   * Load all AIML Sets
   */
  int addAIMLSets() {
    int cnt = 0;
    Timer timer = new Timer();
    timer.start();
    try {
      // Directory path here
      String file;
      File folder = new File(sets_path);
      if (folder.exists()) {
        File[] listOfFiles = IOUtils.listFiles(folder);
        if (MagicBooleans.trace_mode)
          log.info("Loading AIML Sets files from " + sets_path);
        for (File listOfFile : listOfFiles) {
          if (listOfFile.isFile()) {
            file = listOfFile.getName();
            if (file.endsWith(".txt") || file.endsWith(".TXT")) {
              if (MagicBooleans.trace_mode)
                log.info(file);
              String setName = file.substring(0, file.length() - ".txt".length());
              if (MagicBooleans.trace_mode)
                log.info("Read AIML Set " + setName);
              AIMLSet aimlSet = new AIMLSet(setName, this);
              cnt += aimlSet.readAIMLSet(this);
              setMap.put(setName, aimlSet);
            }
          }
        }
      } else
        log.info("addAIMLSets: " + sets_path + " does not exist.");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return cnt;
  }

  /**
   * Load all AIML Maps
   */
  int addAIMLMaps() {
    int cnt = 0;
    Timer timer = new Timer();
    timer.start();
    try {
      // Directory path here
      String file;
      File folder = new File(maps_path);
      if (folder.exists()) {
        File[] listOfFiles = IOUtils.listFiles(folder);
        if (MagicBooleans.trace_mode)
          log.info("Loading AIML Map files from " + maps_path);
        for (File listOfFile : listOfFiles) {
          if (listOfFile.isFile()) {
            file = listOfFile.getName();
            if (file.endsWith(".txt") || file.endsWith(".TXT")) {
              if (MagicBooleans.trace_mode)
                log.info(file);
              String mapName = file.substring(0, file.length() - ".txt".length());
              if (MagicBooleans.trace_mode)
                log.info("Read AIML Map " + mapName);
              AIMLMap aimlMap = new AIMLMap(mapName, this);
              cnt += aimlMap.readAIMLMap(this);
              mapMap.put(mapName, aimlMap);
            }
          }
        }
      } else
        log.info("addAIMLMaps: " + maps_path + " does not exist.");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return cnt;
  }

  public void deleteLearnfCategories() {
    ArrayList<Category> learnfCategories = learnfGraph.getCategories();
    for (Category c : learnfCategories) {
      Nodemapper n = brain.findNode(c);
      log.info("Found node " + n + " for " + c.inputThatTopic());
      if (n != null)
        n.category = null;
    }
    learnfGraph = new Graphmaster(this);
  }

  public void deleteLearnCategories() {
    ArrayList<Category> learnCategories = learnGraph.getCategories();
    for (Category c : learnCategories) {
      Nodemapper n = brain.findNode(c);
      log.info("Found node " + n + " for " + c.inputThatTopic());
      if (n != null)
        n.category = null;
    }
    learnGraph = new Graphmaster(this);
  }

  /**
   * check Graphmaster for shadowed categories
   */
  public void shadowChecker() {
    shadowChecker(brain.root);
  }

  /**
   * traverse graph and test all categories found in leaf nodes for shadows
   *
   * @param node
   */
  void shadowChecker(Nodemapper node) {
    if (NodemapperOperator.isLeaf(node)) {
      String input = node.category.getPattern();
      input = brain.replaceBotProperties(input);
      input = input.replace("*", "XXX").replace("_", "XXX").replace("^", "").replace("#", "");
      String that = node.category.getThat().replace("*", "XXX").replace("_", "XXX").replace("^", "").replace("#", "");
      String topic = node.category.getTopic().replace("*", "XXX").replace("_", "XXX").replace("^", "").replace("#", "");
      input = instantiateSets(input);
      log.info("shadowChecker: input=" + input);
      Nodemapper match = brain.match(input, that, topic);
      if (match != node) {
        log.info("" + Graphmaster.inputThatTopic(input, that, topic));
        log.info("MATCHED:     " + match.category.inputThatTopic());
        log.info("SHOULD MATCH:" + node.category.inputThatTopic());
      }
    } else {
      for (String key : NodemapperOperator.keySet(node)) {
        shadowChecker(NodemapperOperator.get(node, key));
      }
    }
  }

  public String instantiateSets(String pattern) {
    String[] splitPattern = pattern.split(" ");
    pattern = "";
    for (String x : splitPattern) {
      if (x.startsWith("<SET>")) {
        String setName = AIMLProcessor.trimTag(x, "SET");
        AIMLSet set = setMap.get(setName);
        if (set != null)
          x = "FOUNDITEM";
        else
          x = "NOTFOUND";
      }
      pattern = pattern + " " + x;
    }
    return pattern.trim();
  }

  // If this is not null, the sraix patterns will callback to this handler to
  // handle the response.
  public void setSraixHandler(SraixHandler sraixHandler) {
    this.sraixHandler = sraixHandler;
  }

  public SraixHandler getSraixHandler() {
    return sraixHandler;
  }
}
