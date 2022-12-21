package io.space.alt.altimpl;

import io.space.alt.AccountEnum;
import io.space.alt.Alt;

public final class OfflineAlt extends Alt {
    public OfflineAlt(String userName) {
        super(userName,AccountEnum.OFFLINE);
    }
}
