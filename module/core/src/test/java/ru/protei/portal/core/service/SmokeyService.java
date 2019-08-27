package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;

/**
 * Created by Mike on 06.11.2016.
 */
public class SmokeyService {

    public Result<Boolean> throwException () {
        throw new RuntimeException("I am a smokey service");
    }
}
