import java.util.*;

public class EventHierarchy {

    // Make the default event class that include the universal event info
    static class Event {
        int eventId;
        String title;
        int dateTime;
        String location;
        int capacity; //Must be >0
        Boolean status;
    }

    //Made children classes that use the Event class's variables, but also add their own
    static class Workshop extends Event {
        String topic;
    }

    static class Seminar extends Event {
        String speakerName;
    }

    static class Concert extends Event {
        int ageRestrictment;
    }

    //Made a function to add an event
    Event addEvent(Event event) {
        Scanner sc = new Scanner(System.in);
        String type;

        IO.println("What type of event is it?");
        type = sc.nextLine();
        switch (type) {
            case "Workshop" -> event = new Workshop();
            case "Seminar" -> event = new Seminar();
            case "Concert" -> event = new Concert();
        }

        IO.println("Enter ID:");
        event.eventId = sc.nextInt();
        IO.println("Enter title:");
        event.title = sc.next();
        IO.println("Enter date/time:");
        event.dateTime = sc.nextInt();
        IO.println("Enter location:");
        event.location = sc.next();
        IO.println("Enter capacity:");
        event.capacity = sc.nextInt();
        //If capacity is not valid
        if (event.capacity <= 0) {
            System.exit(0); //Temporarily exits the program
        }
        // Specific fields
        switch (event) {
            case Workshop workshop -> {
                IO.println("Enter topic:");
                workshop.topic = sc.next();
            }
            case Seminar seminar -> {
                IO.println("Enter speaker name:");
                seminar.speakerName = sc.next();
            }
            case Concert concert -> {
                IO.println("Enter age restriction:");
                concert.ageRestrictment = sc.nextInt();
            }
            //This needs to be here to fill all possible cases, even though it will never happen
            default -> {
            }
        }

        IO.println("Enter status (true/false):");

        event.status = sc.nextBoolean();

        return event;
    }

    //This is basically the same as the addEvent function
    Event updateEvent(Event event) {
        Scanner sc = new Scanner(System.in);
        String field;

        IO.println("What field would you like to update?");
        field = sc.next();
        //All the cases for changing each individual field
        switch (field) {
            case "ID" -> {
                IO.println("Enter ID:");
                event.eventId = sc.nextInt();
            }
            case "Title" -> {
                IO.println("Enter title:");
                event.title = sc.next();
            }
            case "Date" -> {
                IO.println("Enter date/time:");
                event.dateTime = sc.nextInt();
            }
            case "Location" -> {
                IO.println("Enter location:");
                event.location = sc.next();
            }
            case "Capacity" -> {
                IO.println("Enter capacity:");
                event.capacity = sc.nextInt();
                if (event.capacity <= 0) {
                    System.exit(0);
                }
            }
            case "Topic", "Speaker", "Age" -> {
                // Specific fields
                switch (event) {
                    case Workshop workshop -> {
                        IO.println("Enter topic:");
                        workshop.topic = sc.next();
                    }
                    case Seminar seminar -> {
                        IO.println("Enter speaker name:");
                        seminar.speakerName = sc.next();
                    }
                    case Concert concert -> {
                        IO.println("Enter age restriction:");
                        concert.ageRestrictment = sc.nextInt();
                    }
                    default -> {
                    }
                }
            }
        }
        IO.println("Would you like to change another field?");
        String check = sc.next();
        if(check.equals("Yes")) {updateEvent(event);}

        return event;
    }

    //Sets event status to false
    void cancel(Event event) {
        event.status = false;
    }

    //Prints all info of all events
//This isn't formatted nicely
    void listEvents(ArrayList<Event> events) {
        for (Event e : events) {
            IO.println(e.capacity);
            IO.println(e.eventId);
            IO.println(e.title);
            IO.println(e.dateTime);
            IO.println(e.location);
            //For specific instances of the Event class
            if (e instanceof Workshop) IO.println(((Workshop) e).topic);
            if (e instanceof Seminar) IO.println(((Seminar) e).speakerName);
            if (e instanceof Concert) IO.println(((Concert) e).ageRestrictment);
        }
    }

    //Function to search events using the title
    void searchEvents(ArrayList<Event> events) {
        Scanner sc = new Scanner(System.in);
        String name;
        int check = 0;

        System.out.println("What event are you looking for? ");
        name = sc.nextLine();

        //Looks through all events
        for (Event e : events) {
            //check each event in the array to see if the named on is there
            if (name.equals(e.title)) {
                System.out.println("Event found -- ID: " + e.eventId);
                break;
            }
            check++;
            //If it does not exist in the array
            if (check == events.size()) {
                System.out.println("Event not found");
            }
        }
    }

    public void main(String[] args) {
        ArrayList<Event> events = new ArrayList<>();

        events.add(addEvent(new Event()));

        searchEvents(events);

        //Sets the event at some position in the array (0 in this case) to new variables
        events.set(0, updateEvent(events.get(0)));

        searchEvents(events);

        cancel(events.get(0));

        listEvents(events);

    }
}