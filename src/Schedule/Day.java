package Schedule;

import java.time.LocalTime;
import java.util.ArrayList;

/**
 * A day which knows its identifier, the time at which it starts and the <code>Period</code>s taking place on it (in the order that they take place).
 *
 * @author Shazz Amin
 * @version 1.0 2017-02-06
 */
public class Day
{
   // class variables
   /*
    * Default time at which a day starts.
    */
   public static final LocalTime DEFAULT_START_TIME = LocalTime.MIN;

   // instance fields
   private String identifier;
   private ArrayList<Period> period;
   private LocalTime startTime;

   /*
      constructors
   */

   /**
    * Constructs a day with default characteristics and no <code>Period</code>s.
    */
   public Day()
   {
      this("", new ArrayList<Period>(), DEFAULT_START_TIME);
   }

   /**
    * Constructs a day with the specified characteristics and no <code>Period</code>s.
    *
    * @param identifier the identifier for this day<br><i>pre-condition:</i> cannot be <code>null</code>
    * @param startTime the time at which this day starts<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public Day(String identifier, LocalTime startTime)
   {
      this(identifier, new ArrayList<Period>(), startTime);
   }

   /**
    * Constructs a day with the specified characteristics and set of <code>Period</code>s.
    *
    * @param identifier the identifier for this day<br><i>pre-condition:</i> cannot be <code>null</code>
    * @param period the set of <code>Period</code>s taking place on this day (in the order that they take place)<br><i>pre-condition:</i> cannot be <code>null</code>
    * @param startTime the time at which this day starts<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public Day(String identifier, ArrayList<Period> period, LocalTime startTime)
   {
      if (identifier != null) this.identifier = identifier;
      else this.identifier = "";

      if (period != null) this.period = period;
      else this.period = new ArrayList<Period>();

      if (startTime != null) this.startTime = startTime;
      else this.startTime = DEFAULT_START_TIME;
   }

   /*
      accessors
   */

   /**
    * Returns the identifier for this day.
    *
    * @return the identifier for this day
    */
   public String getIdentifier()
   {
      return identifier;
   }

   /**
    * Returns the set of <code>Period</code>s taking place on this day.
    *
    * @return the set of <code>Period</code>s taking place on this day (in the order that they take place)
    */
   public ArrayList<Period> getPeriod()
   {
      return period;
   }

   /**
    * Returns the time at which this day starts.
    *
    * @return the time at which this day starts
    */
   public LocalTime getStartTime()
   {
      return startTime;
   }

   /**
    * Returns a string representation of this day.
    *
    * @return a string representation of this day
    */
   public String toString()
   {
      return "[" + startTime + "] " + identifier;
   }

   /*
      mutators
   */

   /**
    * Sets the identifier for this day.
    *
    * @param identifier the new identifier for this day<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setIdentifier(String identifier)
   {
      if (identifier == null) return;

      this.identifier = identifier;
   }

   /**
    * Sets the set of <code>Period</code>s taking place in this day.
    *
    * @param period the new set of <code>Period</code>s taking place on this day (in the order that they take place)<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setPeriod(ArrayList<Period> period)
   {
      if (period == null) return;

      this.period = period;
   }

   /**
    * Sets the time at which this day starts.
    *
    * @param startTime the new time at which this day starts<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setStartTime(LocalTime startTime)
   {
      if (startTime == null) return;

      this.startTime = startTime;
   }
}
