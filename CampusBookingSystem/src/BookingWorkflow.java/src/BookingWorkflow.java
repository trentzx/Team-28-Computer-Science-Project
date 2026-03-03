public class BookingWorkflow {

    public void addSeat(User user, Event event) {
        String name = user.getBookingId();

//check for the user if they are confirmed or waitlist
        if (event.isConfirmed(name) || event.isWaitlisted(name)) {
            throw new IllegalStateException("User already has a seat or waitlist spot for this event.");
        }

        //  Checking if there is space, space --> seat, no space = no seat
        if (event.hasSpace()) {
            event.addConfirmedName(name);
        } else {
            event.addToWaitlist(name);
        }
    }
    //cancellation
    public void cancelSeat(User user, Event event) {
        String name = user.getBookingId();

        //  user had a confirmed seat
        if (event.isConfirmed(name)) {
            // remove  seat
            event.removeConfirmedName(name);

            // since a seat opened up, promote next person on waitlist (if any)
            promoteNextIfPossible(event);
            return;
        }

        //  user was on waitlist
        if (event.isWaitlisted(name)) {
            // remove them from the waitlist
            event.removeFromWaitlist(name);
            return;
        }

        //  user has no seat and wasn't waitlisted
        throw new IllegalStateException("User has no seat/waitlist spot for this event.");
    }
    // getting people up waitlist
    private void promoteNextIfPossible(Event event) {

        if (!event.hasSpace()) return;

        // Get the next person in line
        String next = event.popNextWaitlistedName();

        // If no one is waiting, stop
        if (next == null) return;

        // Give them the newly available confirmed seat
        event.addConfirmedName(next);
    }
}