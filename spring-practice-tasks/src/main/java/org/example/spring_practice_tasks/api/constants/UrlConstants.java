package org.example.spring_practice_tasks.api.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UrlConstants {

    public static final String ID_URL = "/{id}";

    public static final String PING_URL = "/ping";

    public static final String NOTE_URL = "/notes";
    public static final String NOTE_WITH_ID_URL = NOTE_URL + ID_URL;
    public static final String NOTE_EXPORT_URL = NOTE_URL + "/export";
    public static final String NOTE_BATCH_URL = NOTE_URL + "/batch";

}
