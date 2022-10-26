package variables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Contraintes {
    Pair<Noeud, Noeud> noeudsPair;
    List<Pair<Integer, Integer>> coupleList;

    public Contraintes(Noeud n1, Noeud n2) {
        this.noeudsPair = new Pair<>(n1, n2);
        this.coupleList = new ArrayList<>();
    }

    public void generateListValue() {
        for (int i = 0; i < noeudsPair.getLeft().domaine.length; i++)
            for (int j = 0; j < noeudsPair.getRight().domaine.length; j++)
                this.coupleList.add(new Pair<>(noeudsPair.getLeft().domaine[i], noeudsPair.getRight().domaine[j]));
    }

    public void generateListValueNQueen() {
        generateListValue();
        this.coupleList.removeIf(pair -> pair.getLeft().equals(pair.getRight()) || Math.abs(noeudsPair.getLeft().id - noeudsPair.getRight().id) == Math.abs(pair.getLeft() - pair.getRight()));
    }

    public List<Pair<Integer, Integer>> getCoupleList() {
        return coupleList;
    }

    @Override
    public String toString() {
        return "Contraintes entre noeud " + noeudsPair.getLeft().id + " et noeud " + noeudsPair.getRight().id + " : " +
                coupleList;
    }
}
