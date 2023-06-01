package superapp.logic.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UtilHelper {

    public static <T> T jacksonHandle(Object toRead, Class<T> readAs, ObjectMapper jackson) throws Exception {

            String json = jackson.writeValueAsString(toRead);
            return jackson.readValue(json, readAs);
    }



    public static int getPageAsInt(String page, int defaultPage)  {
        try {
            int pageAsInt =  Integer.parseInt(page);
            if (pageAsInt < 0)
                return defaultPage;
            return pageAsInt;
        } catch (Exception e) {
            return defaultPage;
        }
    }

    public static int getSizeAsInt(String size, int defaultSize)  {

        try {
            int sizeAsInt =  Integer.parseInt(size);
            if (sizeAsInt <= 0)
                return defaultSize;
            return sizeAsInt;
        } catch (Exception e) {
            return defaultSize;
        }
    }
}

