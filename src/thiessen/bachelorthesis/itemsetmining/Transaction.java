package thiessen.bachelorthesis.itemsetmining;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Maximilian Thiessen on 13.10.2017.
 * Represents a transaction in the database/sliding window.
 * Is basically just a HashSet with an id.
 * The id is mainly used to group the decomposed maximal X_i's
 */
public class Transaction extends HashSet<Integer> {
    //Global counter for all the transactions, so all the transaction have got different ids
    public static long idCounter = 1;

    private long id;

    public Transaction() {
        id = idCounter;
        idCounter++;
    }

    public Transaction(Collection<? extends Integer> c) {
        super(c);
        id = idCounter;
        idCounter++;
    }

    public long getId() {
        return id;
    }
}
