package adr.ejemplo.adrprecios;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateKey {

    public String turnRequest (String requestJson){

        String cutResqJson = requestJson.subSequence(0, 10).toString();

        try {
            SimpleDateFormat formarOrigin = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatResult = new SimpleDateFormat("ddMMyyyy");

            Date dateResult = formarOrigin.parse(cutResqJson);
            String dateFormat = formatResult.format(dateResult);

            String day = dateFormat.subSequence(0, 2).toString();
            int numDay = Integer.parseInt(day);

            int resultado = ((Integer.parseInt(dateFormat))*6)/3;
            String hexa = Integer.toHexString(resultado + (numDay * 7));
            cutResqJson = hexa.subSequence(0, 6).toString();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return cutResqJson;
    }

}
