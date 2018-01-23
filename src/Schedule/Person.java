package Schedule;

/**
 * A person which knows its first name, last name, role and an identifier.
 *
 * @author Shazz Amin
 * @version 1.0 2017-01-18
 */
public class Person
{
   // instance fields
   private String firstName;
   private String identifier;
   private String lastName;
   private String role;

   /*
      constructors
   */

   /**
    * Constructs a person with default characteristics.
    */
   public Person()
   {
      this(null, null, null, null);
   }

   /**
    * Constructs a person with the specified characteristics.
    *
    * @param firstName the first name of this person<br><i>pre-condition:</i> cannot be <code>null</code>
    * @param identifier the identifier for this person<br><i>pre-condition:</i> cannot be <code>null</code>
    * @param lastName the last name of this person<br><i>pre-condition:</i> cannot be <code>null</code>
    * @param role the role of this person<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public Person(String firstName, String identifier, String lastName, String role)
   {
      if (firstName != null) this.firstName = firstName;
      else this.firstName = "";

      if (identifier != null) this.identifier = identifier;
      else this.identifier = "";

      if (lastName != null) this.lastName = lastName;
      else this.lastName = "";

      if (role != null) this.role = role;
      else this.role = "";
   }

   /*
      accessors
   */

   /**
    * Returns the first name of this person.
    *
    * @return the first name of this person
    */
   public String getFirstName()
   {
      return firstName;
   }

   /**
    * Returns the identifier for this person.
    *
    * @return the identifier for this person
    */
   public String getIdentifier()
   {
      return identifier;
   }

   /**
    * Returns the last name of this person.
    *
    * @return the last name of this person
    */
   public String getLastName()
   {
      return lastName;
   }

   /**
    * Returns the role of this person.
    *
    * @return the role of this person
    */
   public String getRole()
   {
      return role;
   }

   /**
    * Returns a string representation of this person.
    *
    * @return a string representation of this person
    */
   public String toString()
   {
      return "[" + role + "] (" + identifier + ") " + firstName + " " + lastName;
   }

   /*
      mutators
   */

   /**
    * Sets the first name of this person.
    *
    * @param firstName the new first name of this person<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setFirstName(String firstName)
   {
      if (firstName == null) return;

      this.firstName = firstName;
   }

   /**
    * Sets the identifier for this person.
    *
    * @param identifier the new identifier for this person<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setIdentifier(String identifier)
   {
      if (identifier == null) return;

      this.identifier = identifier;
   }

   /**
    * Sets the last name of this person.
    *
    * @param lastName the new last name of this person<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setLastName(String lastName)
   {
      if (lastName == null) return;

      this.lastName = lastName;
   }

   /**
    * Sets the role of this person.
    *
    * @param role the new role of this person<br><i>pre-condition:</i> cannot be <code>null</code>
    */
   public void setRole(String role)
   {
      if (role == null) return;

      this.role = role;
   }
}
