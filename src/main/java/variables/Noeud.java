package variables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Noeud {
    protected int id;
    public final Integer[] domaine = {1,2,3,4};
    public List<Integer> listDomaine;

    public Noeud (int id) {
        this.id = id;
        listDomaine = new ArrayList<>(Arrays.asList(domaine));
    }

    public void reinitListDomaine() {
        listDomaine = new ArrayList<>(Arrays.asList(domaine));
    }
    public List<Integer> getListDomaine() {
        return listDomaine;
    }
}
