package org.main.excaption;

import static org.lwjgl.openal.AL10.*;

public class OpenAlcException extends RuntimeException {

    public OpenAlcException(int err){
        super("Internal " + (err == AL_INVALID_NAME ? "invalid name " :
                err == AL_INVALID_ENUM ? "invalid enum " :
                        err == AL_INVALID_VALUE ? "invalid value " : err == AL_INVALID_OPERATION ? "invalid operation" : "unknow"
                ) + " OpenAL Exception.");
    }

}
