package com.example.summit.session;

import com.example.summit.model.Entrant;

public class Session {
    private static Entrant currentEntrant;

    private Session() {}

    public static void setEntrant(Entrant e) { currentEntrant = e; }
    public static Entrant getEntrant() { return currentEntrant; }
}
