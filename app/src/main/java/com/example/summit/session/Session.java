package com.example.summit.session;

import com.example.summit.model.Admin;
import com.example.summit.model.Entrant;
import com.example.summit.model.Organizer;

public class Session {
    private static Entrant currentEntrant;
    private static Organizer currentOrganizer;
    private static Admin currentAdmin;
    private Session() {}

    public static void setEntrant(Entrant e) { currentEntrant = e; }
    public static Entrant getEntrant() { return currentEntrant; }

    public static void setOrganizer(Organizer o) { currentOrganizer = o; }
    public static Organizer getOrganizer() { return currentOrganizer; }

    /**
     * Sets the current admin user in the session.
     *
     * @param a The Admin object to store in the session
     */
    public static void setAdmin(Admin a) { currentAdmin = a; }

    /**
     * Retrieves the current admin user from the session.
     *
     * @return The current Admin object, or null if no admin is logged in
     */
    public static Admin getAdmin() { return currentAdmin; }

}
