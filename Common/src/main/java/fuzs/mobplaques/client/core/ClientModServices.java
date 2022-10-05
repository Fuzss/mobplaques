package fuzs.mobplaques.client.core;

import fuzs.puzzleslib.util.PuzzlesUtil;

public class ClientModServices {
    public static final ClientAbstractions ABSTRACTIONS = PuzzlesUtil.loadServiceProvider(ClientAbstractions.class);
}
