package superapp.miniapps;

public enum MiniAppNames {

    UNKNOWN,
    DATING,
    EVENT,
    GROUP,
    MARKETPLACE;


    public static MiniAppNames strToMiniAppName(String miniappName) {
        try {
            return MiniAppNames.valueOf(miniappName);
        }catch (Exception e){
            return UNKNOWN;
        }
    }
}
