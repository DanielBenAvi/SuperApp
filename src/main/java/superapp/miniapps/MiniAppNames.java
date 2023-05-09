package superapp.miniapps;

public enum MiniAppNames {

    DATING,
    EVENT,
    GROUP,
    MARKETPLACE,
    UNKNOWN;

    public static final MiniAppNames getStr(String miniAppName) {
        if (miniAppName.equals("DATING"))
            return DATING;

        if (miniAppName.equals("EVENT"))
            return EVENT;
        if (miniAppName.equals("GROUP"))
            return DATING;
        if (miniAppName.equals("MARKETPLACE"))
            return DATING;

        return UNKNOWN;
    }

}
