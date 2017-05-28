package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;

/**
 * Created by Mike on 06.11.2016.
 */
public class SmokeyService {

    public CoreResponse<Boolean> throwException () {
        throw new RuntimeException("I am a smokey service");
    }
}
