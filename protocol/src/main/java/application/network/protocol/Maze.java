package application.network.protocol;

import java.io.Serializable;
import java.util.List;

/**
 * Diese Klasse beschreibt das Labyrinth.
 */
public class Maze implements Serializable {

    private List<Field> fields;

    public List<Field> getFields() {
        return fields;
    }

    public Maze setFields(List<Field> fields) {
        this.fields = fields;
        return this;
    }
}
