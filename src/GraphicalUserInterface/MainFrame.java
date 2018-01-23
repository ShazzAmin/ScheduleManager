package GraphicalUserInterface;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import Schedule.Activity;
import Schedule.Day;
import Schedule.Period;
import Schedule.Person;

/**
 * A graphical user interface for managing schedules.
 *
 * @author Shazz Amin
 * @version 1.0 2017-01-28
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame
{
   // class variables
   private static final char ELLIPSIS = '\u2026';
   private static final String FILE_FORMAT = "scdl";
   private static final String ICON_FILE = "icon.png";
   private static final int SECTION_LIST_ROW_COUNT = 50;
   private static final int SECTION_PANEL_PADDING = 5;
   private static final int SCHEDULE_PANEL_VERTICAL_GAP = 15;
   private static final Font SECTION_TITLE_TEXT_FONT = new Font("Arial", Font.BOLD, 24);
   private static final String TITLE = "Schedule Manager";
   private static final String VERSION = "v1.0";

   // instance fields
   private ActivitiesPanel activitiesPanel;
   private ArrayListModel<Activity> activity;
   private File currentFile;
   private ArrayListModel<Day> day;
   private DaysPanel daysPanel;
   private FileManager fileManager;
   private boolean hasChanged;
   private ArrayListModel<Period> period;
   private PeriodsPanel periodsPanel;
   private ArrayListModel<Person> person;
   private PersonsPanel personsPanel;

   /*
      constructors
   */

   /**
    * Constructs the main frame.
    */
   public MainFrame()
   {
      super();

      fileManager = new FileManager();

      setTitle(TITLE);
      getClass().getClassLoader().getResource(ICON_FILE);
      setIconImage((new ImageIcon(getClass().getClassLoader().getResource(ICON_FILE))).getImage());
      setJMenuBar(makeMenuBar());
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      setExtendedState(JFrame.MAXIMIZED_BOTH);
      addWindowListener(
         new WindowListener()
         {
            public void windowActivated(WindowEvent event) { }
            public void windowClosed(WindowEvent event) { }

            public void windowClosing(WindowEvent event)
            {
               quit();
            }

            public void windowDeactivated(WindowEvent event) { }

            public void windowDeiconified(WindowEvent event) { }

            public void windowIconified(WindowEvent event) { }

            public void windowOpened(WindowEvent event) { }
         }
      );

      JPanel contentPane = new JPanel();
      contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
      contentPane.add(makeDataControlPanel());
      contentPane.add(new ScheduleGeneratorPanel());
      setContentPane(contentPane);

      pack();
      setVisible(true);

      setHasChanged(false);
      createNew();
   }

   /*
      mutators
   */

   private void setHasChanged(boolean hasChanged)
   {
      this.hasChanged = hasChanged;
      updateTitle();
   }

   /*
      helper methods
   */

   private void about()
   {
      JPanel aboutPanel = new JPanel();
      aboutPanel.setLayout(new BoxLayout(aboutPanel, BoxLayout.Y_AXIS));
      aboutPanel.add(new JLabel(TITLE + " " + VERSION));
      aboutPanel.add(new JLabel("A flexible, general-purpose schedule manager and generator."));
      aboutPanel.add(new JLabel("Developed by Shazz Amin"));

      JOptionPane.showMessageDialog(this, aboutPanel, "About", JOptionPane.INFORMATION_MESSAGE);
   }

   private void createNew()
   {
      if (hasChanged && !promptToSave()) return;

      day = new ArrayListModel<Day>();
      daysPanel.setAllListData(day);

      period = new ArrayListModel<Period>();
      periodsPanel.setAllListData(period);

      activity = new ArrayListModel<Activity>();
      activitiesPanel.setAllListData(activity);

      person = new ArrayListModel<Person>();
      personsPanel.setAllListData(person);

      currentFile = null;
      setHasChanged(false);
   }

   private void howToUse()
   {
      JPanel howToUsePanel = new JPanel();
      howToUsePanel.setLayout(new BoxLayout(howToUsePanel, BoxLayout.Y_AXIS));

      howToUsePanel.add(new JLabel("One schedule cycle consists of days (in the order that they take place)."));
      howToUsePanel.add(new JLabel("Each day consists of periods which take place on that day (in the order that they take place)."));
      howToUsePanel.add(new JLabel("Each period consists of activities which take place during that period."));
      howToUsePanel.add(new JLabel("Each activity consists of persons who participate in it."));
      howToUsePanel.add(new JLabel("A schedule can be generated for a specific person. It will display what activity they are participating in for every period of every day."));

      JOptionPane.showMessageDialog(MainFrame.this, howToUsePanel, "How to use", JOptionPane.INFORMATION_MESSAGE);
   }

   private JPanel makeDataControlPanel()
   {
      JPanel dataControlPanel = new JPanel();
      dataControlPanel.setLayout(new GridLayout(1, 4));

      daysPanel = new DaysPanel();
      dataControlPanel.add(daysPanel);

      periodsPanel = new PeriodsPanel();
      dataControlPanel.add(periodsPanel);

      activitiesPanel = new ActivitiesPanel();
      dataControlPanel.add(activitiesPanel);

      personsPanel = new PersonsPanel();
      dataControlPanel.add(personsPanel);

      return dataControlPanel;
   }

   private JMenuBar makeMenuBar()
   {
      final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

      JMenuBar menuBar = new JMenuBar();

      JMenu fileMenu = new JMenu("File");
      menuBar.add(fileMenu);

      JMenuItem newItem = new JMenuItem("New" + ELLIPSIS);
      fileMenu.add(newItem);
      newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK));
      newItem.addActionListener(
         new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               createNew();
            }
         }
      );

      JMenuItem openItem = new JMenuItem("Open" + ELLIPSIS);
      fileMenu.add(openItem);
      openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_MASK));
      openItem.addActionListener(
         new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               open();
            }
         }
      );

      JMenuItem saveItem = new JMenuItem("Save");
      fileMenu.add(saveItem);
      saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
      saveItem.addActionListener(
         new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               save();
            }
         }
      );

      JMenuItem saveAsItem = new JMenuItem("Save As" + ELLIPSIS);
      fileMenu.add(saveAsItem);
      saveAsItem.addActionListener(
         new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               saveAs();
            }
         }
      );

      JMenuItem quitItem = new JMenuItem("Quit");
      fileMenu.add(quitItem);
      quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
      quitItem.addActionListener(
         new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               quit();
            }
         }
      );

      JMenu helpMenu = new JMenu("Help");
      menuBar.add(helpMenu);

      JMenuItem howToUseItem = new JMenuItem("How to use" + ELLIPSIS);
      helpMenu.add(howToUseItem);
      howToUseItem.addActionListener(
         new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               howToUse();
            }
         }
      );

      JMenuItem aboutItem = new JMenuItem("About " + TITLE + ELLIPSIS);
      helpMenu.add(aboutItem);
      aboutItem.addActionListener(
         new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               about();
            }
         }
      );

      return menuBar;
   }

   private void open()
   {
      if (hasChanged && !promptToSave()) return;

      File file = fileManager.getOpenFile();
      if (file != null)
      {
         boolean didLoad = fileManager.loadFromFile(file);
         if (didLoad)
         {
            daysPanel.setAllListData(day);
            periodsPanel.setAllListData(period);
            activitiesPanel.setAllListData(activity);
            personsPanel.setAllListData(person);

            currentFile = file;
            setHasChanged(false);
         }
         else
         {
            showError("Failed to load from specified file. It might be corrupted.");
         }
      }
   }

   private boolean promptToSave()
   {
      JPanel savePromptDialogPanel = new JPanel();
      savePromptDialogPanel.setLayout(new GridLayout(2, 1));
      savePromptDialogPanel.add(new JLabel("What would you like to do before exiting?"));
      savePromptDialogPanel.add(new JLabel("Any unsaved changes will be lost."));

      int response = JOptionPane.showOptionDialog(this, savePromptDialogPanel, "Save Changes?", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Save", "Don't Save", "Cancel"}, null);
      if (response == 2) return false;
      if (response == 0) save();
      return true;
   }

   private void quit()
   {
      if (hasChanged && !promptToSave()) return;

      setVisible(false);
      dispose();
      System.exit(0);
   }

   private void save()
   {
      if (currentFile == null)
      {
         saveAs();
      }
      else
      {
         if (fileManager.saveToFile(currentFile)) setHasChanged(false);
         else showError("Failed to save to file.");
      }
   }

   private void saveAs()
   {
      File file = fileManager.getSaveFile();
      if (file != null)
      {
         if (fileManager.saveToFile(file))
         {
            currentFile = file;
            setHasChanged(false);
         }
         else
         {
            showError("Failed to save to file");
         }
      }
   }

   private void showError(String error)
   {
      JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
   }

   private void updateTitle()
   {
      if (currentFile != null) setTitle(TITLE + " - " + currentFile.getName() + (hasChanged ? " *" : "") + " (" + currentFile.getAbsolutePath() + ")");
      else setTitle(TITLE + (hasChanged ? " - *" : ""));
   }

   /*
      inner classes
   */

   private class ActivitiesPanel extends SectionPanel<Activity, Person>
   {
      /*
         constructors
      */

      public ActivitiesPanel()
      {
         super(activity, "Activities", "activity", "period", false, true);
      }

      /*
         helper methods
      */

      protected void addTo(Activity activity)
      {
         periodsPanel.setToAdd(activity);
      }

      protected Activity createNew()
      {
         Activity newActivity = new Activity();
         return edit(newActivity) ? newActivity : null;
      }

      protected void delete(Activity activity)
      {
         for (Period periodElement : period) periodElement.getActivity().remove(activity);
      }

      protected boolean edit(Activity activity)
      {
         JPanel editPanel = new JPanel();
         editPanel.setLayout(new GridLayout(2, 2));

         editPanel.add(new JLabel("Identifier: "));
         JTextField identifierTextField = new JTextField(activity.getIdentifier());
         editPanel.add(identifierTextField);

         editPanel.add(new JLabel("Type: "));
         JTextField typeTextField = new JTextField(activity.getType());
         editPanel.add(typeTextField);

         int response = JOptionPane.showOptionDialog(MainFrame.this, editPanel, "Create/edit activity", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Save", "Cancel"}, null);
         if (response == 1) return false;

         activity.setIdentifier(identifierTextField.getText());
         activity.setType(typeTextField.getText());
         return true;
      }

      protected void listElementSelectEvent(Activity activity, Person toAdd)
      {
         personsPanel.setIsListElementSelectedInPrevious(activity != null);

         if (activity != null)
         {
            if (toAdd != null) activity.getPerson().add(toAdd);
            personsPanel.setFilter(activity.getIdentifier(), activity.getPerson());
         }
         else
         {
            personsPanel.clearFilter();
         }
      }

      protected void removeFromPrevious(Activity activity)
      {
         periodsPanel.getSelectedElement().getActivity().remove(activity);
      }

      protected void swapListElements(int currentIndex, int newIndex) { }
   }

   private class DaysPanel extends SectionPanel<Day, Period>
   {
      /*
         constructors
      */

      public DaysPanel()
      {
         super(day, "Days", "day", null, true, false);
         setIsListElementSelectedInPrevious(true);
      }

      /*
         helper methods
      */

      protected void addTo(Day day) { }

      protected Day createNew()
      {
         Day newDay = new Day();
         return edit(newDay) ? newDay : null;
      }

      protected void delete(Day day) { }

      protected boolean edit(Day day)
      {
         JPanel editPanel = new JPanel();
         editPanel.setLayout(new GridLayout(2, 2));

         editPanel.add(new JLabel("Identifier: "));
         JTextField identifierTextField = new JTextField(day.getIdentifier());
         editPanel.add(identifierTextField);

         editPanel.add(new JLabel("Start time (hh:mm): "));
         JPanel startTimePanel = new JPanel();
         editPanel.add(startTimePanel);

         JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(day.getStartTime().getHour(), 0, 23, 1));
         startTimePanel.add(hourSpinner);

         startTimePanel.add(new JLabel(":"));

         JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(day.getStartTime().getMinute(), 0, 59, 1));
         startTimePanel.add(minuteSpinner);

         int response = JOptionPane.showOptionDialog(MainFrame.this, editPanel, "Create/edit day", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Save", "Cancel"}, null);
         if (response == 1) return false;

         day.setIdentifier(identifierTextField.getText());
         day.setStartTime(LocalTime.of(((SpinnerNumberModel) hourSpinner.getModel()).getNumber().intValue(), ((SpinnerNumberModel) minuteSpinner.getModel()).getNumber().intValue()));
         return true;
      }

      protected void listElementSelectEvent(Day day, Period toAdd)
      {
         periodsPanel.setIsListElementSelectedInPrevious(day != null);

         if (day != null)
         {
            if (toAdd != null) day.getPeriod().add(toAdd);
            periodsPanel.setFilter(day.getIdentifier(), day.getPeriod());
         }
         else
         {
            periodsPanel.clearFilter();
         }
      }

      protected void removeFromPrevious(Day day) { }

      protected void swapListElements(int currentIndex, int newIndex) { }
   }

   private class FileManager
   {
      // instance fields
      private final String FIELDS_SEPARATOR = "|:|";
      private JFileChooser fileChooser;
      private final String LIST_SEPARATOR = ",";
      private MainFrame parent;

      /*
         constructors
      */

      private FileManager()
      {
         fileChooser = new JFileChooser(System.getProperty("user.dir"));
         fileChooser.setFileFilter(new FileNameExtensionFilter(TITLE + " File (." + FILE_FORMAT + ")", FILE_FORMAT));
         parent = MainFrame.this;
      }

      /*
         helper methods
      */

      private File getOpenFile()
      {
         if (fileChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) return null;

         return fileChooser.getSelectedFile();
      }

      private File getSaveFile()
      {
         if (fileChooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return null;

         File file = fileChooser.getSelectedFile();
         String filePath = file.getAbsolutePath();
         if (!filePath.endsWith("." + FILE_FORMAT)) file = new File(filePath + "." + FILE_FORMAT);

         return file;
      }

      private boolean loadFromFile(File file)
      {
         try
         {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            try
            {
               int numberOfDays = Integer.parseInt(reader.readLine());
               day = new ArrayListModel<Day>();
               ArrayList<ArrayList<Integer>> dayPeriodList = new ArrayList<ArrayList<Integer>>();
               for (int i = 0; i < numberOfDays; i++)
               {
                  String[] dayInfo = reader.readLine().split(Pattern.quote(FIELDS_SEPARATOR), -1);
                  day.add(new Day(dayInfo[0], LocalTime.of(Integer.parseInt(dayInfo[1]), Integer.parseInt(dayInfo[2]))));

                  dayPeriodList.add(new ArrayList<Integer>());
                  if (!dayInfo[3].equals(""))
                  {
                     String[] dayPeriodInfo = dayInfo[3].split(Pattern.quote(LIST_SEPARATOR));
                     for (int j = 0; j < dayPeriodInfo.length; j++) dayPeriodList.get(i).add(Integer.valueOf(dayPeriodInfo[j]));
                  }
               }

               int numberOfPeriods = Integer.parseInt(reader.readLine());
               period = new ArrayListModel<Period>();
               ArrayList<ArrayList<Integer>> periodActivityList = new ArrayList<ArrayList<Integer>>();
               for (int i = 0; i < numberOfPeriods; i++)
               {
                  String[] periodInfo = reader.readLine().split(Pattern.quote(FIELDS_SEPARATOR), -1);
                  period.add(new Period(Duration.ofMinutes(Long.parseLong(periodInfo[1])), periodInfo[0]));

                  periodActivityList.add(new ArrayList<Integer>());
                  if (!periodInfo[2].equals(""))
                  {
                     String[] periodActivityInfo = periodInfo[2].split(Pattern.quote(LIST_SEPARATOR));
                     for (int j = 0; j < periodActivityInfo.length; j++) periodActivityList.get(i).add(Integer.valueOf(periodActivityInfo[j]));
                  }
               }

               int numberOfActivities = Integer.parseInt(reader.readLine());
               activity = new ArrayListModel<Activity>();
               ArrayList<ArrayList<Integer>> activityPersonList = new ArrayList<ArrayList<Integer>>();
               for (int i = 0; i < numberOfActivities; i++)
               {
                  String[] activityInfo = reader.readLine().split(Pattern.quote(FIELDS_SEPARATOR), -1);
                  activity.add(new Activity(activityInfo[0], activityInfo[1]));

                  activityPersonList.add(new ArrayList<Integer>());
                  if (!activityInfo[2].equals(""))
                  {
                     String[] activityPersonInfo = activityInfo[2].split(Pattern.quote(LIST_SEPARATOR));
                     for (int j = 0; j < activityPersonInfo.length; j++) activityPersonList.get(i).add(Integer.valueOf(activityPersonInfo[j]));
                  }
               }

               int numberOfPersons = Integer.parseInt(reader.readLine());
               person = new ArrayListModel<Person>();
               for (int i = 0; i < numberOfPersons; i++)
               {
                  String[] personInfo = reader.readLine().split(Pattern.quote(FIELDS_SEPARATOR), -1);
                  person.add(new Person(personInfo[1], personInfo[0], personInfo[2], personInfo[3]));
               }

               for (int i = 0; i < numberOfDays; i++)
               {
                  ArrayListModel<Period> periodList = new ArrayListModel<Period>();
                  for (int j = 0; j < dayPeriodList.get(i).size(); j++) periodList.add(period.get(dayPeriodList.get(i).get(j)));
                  day.get(i).setPeriod(periodList);
               }

               for (int i = 0; i < numberOfPeriods; i++)
               {
                  HashSet<Activity> activitySet = new HashSet<Activity>();
                  for (int j = 0; j < periodActivityList.get(i).size(); j++) activitySet.add(activity.get(periodActivityList.get(i).get(j)));
                  period.get(i).setActivity(activitySet);
               }

               for (int i = 0; i < numberOfActivities; i++)
               {
                  HashSet<Person> personSet = new HashSet<Person>();
                  for (int j = 0; j < activityPersonList.get(i).size(); j++) personSet.add(person.get(activityPersonList.get(i).get(j)));
                  activity.get(i).setPerson(personSet);
               }
            }
            finally
            {
               reader.close();
            }
         }
         catch (Exception exception)
         {
            System.out.println(exception);
            exception.printStackTrace();
            return false;
         }

         return true;
      }

      private boolean saveToFile(File file)
      {
         try
         {
            if (!file.exists()) file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            try
            {
               writer.write(String.valueOf(day.size()));
               writer.newLine();

               for (Day dayElement : day)
               {
                  StringJoiner dayLine = new StringJoiner(FIELDS_SEPARATOR);
                  dayLine.add(dayElement.getIdentifier());
                  dayLine.add(String.valueOf(dayElement.getStartTime().getHour()));
                  dayLine.add(String.valueOf(dayElement.getStartTime().getMinute()));

                  StringJoiner periodList = new StringJoiner(LIST_SEPARATOR);
                  for (Period periodElement : dayElement.getPeriod()) periodList.add(String.valueOf(period.indexOf(periodElement)));

                  dayLine.add(periodList.toString());

                  writer.write(dayLine.toString());
                  writer.newLine();
               }

               writer.write(String.valueOf(period.size()));
               writer.newLine();

               for (Period periodElement : period)
               {
                  StringJoiner periodLine = new StringJoiner(FIELDS_SEPARATOR);
                  periodLine.add(periodElement.getIdentifier());
                  periodLine.add(String.valueOf(periodElement.getDuration().toMinutes()));

                  StringJoiner activityList = new StringJoiner(LIST_SEPARATOR);
                  for (Activity activityElement : periodElement.getActivity()) activityList.add(String.valueOf(activity.indexOf(activityElement)));

                  periodLine.add(activityList.toString());

                  writer.write(periodLine.toString());
                  writer.newLine();
               }

               writer.write(String.valueOf(activity.size()));
               writer.newLine();

               for (Activity activityElement : activity)
               {
                  StringJoiner activityLine = new StringJoiner(FIELDS_SEPARATOR);
                  activityLine.add(activityElement.getIdentifier());
                  activityLine.add(activityElement.getType());

                  StringJoiner personList = new StringJoiner(LIST_SEPARATOR);
                  for (Person personElement : activityElement.getPerson()) personList.add(String.valueOf(person.indexOf(personElement)));

                  activityLine.add(personList.toString());

                  writer.write(activityLine.toString());
                  writer.newLine();
               }

               writer.write(String.valueOf(person.size()));
               writer.newLine();

               for (Person personElement : person)
               {
                  StringJoiner personLine = new StringJoiner(FIELDS_SEPARATOR);
                  personLine.add(personElement.getIdentifier());
                  personLine.add(personElement.getFirstName());
                  personLine.add(personElement.getLastName());
                  personLine.add(personElement.getRole());

                  writer.write(personLine.toString());
                  writer.newLine();
               }
            }
            finally
            {
               writer.close();
            }
         }
         catch (Exception exception)
         {
            return false;
         }

         return true;
      }
   }

   private class PeriodsPanel extends SectionPanel<Period, Activity>
   {
      /*
         constructors
      */

      public PeriodsPanel()
      {
         super(period, "Periods", "period", "day", true, true);
      }

      /*
         helper methods
      */

      protected void addTo(Period period)
      {
         daysPanel.setToAdd(period);
      }

      protected Period createNew()
      {
         Period newPeriod = new Period();
         return edit(newPeriod) ? newPeriod : null;
      }

      protected void delete(Period period)
      {
         for (Day dayElement : day)
         {
            ArrayList<Period> dayElementPeriod = dayElement.getPeriod();
            while (dayElementPeriod.indexOf(period) != -1) dayElementPeriod.remove(period);
         }
      }

      protected boolean edit(Period period)
      {
         JPanel editPanel = new JPanel();
         editPanel.setLayout(new GridLayout(2, 2));

         editPanel.add(new JLabel("Identifier: "));
         JTextField identifierTextField = new JTextField(period.getIdentifier());
         editPanel.add(identifierTextField);

         editPanel.add(new JLabel("Duration: "));
         JPanel durationPanel = new JPanel();
         editPanel.add(durationPanel);

         JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(period.getDuration().toMinutes() / 60, 0, Integer.MAX_VALUE, 1));
         durationPanel.add(hourSpinner);
         durationPanel.add(new JLabel("h"));

         JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(period.getDuration().toMinutes() % 60, 0, 59, 1));
         durationPanel.add(minuteSpinner);
         durationPanel.add(new JLabel("m"));

         int response = JOptionPane.showOptionDialog(MainFrame.this, editPanel, "Create/edit period", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Save", "Cancel"}, null);
         if (response == 1) return false;

         period.setIdentifier(identifierTextField.getText());
         period.setDuration(Duration.ofMinutes(((SpinnerNumberModel) hourSpinner.getModel()).getNumber().longValue() * 60 + ((SpinnerNumberModel) minuteSpinner.getModel()).getNumber().longValue()));
         return true;
      }

      protected void listElementSelectEvent(Period period, Activity toAdd)
      {
         activitiesPanel.setIsListElementSelectedInPrevious(period != null);

         if (period != null)
         {
            if (toAdd != null) period.getActivity().add(toAdd);
            activitiesPanel.setFilter(period.getIdentifier(), period.getActivity());
         }
         else
         {
            activitiesPanel.clearFilter();
         }
      }

      protected void removeFromPrevious(Period period)
      {
         daysPanel.getSelectedElement().getPeriod().remove(period);
      }

      protected void swapListElements(int currentIndex, int newIndex)
      {
         ArrayList<Period> dayPeriod = daysPanel.getSelectedElement().getPeriod();

         Period selectedPeriod = dayPeriod.get(currentIndex);
         dayPeriod.set(currentIndex, dayPeriod.get(newIndex));
         dayPeriod.set(newIndex, selectedPeriod);
      }
   }

   private class PersonsPanel extends SectionPanel<Person, Object>
   {
      /*
         constructors
      */

      public PersonsPanel()
      {
         super(person, "Persons", "person", "activity", false, true);
      }

      /*
         helper methods
      */

      protected void addTo(Person person)
      {
         activitiesPanel.setToAdd(person);
      }

      protected Person createNew()
      {
         Person newPerson = new Person();
         return edit(newPerson) ? newPerson : null;
      }

      protected void delete(Person person)
      {
         for (Activity activityElement : activity) activityElement.getPerson().remove(person);
      }

      protected boolean edit(Person person)
      {
         JPanel editPanel = new JPanel();
         editPanel.setLayout(new GridLayout(4, 2));

         editPanel.add(new JLabel("Identifier: "));
         JTextField identifierTextField = new JTextField(person.getIdentifier());
         editPanel.add(identifierTextField);

         editPanel.add(new JLabel("First name: "));
         JTextField firstNameTextField = new JTextField(person.getFirstName());
         editPanel.add(firstNameTextField);

         editPanel.add(new JLabel("Last name: "));
         JTextField lastNameTextField = new JTextField(person.getLastName());
         editPanel.add(lastNameTextField);

         editPanel.add(new JLabel("Role: "));
         JTextField roleTextField = new JTextField(person.getRole());
         editPanel.add(roleTextField);

         int response = JOptionPane.showOptionDialog(MainFrame.this, editPanel, "Create/edit person", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Save", "Cancel"}, null);
         if (response == 1) return false;

         person.setIdentifier(identifierTextField.getText());
         person.setFirstName(firstNameTextField.getText());
         person.setLastName(lastNameTextField.getText());
         person.setRole(roleTextField.getText());
         return true;
      }

      protected void listElementSelectEvent(Person person, Object toAdd) { }

      protected void removeFromPrevious(Person person)
      {
         activitiesPanel.getSelectedElement().getPerson().remove(person);
      }

      protected void swapListElements(int currentIndex, int newIndex) { }
   }

   private class ScheduleGeneratorPanel extends JPanel
   {
      /*
         constructors
      */

      public ScheduleGeneratorPanel()
      {
         super();

         setLayout(new GridLayout(1, 3));

         JButton generateForPersonButton = new JButton("Generate schedule for selected person");
         add(generateForPersonButton);
         generateForPersonButton.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  generateForPerson(personsPanel.getSelectedElement());
               }
            }
         );
      }

      /*
         helper methods
      */

      private void generateForPerson(Person person)
      {
         if (person == null)
         {
            showError("No person is selected.");
            return;
         }

         JPanel schedulePanel = new JPanel();
         schedulePanel.setLayout(new GridLayout(day.size(), 2, 0, SCHEDULE_PANEL_VERTICAL_GAP));

         for (int i = 0; i < day.size(); i++)
         {
            schedulePanel.add(new JLabel("Day " + day.get(i)));
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));

            LocalTime currentTime = day.get(i).getStartTime();

            for (int j = 0; j < day.get(i).getPeriod().size(); j++)
            {
               LocalTime periodStartTime = currentTime;
               currentTime = currentTime.plusMinutes(day.get(i).getPeriod().get(j).getDuration().toMinutes());

               Activity periodActivity = null;
               for (Activity activityElement : day.get(i).getPeriod().get(j).getActivity())
               {
                  if (activityElement.getPerson().contains(person))
                  {
                     periodActivity = activityElement;
                     break;
                  }
               }
               dayPanel.add(new JLabel("[" + periodStartTime + " -> " + currentTime + "] Period " + day.get(i).getPeriod().get(j).getIdentifier() + ": " + (periodActivity != null ? periodActivity : "")));
            }

            schedulePanel.add(dayPanel);
         }

         JOptionPane.showMessageDialog(MainFrame.this, schedulePanel, person.toString(), JOptionPane.INFORMATION_MESSAGE);
      }
   }

   private abstract class SectionPanel<E, T> extends JPanel
   {
      // instance fields
      private JButton addToButton;
      private ArrayListModel<E> allListData;
      private JButton deleteButton;
      private JButton deselectButton;
      private JButton editButton;
      private boolean isListElementSelectedInPrevious;
      private JList<E> list;
      private ListDataListener listModificationListener;
      private JButton moveDownButton;
      private JButton moveUpButton;
      private JButton newButton;
      private String previousSectionTitle;
      private JButton removeFromPreviousButton;
      private String sectionTitle;
      private String sectionTitleSingular;
      private JLabel statusLabel;
      private T toAdd;

      /*
         constructors
      */

      public SectionPanel(ArrayListModel<E> allListData, String sectionTitle, String sectionTitleSingular, String previousSectionTitle, boolean orderingFunctionality, boolean previousFunctionality)
      {
         super();

         this.isListElementSelectedInPrevious = false;
         this.listModificationListener = new ListDataListener()
         {
            public void intervalAdded(ListDataEvent event)
            {
               setHasChanged(true);
            }

            public void intervalRemoved(ListDataEvent event)
            {
               setHasChanged(true);
            }

            public void contentsChanged(ListDataEvent event)
            {
               setHasChanged(true);
            }
         };
         this.previousSectionTitle = previousSectionTitle;
         this.sectionTitle = sectionTitle;
         this.sectionTitleSingular = sectionTitleSingular;
         this.toAdd = null;

         setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
         setBorder(BorderFactory.createEmptyBorder(SECTION_PANEL_PADDING, SECTION_PANEL_PADDING, SECTION_PANEL_PADDING, SECTION_PANEL_PADDING));
         add(makeTitleLabel());
         add(makeStatusLabel());
         add(makeDeselectPanel());
         add(makeListScrollPane());
         add(makeReorderPanel(orderingFunctionality));
         add(makeModifyPanel());
         add(makePreviousPanel(previousFunctionality));

         setAllListData(allListData);
      }

      /*
         helper methods
      */

      public void clearFilter()
      {
         newButton.setEnabled(true);
         statusLabel.setText("Showing all " + sectionTitle.toLowerCase() + ".");

         list.setModel(allListData);
      }

      public E getSelectedElement()
      {
         return list.getSelectedValue();
      }

      private JPanel makeDeselectPanel()
      {
         JPanel deselectPanel = new JPanel();
         add(deselectPanel);
         deselectPanel.setLayout(new GridLayout(1, 1));

         deselectButton = new JButton("Deselect");
         deselectPanel.add(deselectButton);
         deselectButton.setEnabled(false);
         deselectButton.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  if (list.getSelectedIndex() != -1) list.clearSelection();
                  else setToAdd(null);
               }
            }
         );

         return deselectPanel;
      }

      private JScrollPane makeListScrollPane()
      {
         JScrollPane listScrollPane = new JScrollPane(list);
         add(listScrollPane);

         list = new JList<E>();
         listScrollPane.setViewportView(list);
         list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         list.setVisibleRowCount(SECTION_LIST_ROW_COUNT);
         list.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

         list.addListSelectionListener(
            new ListSelectionListener()
            {
               public void valueChanged(ListSelectionEvent event)
               {
                  if (event.getValueIsAdjusting()) return;

                  int newIndex = list.getSelectedIndex();
                  if (newIndex == -1)
                  {
                     deselectButton.setEnabled(false);

                     moveUpButton.setEnabled(false);
                     moveDownButton.setEnabled(false);

                     editButton.setEnabled(false);
                     deleteButton.setEnabled(false);

                     addToButton.setEnabled(false);
                     removeFromPreviousButton.setEnabled(false);
                  }
                  else
                  {
                     deselectButton.setEnabled(true);

                     if (isListElementSelectedInPrevious) moveUpButton.setEnabled(newIndex == 0 ? false : true);
                     if (isListElementSelectedInPrevious) moveDownButton.setEnabled(newIndex == list.getModel().getSize() - 1 ? false : true);

                     editButton.setEnabled(true);
                     deleteButton.setEnabled(true);

                     addToButton.setEnabled(true);
                     if (isListElementSelectedInPrevious) removeFromPreviousButton.setEnabled(true);
                  }

                  listElementSelectEvent(newIndex == -1 ? null : list.getSelectedValue(), toAdd);
                  setToAdd(null);
               }
            }
         );

         return listScrollPane;
      }

      private JPanel makeModifyPanel()
      {
         JPanel modifyPanel = new JPanel();
         add(modifyPanel);
         modifyPanel.setLayout(new GridLayout(1, 3));

         newButton = new JButton("Create new" + ELLIPSIS);
         modifyPanel.add(newButton);
         newButton.setEnabled(true);
         newButton.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  E element = createNew();
                  if (element != null) allListData.add(element);
               }
            }
         );

         editButton = new JButton("Edit selected" + ELLIPSIS);
         modifyPanel.add(editButton);
         editButton.setEnabled(false);
         editButton.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  if (edit(list.getSelectedValue())) ((ArrayListModel<E>) list.getModel()).fireContentsChangedEvent(list.getSelectedIndex(), list.getSelectedIndex());
               }
            }
         );

         deleteButton = new JButton("Delete selected" + ELLIPSIS);
         modifyPanel.add(deleteButton);
         deleteButton.setEnabled(false);
         deleteButton.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  JPanel deleteDialogPanel = new JPanel();
                  deleteDialogPanel.setLayout(new BoxLayout(deleteDialogPanel, BoxLayout.Y_AXIS));
                  deleteDialogPanel.add(new JLabel("Are you sure you would like to delete the selected " + sectionTitleSingular + "?"));
                  deleteDialogPanel.add(new JLabel("It will be deleted from everywhere permanently and is NOT recoverable!"));
                  int response = JOptionPane.showOptionDialog(MainFrame.this, deleteDialogPanel, "Delete " + sectionTitleSingular + "?", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, null);
                  if (response == 0)
                  {
                     int selectedIndex = list.getSelectedIndex();
                     E element = list.getSelectedValue();

                     ((ArrayListModel<E>) list.getModel()).remove(element);
                     if (selectedIndex < list.getModel().getSize()) list.setSelectedIndex(selectedIndex);
                     else list.setSelectedIndex(selectedIndex - 1);

                     allListData.remove(element);
                     delete(element);
                  }
               }
            }
         );

         return modifyPanel;
      }

      private JPanel makePreviousPanel(boolean previousFunctionality)
      {
         JPanel previousPanel = new JPanel();
         add(previousPanel);
         previousPanel.setLayout(new GridLayout(1, 2));

         addToButton = new JButton("Add to " + previousSectionTitle + ELLIPSIS);
         previousPanel.add(addToButton);
         addToButton.setEnabled(false);
         addToButton.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  JPanel addToPanel = new JPanel();
                  addToPanel.setLayout(new BoxLayout(addToPanel, BoxLayout.Y_AXIS));
                  addToPanel.add(new JLabel("Select the " + previousSectionTitle + " to which this " + sectionTitleSingular + " is to be added."));
                  addToPanel.add(new JLabel("Click the \"Cancel 'add to'\" button to cancel this process."));
                  JOptionPane.showMessageDialog(MainFrame.this, addToPanel, "Select " + previousSectionTitle, JOptionPane.INFORMATION_MESSAGE);
                  addTo(list.getSelectedValue());
               }
            }
         );

         removeFromPreviousButton = new JButton("Remove from selected " + previousSectionTitle);
         previousPanel.add(removeFromPreviousButton);
         removeFromPreviousButton.setEnabled(false);
         removeFromPreviousButton.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  int selectedIndex = list.getSelectedIndex();
                  E element = list.getSelectedValue();

                  ((ArrayListModel<E>) list.getModel()).remove(element);
                  if (selectedIndex < list.getModel().getSize()) list.setSelectedIndex(selectedIndex);
                  else list.setSelectedIndex(selectedIndex - 1);

                  removeFromPrevious(element);
               }
            }
         );

         if (!previousFunctionality)
         {
            addToButton.setVisible(false);
            removeFromPreviousButton.setVisible(false);
         }

         return previousPanel;
      }

      private JPanel makeReorderPanel(boolean orderingFunctionality)
      {
         JPanel reorderPanel = new JPanel();
         add(reorderPanel);
         reorderPanel.setLayout(new GridLayout(1, 2));

         moveUpButton = new JButton("Move up");
         reorderPanel.add(moveUpButton);
         moveUpButton.setEnabled(false);
         moveUpButton.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  swap(list.getSelectedIndex(), list.getSelectedIndex() - 1);
               }
            }
         );

         moveDownButton = new JButton("Move down");
         reorderPanel.add(moveDownButton);
         moveDownButton.setEnabled(false);
         moveDownButton.addActionListener(
            new ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  swap(list.getSelectedIndex(), list.getSelectedIndex() + 1);
               }
            }
         );

         if (!orderingFunctionality)
         {
            moveUpButton.setVisible(false);
            moveDownButton.setVisible(false);
         }

         return reorderPanel;
      }

      private JLabel makeStatusLabel()
      {
         statusLabel = new JLabel();
         add(statusLabel);
         statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

         return statusLabel;
      }

      private JLabel makeTitleLabel()
      {
         JLabel titleLabel = new JLabel(sectionTitle);
         titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         titleLabel.setFont(SECTION_TITLE_TEXT_FONT);

         return titleLabel;
      }

      public void setAllListData(ArrayListModel<E> allListData)
      {
         if (allListData == null) return;

         this.allListData = allListData;
         clearFilter();

         allListData.addListDataListener(listModificationListener);
      }

      private void setFilter(String filter, ArrayListModel<E> filteredListData)
      {
         if (filter == null || filteredListData == null) return;

         newButton.setEnabled(false);
         statusLabel.setText("Showing " + sectionTitle.toLowerCase() + " in " + previousSectionTitle + " " + filter);

         list.setModel(filteredListData);

         filteredListData.addListDataListener(listModificationListener);
      }

      public void setFilter(String filter, HashSet<E> filteredListData)
      {
         setFilter(filter, new ArrayListModel<E>(filteredListData));
      }

      public void setFilter(String filter, ArrayList<E> filteredListData)
      {
         setFilter(filter, new ArrayListModel<E>(filteredListData));
      }

      public void setIsListElementSelectedInPrevious(boolean isListElementSelectedInPrevious)
      {
         this.isListElementSelectedInPrevious = isListElementSelectedInPrevious;
         if (isListElementSelectedInPrevious && list.getSelectedIndex() != -1)
         {
            moveUpButton.setEnabled(true);
            moveDownButton.setEnabled(true);
            removeFromPreviousButton.setEnabled(true);
         }
         else if (list.getSelectedIndex() == -1)
         {
            moveUpButton.setEnabled(false);
            moveDownButton.setEnabled(false);
            removeFromPreviousButton.setEnabled(false);
         }
         else
         {
            moveUpButton.setEnabled(false);
            moveDownButton.setEnabled(false);
            removeFromPreviousButton.setEnabled(false);
         }
      }

      public void setToAdd(T toAdd)
      {
         if (toAdd != null)
         {
            deselectButton.setText("Cancel 'add to'");
            deselectButton.setEnabled(true);
         }
         else
         {
            deselectButton.setText("Deselect");
         }

         this.toAdd = toAdd;
      }

      private void swap(int currentIndex, int newIndex)
      {
         ArrayListModel<E> listData = (ArrayListModel<E>) list.getModel();

         E selectedElement = listData.get(currentIndex);
         listData.set(currentIndex, listData.get(newIndex));
         listData.set(newIndex, selectedElement);

         swapListElements(currentIndex, newIndex);

         list.setSelectedIndex(newIndex);
      }

      /*
         abstract methods
      */

      abstract protected void addTo(E element);
      abstract protected E createNew();
      abstract protected void delete(E element);
      abstract protected boolean edit(E element);
      abstract protected void listElementSelectEvent(E element, T toAdd);
      abstract protected void removeFromPrevious(E element);
      abstract protected void swapListElements(int currentIndex, int newIndex);
   }

   /*
      main method
   */

   /**
    * Creates the graphical user interface.
    *
    * @param argument not used
    */
   public static void main(String[] argument)
   {
      new MainFrame();
   }
}
