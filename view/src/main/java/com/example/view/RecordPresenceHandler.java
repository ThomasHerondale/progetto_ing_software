package com.example.view;

import database.DBMSDaemon;
import database.DBMSException;

public class RecordPresenceHandler {
    public void clickedRecordEntrance(String name, String surname, String id) {
        try {
            if (DBMSDaemon.getInstance().checkCredentials(id, name, surname)){

            }
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
    }

    public void clickedRecordExit(String name, String surname, String id) {
        try {
            if (DBMSDaemon.getInstance().checkCredentials(id, name, surname)){
                DBMSDaemon.getInstance()
            }
        } catch (DBMSException e) {
            //TODO:
            throw new RuntimeException(e);
        }
    }
}
