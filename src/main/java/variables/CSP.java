package variables;

import java.util.ArrayList;
import java.util.Iterator;
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
        for (int i = 1; i <= nbVar; i++)
            this.noeudList.add(new Noeud(i));
        for (int i = 0; i < nbVar - 1; i++)
            for (int j = i + 1; j < nbVar; j++)
                this.contraintesList.add(new Contraintes(noeudList.get(i), noeudList.get(j)));
        for (int i = 0; i < ((nbVar * (nbVar - 1) / 2)) * (1 - densite); i++)
            contraintesList.remove((int) (Math.random() * contraintesList.size()));
        this.contraintesList.forEach(f -> {
            f.generateListValue();
            int tailleCoupleList = f.getCoupleList().size();
            for (int i = 0; i < (int) (tailleCoupleList * (1 - durete)); i++)
                f.getCoupleList().remove((int) (Math.random() * f.getCoupleList().size()));
        });
    }

    public CSP(int nbVar, boolean nQueen) {
        this.noeudList = new ArrayList<>();
        this.contraintesList = new ArrayList<>();
        for (int i = 1; i <= nbVar; i++)
            this.noeudList.add(new Noeud(i));
        for (int i = 0; i < nbVar - 1; i++)
            for (int j = i + 1; j < nbVar; j++)
                this.contraintesList.add(new Contraintes(noeudList.get(i), noeudList.get(j)));
        for (Contraintes ct : this.contraintesList) {
            ct.generateListValueNQueen();
        }
    }

    @Override
    public String toString() {
        return "CSP avec " + densite + " de densite et " + durete + " de durete\n" +
                noeudList + "\n" +
                contraintesList;
    }


    /**
     * Vérifie les contraintes
     */
    public boolean coherentAssign(List<Integer> listSoluce) {
        boolean ok = true;
        for (int i = 0; i < listSoluce.size() - 1; i++) {
            for (int j = i + 1; j < listSoluce.size(); j++) {
                for (Contraintes c : this.contraintesList) {
                    if (c.noeudsPair.getLeft().id == i + 1 && c.noeudsPair.getRight().id == j + 1) {
                        if (!c.coupleList.contains(new Pair<>(listSoluce.get(i), listSoluce.get(j))))
                            ok = false;
                    }
                }
            }
        }
        return ok;
    }

    public boolean coherentAssignNQueen(List<Integer> listSoluce) {
        boolean ok = true;
        for (int i = 0; i < listSoluce.size() - 1; i++) {
            for (int j = i + 1; j < listSoluce.size(); j++) {
                if (listSoluce.get(i).equals(listSoluce.get(j)) || (Math.abs(i - j) == Math.abs(listSoluce.get(i) - listSoluce.get(j)))) {
                    ok = false;
                    break;
                }
            }
        }
        return ok;
    }

    public boolean coherentAssignBackjumping(int a, int b,int valueA, int valueB) {
        boolean coherent = false;
        for (Contraintes c : this.contraintesList) {
            if (c.noeudsPair.getLeft().id == a + 1 && c.noeudsPair.getRight().id == b + 1)
                if(c.coupleList.contains(new Pair<>(valueA,valueB)))
                    coherent = true;
        }
        return coherent;
    }

    public void backTracking() {
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

    public void backTrackingNQueen() {
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
                if (listSoluce.size() < 1 || (this.coherentAssign(listSoluce) && this.coherentAssignNQueen(listSoluce)))
                    ok = true;
            }
            if (!ok) {
                listSoluce.remove(i);
                n.reinitListDomaine();
                i--;
            } else
                i++;
        }
        if (i == -1)
            System.out.println("UNSAT");
        else
            System.out.println(listSoluce);
    }


    public void backjumping() {
        int i = 0;
        int coupable = -1;
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
                boolean consistant = true;
                int k = 0;
                while (k < i && consistant) {
                    if (k > coupable) coupable = k;
                    int tmp = 0;
                    while (tmp < k+1) {
                        //On cherche un conflit, donc il faut que l'assignation ne soit pas cohérente
                        if (!(coherentAssignBackjumping(tmp, i,listSoluce.get(tmp),listSoluce.get(i))) && !coherentAssignNQueen(listSoluce)) {
                            consistant = false;
                            break;
                        } else tmp++;
                    }
                    k++;
                }
                if (consistant) ok = true;
            }
            if (!ok) {
                i = coupable;
                for (int temp = i + 1; temp < this.noeudList.size(); temp++)
                    this.noeudList.get(temp).reinitListDomaine();
            } else {
                i++;
                coupable = -1;
            }
        }
        if (i < 0) System.out.println("UNSAT");
        else System.out.println(listSoluce);
    }

    public static void main(String[] args) {
        CSP csp = new CSP(4, true);
        csp.backjumping();
    }
}


