package superapp.logic.utils.convertors;

public class ConvertIdsHelper {

    final public static String DELIMITER_ID = "_";

    public static String concatenateIds(String[] ids) {

        return String.join(DELIMITER_ID, ids);
    }

    public static String[] splitConcretedIds(String concretedStr) {
        return concretedStr.split(DELIMITER_ID);
    }

}
