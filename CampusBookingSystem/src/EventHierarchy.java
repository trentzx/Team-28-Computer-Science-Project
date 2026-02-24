static class Event {
    int eventId;
    String title;
    int dateTime;
    String location;
    int capacity; //Must be >0
    Boolean status;
}

static class Workshop extends Event {
    String topic;
}

static class Seminar extends Event {
    String speakerName;
}

static class Concert extends Event {
    int ageRestrictment;
}

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

void cancel(Event event) {
    event.status = false;
}

void listEvents(ArrayList<Event> events) {
    for (Event e : events) {
        IO.println(e.capacity);
        IO.println(e.eventId);
        IO.println(e.title);
        IO.println(e.dateTime);
        IO.println(e.location);
        if (e instanceof Workshop) IO.println(((Workshop) e).topic);
        if (e instanceof Seminar) IO.println(((Seminar) e).speakerName);
        if (e instanceof Concert) IO.println(((Concert) e).ageRestrictment);
    }
}

void main() {
    ArrayList<Event> events = new ArrayList<>();

    events.add(addEvent(new Workshop()));

    events.set(0, updateEvent(events.get(0)));

    cancel(events.get(0));

    listEvents(events);

}