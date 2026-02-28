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
        if (event instanceof Workshop) {
            IO.println("Enter topic:");
            ((Workshop) event).topic = sc.next();
        } else if (event instanceof Seminar) {
            IO.println("Enter speaker name:");
            ((Seminar) event).speakerName = sc.next();
        } else if (event instanceof Concert) {
            IO.println("Enter age restriction:");
            ((Concert) event).ageRestrictment = sc.nextInt();
        }

        IO.println("Enter status (true/false):");

        event.status = sc.nextBoolean();

        return event;
    }

    //This is basically the same as the addEvent function
    Event updateEvent(Event event) {
        Scanner sc = new Scanner(System.in);

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
        if (event.capacity <= 0) {
            System.exit(0);
        }
        // Specific fields
        if (event instanceof Workshop) {
            IO.println("Enter topic:");
            ((Workshop) event).topic = sc.next();
        } else if (event instanceof Seminar) {
            IO.println("Enter speaker name:");
            ((Seminar) event).speakerName = sc.next();
        } else if (event instanceof Concert) {
            IO.println("Enter age restriction:");
            ((Concert) event).ageRestrictment = sc.nextInt();
        }

        IO.println("Enter status (true/false):");

        event.status = sc.nextBoolean();

        return event;
    }

    //Sets event status to false
    void cancel(Event event) {
        event.status = false;
    }

    //Prints all info of all events
//This isn't formatted nicely yet
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

        events.add(addEvent(new Workshop()));

        searchEvents(events);

        //Sets the event at some position in the array (0 in this case) to new variables
        events.set(0, updateEvent(events.get(0)));

        cancel(events.get(0));

        listEvents(events);

    }
}