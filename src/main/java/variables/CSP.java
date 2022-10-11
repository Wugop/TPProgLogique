package variables;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class CSP {
    private List<Noeud> noeudList;
    private List<Contraintes> contraintesList;
    private double densite;
    private double durete;

    public CSP(int nbVar, double densite, double durete) {
        this.densite = densite;
        this.durete = durete;
        this.noeudList = new ArrayList<>();
        this.contraintesList = new ArrayList<>();
        for (int i = 0; i < nbVar; i++)
            this.noeudList.add(new Noeud(i));
        for (int i = 0; i < nbVar - 1; i++)
            for (int j = i + 1; j < nbVar; j++)
                this.contraintesList.add(new Contraintes(noeudList.get(i), noeudList.get(j)));
        for (int i = 0; i < ((nbVar * (nbVar - 1) / 2)) * (1 - densite); i++)
            contraintesList.remove((int) (Math.random() * contraintesList.size()));
        this.contraintesList.forEach(f -> {
            f.generateListValue();
            //faire la dureté -> Retirer le % de couple de valeur
            int tailleCoupleList = f.getCoupleList().size();
            for (int i = 0; i < (int) (tailleCoupleList * (1 - durete)); i++)
                f.getCoupleList().remove((int) (Math.random() * f.getCoupleList().size()));
        });
    }

    @Override
    public String toString() {
        return "CSP avec " + densite + " de densite et " + durete + " de durete\n" +
                noeudList + "\n" +
                contraintesList;
    }


    public boolean coherentAssign(List<Integer> listSoluce) {
        boolean ok = true;
        for (int i = 0; i < listSoluce.size() - 1; i++) {
            for (int j = i + 1; j < listSoluce.size(); j++) {
                for (Contraintes c : this.contraintesList) {
                    if (c.noeudsPair.getLeft().id == i && c.noeudsPair.getRight().id == j) {
                        if (!c.coupleList.contains(new Pair<>(listSoluce.get(i), listSoluce.get(j))))
                            ok = false;
                    }
                }
            }
        }
        return ok;
    }

    public void resolution() {
        int i = 0;
        List<Integer> listSoluce = new ArrayList<>();
        while (i > -1 && i < this.noeudList.size()) {
            Noeud n = this.noeudList.get(i);
            boolean ok = false;
            while (!ok && !n.getListDomaine().isEmpty()) {
                if (n.getListDomaine().size() == n.domaine.length)
                    listSoluce.add(n.getListDomaine().get(0));
                else
                    listSoluce.set(i, n.getListDomaine().get(0));
                n.getListDomaine().remove(0);
                if (listSoluce.size() < 1 || this.coherentAssign(listSoluce))
                    ok = true;
            }
            if (!ok)
                i--;
            else
                i++;
        }
        if (i == -1)
            System.out.println("UNSAT");
        else
            System.out.println(listSoluce);
    }

    public static void main(String[] args) {
        CSP csp = new CSP(5, 0.8, 0.5);
        System.out.println(csp);
        csp.resolution();

    }
}


