package codigo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser ligero que valida la sintaxis requerida y genera:
 * - Lista de Command (ejecutables)
 * - Tripletas (lista de strings)
 * - generatedCode (pretty)
 *
 * Comandos soportados:
 *   mover N;
 *   girar derecha;
 *   girar izquierda;
 *   repetir N { ... };
 *
 * Lanza ParseException si encuentra sintaxis inválida.
 */
public class CommandParser {

    public static class ParseException extends Exception { public ParseException(String m){ super(m); } }

    public static class Result {
        public List<Command> commands = new ArrayList<>();
        public List<String> triplets = new ArrayList<>();
        public String generatedCode = "";
    }

    // Patterns
    private static final Pattern MOVE_P = Pattern.compile("^mover\\s+(\\d+)\\s*;\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern TURN_P = Pattern.compile("^girar\\s+(derecha|izquierda)\\s*;\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern REPEAT_START_P = Pattern.compile("^repetir\\s+(\\d+)\\s*\\{\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern CLOSE_P = Pattern.compile("^\\s*}\\s*;?\\s*$");

    public static Result parseAndGenerate(String source) throws ParseException {
        String[] lines = source.split("\\r?\\n");
        Stack<List<Command>> stack = new Stack<>();
        stack.push(new ArrayList<>());
        Result res = new Result();
        int tcount = 1;

        for (int i = 0; i < lines.length; i++) {
            String raw = lines[i].trim();
            if (raw.isEmpty() || raw.startsWith("//")) continue;

            Matcher m;

            m = MOVE_P.matcher(raw);
            if (m.matches()) {
                int n = Integer.parseInt(m.group(1));
                Command c = Command.move(n);
                stack.peek().add(c);
                res.triplets.add("t" + (tcount++) + " = MOVE " + n);
                continue;
            }

            m = TURN_P.matcher(raw);
            if (m.matches()) {
                String dir = m.group(1).toLowerCase();
                Command c = Command.turn(dir);
                stack.peek().add(c);
                res.triplets.add("t" + (tcount++) + " = TURN " + dir.toUpperCase());
                continue;
            }

            m = REPEAT_START_P.matcher(raw);
            if (m.matches()) {
                int times = Integer.parseInt(m.group(1));
                Command rep = new Command(Command.Type.REPEAT);
                rep.value = times;
                rep.body = null; // will fill when '}' found
                stack.peek().add(rep);
                stack.push(new ArrayList<>());
                continue;
            }

            m = CLOSE_P.matcher(raw);
            if (m.matches()) {
                if (stack.size() == 1) throw new ParseException("'}' inesperado en línea " + (i+1));
                List<Command> body = stack.pop();
                List<Command> top = stack.peek();
                // find last repeat without body
                boolean found = false;
                for (int j = top.size()-1; j >= 0; j--) {
                    Command last = top.get(j);
                    if (last.type == Command.Type.REPEAT && last.body == null) {
                        last.body = body;
                        res.triplets.add("t" + (tcount++) + " = REPEAT " + last.value + " [ ... ]");
                        found = true;
                        break;
                    }
                }
                if (!found) throw new ParseException("Bloque '}' sin 'repetir' asociado en línea " + (i+1));
                continue;
            }

            // If none matched -> syntax error
            throw new ParseException("Comando no reconocido en línea " + (i+1) + ": '" + raw + "'");
        }

        if (stack.size() != 1) throw new ParseException("Falta '}' de cierre en el código.");

        res.commands = stack.pop();

        // pretty generated code
        StringBuilder sb = new StringBuilder();
        for (Command c : res.commands) sb.append(pretty(c, 0));
        res.generatedCode = sb.toString();

        return res;
    }

    private static String pretty(Command c, int indent) {
        String pad = "  ".repeat(indent);
        return switch (c.type) {
            case MOVE -> pad + "mover " + c.value + ";\n";
            case TURN -> pad + "girar " + c.dir + ";\n";
            case REPEAT -> {
                StringBuilder sb = new StringBuilder();
                sb.append(pad).append("repetir ").append(c.value).append(" {\n");
                if (c.body != null) for (Command sc : c.body) sb.append(pretty(sc, indent+1));
                sb.append(pad).append("};\n");
                yield sb.toString();
            }
            default -> pad + "// desconocido\n";
        };
    }
}
