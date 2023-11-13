package expressions;

public class Predicate extends Expression {
    public int type; //0 = room, 1 = weapon, 2 = person.
    public int number; //Number of the room, weapon or person.

    public Predicate(int type, int number) {
        this.type = type;
        this.number = number;
    }

}
