package name.ulbricht.javafx.gallery.pages.data;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Random;

/**
 * A person with JavaFX properties, used as sample row type by the table and list demos.
 */
public class Person {

    /** The departments used by the generated sample persons. */
    public static final List<String> DEPARTMENTS = List.of(
            "Development", "Design", "Marketing", "Sales", "Support", "Finance", "Human Resources");

    private static final String[] FIRST_NAMES = {
            "Emma", "Liam", "Olivia", "Noah", "Ava", "Elijah", "Sophia", "James", "Isabella", "Lucas",
            "Mia", "Mason", "Amelia", "Ethan", "Harper", "Logan", "Evelyn", "Oliver", "Abigail", "Jacob",
            "Emily", "Aiden", "Ella", "Henry", "Grace", "Leo", "Chloe", "Jack", "Lily", "Owen"};

    private static final String[] LAST_NAMES = {
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Martinez", "Wilson",
            "Anderson", "Taylor", "Thomas", "Moore", "Jackson", "Martin", "Lee", "Thompson", "White", "Harris",
            "Clark", "Lewis", "Walker", "Hall", "Young", "King", "Wright", "Scott", "Green", "Baker"};

    private final StringProperty firstName = new SimpleStringProperty(this, "firstName");
    private final StringProperty lastName = new SimpleStringProperty(this, "lastName");
    private final StringProperty email = new SimpleStringProperty(this, "email");
    private final StringProperty department = new SimpleStringProperty(this, "department");
    private final IntegerProperty age = new SimpleIntegerProperty(this, "age");
    private final BooleanProperty active = new SimpleBooleanProperty(this, "active");

    public Person(String firstName, String lastName, String email, String department, int age, boolean active) {
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setDepartment(department);
        setAge(age);
        setActive(active);
    }

    /**
     * Creates a reproducible list of sample persons.
     *
     * @param count number of persons
     * @return a new observable list
     */
    public static ObservableList<Person> createSamples(int count) {
        Random random = new Random(42);
        ObservableList<Person> persons = FXCollections.observableArrayList();
        for (int i = 0; i < count; i++) {
            String first = FIRST_NAMES[(i * 7 + random.nextInt(3)) % FIRST_NAMES.length];
            String last = LAST_NAMES[(i * 11 + random.nextInt(5)) % LAST_NAMES.length];
            String mail = (first + "." + last).toLowerCase() + "@example.com";
            String dept = DEPARTMENTS.get(random.nextInt(DEPARTMENTS.size()));
            persons.add(new Person(first, last, mail, dept, 20 + random.nextInt(45), random.nextInt(4) != 0));
        }
        return persons;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public String getInitials() {
        String f = getFirstName();
        String l = getLastName();
        return (f == null || f.isEmpty() ? "" : f.substring(0, 1)) + (l == null || l.isEmpty() ? "" : l.substring(0, 1));
    }

    public final StringProperty firstNameProperty() {
        return firstName;
    }

    public final String getFirstName() {
        return firstName.get();
    }

    public final void setFirstName(String value) {
        firstName.set(value);
    }

    public final StringProperty lastNameProperty() {
        return lastName;
    }

    public final String getLastName() {
        return lastName.get();
    }

    public final void setLastName(String value) {
        lastName.set(value);
    }

    public final StringProperty emailProperty() {
        return email;
    }

    public final String getEmail() {
        return email.get();
    }

    public final void setEmail(String value) {
        email.set(value);
    }

    public final StringProperty departmentProperty() {
        return department;
    }

    public final String getDepartment() {
        return department.get();
    }

    public final void setDepartment(String value) {
        department.set(value);
    }

    public final IntegerProperty ageProperty() {
        return age;
    }

    public final int getAge() {
        return age.get();
    }

    public final void setAge(int value) {
        age.set(value);
    }

    public final BooleanProperty activeProperty() {
        return active;
    }

    public final boolean isActive() {
        return active.get();
    }

    public final void setActive(boolean value) {
        active.set(value);
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
