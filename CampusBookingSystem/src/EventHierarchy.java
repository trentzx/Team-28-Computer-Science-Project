import java.util.*;

public class EventHierarchy {

    // Make the default event class that include the universal event info
    static class Event {
        private int eventId;
        private String title;
        private int dateTime;
        private String location;
        private int capacity; //Must be >0
        private boolean status;

        //Constructor
        public Event(){}

        //Getters and Setters
        public int  getEventId() {return eventId;}
        public void setEventId(int eventId) {this.eventId = eventId;}

        public String getTitle() {return title;}
        public void setTitle(String title) {this.title = title;}

        public int getDateTime() {return dateTime;}
        public void setDateTime(int dateTime) {this.dateTime = dateTime;}

        public String getLocation() {return location;}
        public void setLocation(String location) {this.location = location;}

        public int getCapacity() {return capacity;}
        public void setCapacity(int capacity) {
            if(capacity <= 0){System.exit(0);}
            this.capacity = capacity;
        }

        public boolean getStatus() {return status;}
        public void setStatus(boolean status) {this.status = status;}
    }

    //Made children classes that use the Event class's variables, but also add their own
    static class Workshop extends Event {
        private String topic;

        //Getters and Setters
        public String getTopic() {return topic;}
        public void setTopic(String topic) {this.topic = topic;}
    }

    static class Seminar extends Event {
        private String speakerName;

        //Getters and Setters
        public String getSpeakerName() {return speakerName;}
        public void setSpeakerName(String speakerName) {this.speakerName = speakerName;}
    }

    static class Concert extends Event {
        private int ageRestrictment;

        //Getters and Setters
        public int getAgeRestrictment() {return ageRestrictment;}
        public void setAgeRestrictment(int ageRestrictment) {this.ageRestrictment = ageRestrictment;}
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
        event.setEventId(sc.nextInt());
        IO.println("Enter title:");
        event.setTitle(sc.next());
        IO.println("Enter date/time:");
        event.setDateTime(sc.nextInt());
        IO.println("Enter location:");
        event.setLocation(sc.next());
        IO.println("Enter capacity:");
        event.setCapacity(sc.nextInt());
        // Specific fields
        switch (event) {
            case Workshop workshop -> {
                IO.println("Enter topic:");
                workshop.setTopic(sc.next());
            }
            case Seminar seminar -> {
                IO.println("Enter speaker name:");
                seminar.setSpeakerName(sc.next());
            }
            case Concert concert -> {
                IO.println("Enter age restriction:");
                concert.setAgeRestrictment(sc.nextInt());
            }
            //This needs to be here to fill all possible cases, even though it will never happen
            default -> {
            }
        }

        IO.println("Enter status (true/false):");
        event.setStatus(sc.nextBoolean());

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
                event.setEventId(sc.nextInt());
            }
            case "Title" -> {
                IO.println("Enter title:");
                event.setTitle(sc.next());
            }
            case "Date" -> {
                IO.println("Enter date/time:");
                event.setDateTime(sc.nextInt());
            }
            case "Location" -> {
                IO.println("Enter location:");
                event.setLocation(sc.next());
            }
            case "Capacity" -> {
                IO.println("Enter capacity:");
                event.setCapacity(sc.nextInt());
            }
            case "Topic", "Speaker", "Age" -> {
                // Specific fields
                switch (event) {
                    case Workshop workshop -> {
                        IO.println("Enter topic:");
                        workshop.setTopic(sc.next());
                    }
                    case Seminar seminar -> {
                        IO.println("Enter speaker name:");
                        seminar.setSpeakerName(sc.next());
                    }
                    case Concert concert -> {
                        IO.println("Enter age restriction:");
                        concert.setAgeRestrictment(sc.nextInt());
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
        event.setStatus(false);
    }

    //Prints all info of all events
//This isn't formatted nicely
    void listEvents(ArrayList<Event> events) {
        for (Event e : events) {
            IO.println(e.getEventId());
            IO.println(e.getTitle());
            IO.println(e.getCapacity());
            IO.println(e.getDateTime());
            IO.println(e.getLocation());
            //For specific instances of the Event class
            if (e instanceof Workshop) IO.println(((Workshop) e).getTopic());
            if (e instanceof Seminar) IO.println(((Seminar) e).getSpeakerName());
            if (e instanceof Concert) IO.println(((Concert) e).getAgeRestrictment());
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
            if (name.equals(e.getTitle())) {
                System.out.println("Event found -- ID: " + e.getEventId());
                break;
            }
            check++;
            //If it does not exist in the array
            if (check == events.size()) {
                System.out.println("Event not found");
            }
        }
    }

    public static void main(String[] args) {
        new EventHierarchy().run();
    }
    void run(){
        ArrayList<Event> events = new ArrayList<>();

        events.add(addEvent(new Event()));

        events.add(addEvent(new Event()));

        searchEvents(events);

        //Sets the event at some position in the array (0 in this case) to new variables
        events.set(0, updateEvent(events.getFirst()));

        searchEvents(events);

        cancel(events.getFirst());

        listEvents(events);
    }
}