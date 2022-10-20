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
        Iterator<Pair<Integer, Integer>> it = coupleList.listIterator();
        while(it.hasNext()) {
            Pair<Integer,Integer> pair = it.next();
            if(pair.getLeft().equals(pair.getRight()) || ((noeudsPair.getLeft().id - noeudsPair.getRight().id) + (pair.getLeft() - pair.getRight())) == 0)
                this.coupleList.remove(pair);
            it.remove();
        }
    }

    public List<Pair<Integer, Integer>> getCoupleList() {
        return coupleList;
    }

    @Override
    public String toString() {
        return "Contraintes entre noeud " + noeudsPair.getLeft().id + "et noeud " + noeudsPair.getRight().id + " : " +
                coupleList;
    }
}
