package Schedule;

import java.time.Duration;
import java.util.HashSet;

/**
 * A period which knows its duration, identifier and the <code>Activity</code>-ies taking place during it.
 *
 * @author Shazz Amin
 * @version 1.0 2017-01-18
 */
public class Period
{
   // class variables
   /*
    * Default duration a period lasts for.
    */
   public static final Duration DEFAULT_DURATION = Duration.ZERO;

   private static final int MINUTES_PER_HOUR = 60;

   // instance fields
   private HashSet<Activity> activity;
   private Duration duration;
   private String identifier;

   /*
      constructors
   */

   /**
    * Constructs a period with default characteristics and no <code>Activity</code>-ies.
    */
   public Period()
   {
      this(null, null, null);
   }

   /**
    * Constructs a period with the specified characteristics and no <code>Activity</code>-ies.
    *
    * @param duration the duration this period lasts for<br><i>pre-condition:</i> cannot be <code>null</code>
    * @param identifier the identifier for this period<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public Period(Duration duration, String identifier)
   {
      this(null, duration, identifier);
   }

   /**
    * Constructs a period with the specified characteristics and set of <code>Activity</code>-ies.
    *
    * @param activity the set of <code>Activity</code>-ies taking place during this period<br><i>pre-condition:</i> cannot be <code>null</code>
    * @param duration the duration this period lasts for<br><i>pre-condition:</i> cannot be <code>null</code>
    * @param identifier the identifier for this period<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public Period(HashSet<Activity> activity, Duration duration, String identifier)
   {
      if (activity != null) this.activity = activity;
      else this.activity = new HashSet<Activity>();

      if (duration != null) this.duration = duration;
      else this.duration = DEFAULT_DURATION;

      if (identifier != null) this.identifier = identifier;
      else this.identifier = "";
   }

   /*
      accessors
   */

   /**
    * Returns the set of <code>Activity</code>-ies taking place during this period.
    *
    * @return the set of <code>Activity</code>-ies taking place during this period
    */
   public HashSet<Activity> getActivity()
   {
      return activity;
   }

   /**
    * Returns the duration this period lasts for.
    *
    * @return the duration this period lasts for.
    */
   public Duration getDuration()
   {
      return duration;
   }

   /**
    * Returns the identifier for this period.
    *
    * @return the identifier for this period.
    */
   public String getIdentifier()
   {
      return identifier;
   }

   /**
    * Returns a string representation of this period.
    *
    * @return a string representation of this period
    */
   public String toString()
   {
      return "[" + String.format("%02d", duration.toMinutes() / MINUTES_PER_HOUR) + "h" + String.format("%02d", duration.toMinutes() % MINUTES_PER_HOUR) + "m] " + identifier;
   }

   /*
      mutators
   */

   /**
    * Sets the set of <code>Activity</code>-ies taking place during this period.
    *
    * @param activity the new set of <code>Activity</code>-ies taking place during this period<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setActivity(HashSet<Activity> activity)
   {
      if (activity == null) return;

      this.activity = activity;
   }

   /**
    * Sets the duration this period lasts for.
    *
    * @param duration the new duration this period lasts for<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setDuration(Duration duration)
   {
      if (duration == null) return;

      this.duration = duration;
   }

   /**
    * Sets the identifier for this period.
    *
    * @param identifier the new identifier for this period<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setIdentifier(String identifier)
   {
      if (identifier == null) return;

      this.identifier = identifier;
   }
}
