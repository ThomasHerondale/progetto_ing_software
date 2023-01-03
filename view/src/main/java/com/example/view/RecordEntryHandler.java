package com.example.view;

import entities.Shift;

public class RecordEntryHandler {
    public void clickedRecordEntry(Shift shiftEntity) {
        NavigationManager.getInstance().createPopup("Remote Record Entry",
                controller -> new RemoteRecordEntryPopup(shiftEntity, this));
    }
}
