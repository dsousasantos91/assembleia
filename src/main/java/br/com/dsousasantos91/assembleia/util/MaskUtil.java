package br.com.dsousasantos91.assembleia.util;

import javax.swing.text.MaskFormatter;
import java.text.ParseException;

public class MaskUtil {
    public static String cep(String cep) {
        try {
            MaskFormatter mf = new MaskFormatter("#####-###");
            mf.setValueContainsLiteralCharacters(false);
            cep = mf.valueToString(cep);
        } catch (ParseException e) {
            e.getCause();
        }
        return cep;
    }

    public static String cpf(String cpf) {
        try {
            MaskFormatter mf = new MaskFormatter("###.###.###-##");
            mf.setValueContainsLiteralCharacters(false);
            cpf = mf.valueToString(cpf) ;
        } catch (ParseException e) {
            e.getCause();
        }
        return cpf;
    }
}
