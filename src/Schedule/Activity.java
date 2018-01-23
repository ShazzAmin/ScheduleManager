package Schedule;

import java.util.HashSet;

/**
 * An activity which knows its type, identifier and the <code>Person</code>s participating in it.
 *
 * @author Shazz Amin
 * @version 1.0 2017-01-31
 */
public class Activity
{
   // instance fields
   private String identifier;
   private HashSet<Person> person;
   private String type;

   /*
      constructors
   */

   /**
    * Constructs an activity with default characteristics and no <code>Person</code>s.
    */
   public Activity()
   {
      this(null, null, null);
   }

   /**
    * Constructs an activity with the specified characteristics and no <code>Person</code>s.
    *
    * @param identifier the identifier for this activity<br><i>pre-condition:</i> cannot be <code>null</code>
    * @param type the type of this activity<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public Activity(String identifier, String type)
   {
      this(identifier, null, type);
   }

   /**
    * Constructs an activity with the specified characteristics and set of <code>Person</code>s.
    *
    * @param identifier the identifier for this activity<br><i>pre-condition:</i> cannot be <code>null</code>
    * @param person the set of <code>Person</code>s participating in this activity<br><i>pre-condition:</i> cannot be <code>null</code>
    * @param type the type for this activity<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public Activity(String identifier, HashSet<Person> person, String type)
   {
      if (identifier != null) this.identifier = identifier;
      else this.identifier = "";

      if (person != null) this.person = person;
      else this.person = new HashSet<Person>();

      if (type != null) this.type = type;
      else this.type = "";
   }

   /*
      accessors
   */

   /**
    * Returns the identifier for this activity.
    *
    * @return the identifier for this activity
    */
   public String getIdentifier()
   {
      return identifier;
   }

   /**
    * Returns the set of <code>Person</code>s participating in this activity.
    *
    * @return the set of <code>Person</code>s participating in this activity
    */
   public HashSet<Person> getPerson()
   {
      return person;
   }

   /**
    * Returns the type of this activity.
    *
    * @return the type of this activity
    */
   public String getType()
   {
      return type;
   }

   /**
    * Returns a string representation of this activity.
    *
    * @return a string representation of this activity
    */
   public String toString()
   {
      return "[" + type + "] " + identifier;
   }

   /*
      mutators
   */

   /**
    * Sets the identifier for this activity.
    *
    * @param identifier the new identifier for this activity<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setIdentifier(String identifier)
   {
      if (identifier == null) return;

      this.identifier = identifier;
   }

   /**
    * Sets the set of <code>Person</code>s participating in this activity.
    *
    * @param person the new set of <code>Person</code>s participating in this activity<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setPerson(HashSet<Person> person)
   {
      if (person == null) return;

      this.person = person;
   }

   /**
    * Sets the type of this activity.
    *
    * @param type the new type of this activity<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setType(String type)
   {
      if (type == null) return;

      this.type = type;
   }
}
