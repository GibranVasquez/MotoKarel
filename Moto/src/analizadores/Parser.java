package analizadores;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static List<String> comandos = new ArrayList<>();

    public static void reset() {
        comandos.clear();
    }

    public static void addComando(String cmd) {
        comandos.add(cmd);
    }

    public static List<String> getComandos() {
        return comandos;
    }

    public static String getComandosTexto() {
        StringBuilder sb = new StringBuilder();
        for (String c : comandos)
            sb.append(c).append("\n");
        return sb.toString();
    }
}
