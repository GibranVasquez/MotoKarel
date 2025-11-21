package codigo;

import java.util.List;

public class Command {
    public enum Type { MOVE, TURN, REPEAT }
    public Type type;
    public int value; // MOVE steps or REPEAT count
    public String dir; // "izquierda" / "derecha" for TURN
    public List<Command> body; // for REPEAT

    public Command(Type t) { this.type = t; }

    public static Command move(int steps) { Command c = new Command(Type.MOVE); c.value = steps; return c; }
    public static Command turn(String dir) { Command c = new Command(Type.TURN); c.dir = dir; return c; }
    public static Command repeat(int times, java.util.List<Command> body) { Command c = new Command(Type.REPEAT); c.value = times; c.body = body; return c; }
}
