package variables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Noeud {
    protected int id;
    public List<Integer> initialDomaine;
    public List<Integer> listDomaine;

    public Noeud (int id) {
        this.id = id;
    }

    public void reinitListDomaine() {
        listDomaine = new ArrayList<>(initialDomaine);
    }
    public List<Integer> getListDomaine() {
        return listDomaine;
    }

    public void setDomaine(int nbVar) {
        initialDomaine = new ArrayList<>();
        for(int i = 1; i<=nbVar;i++)
            initialDomaine.add(i);
        listDomaine = new ArrayList<>(initialDomaine);
    }
}
