package variables;

import java.util.*;

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
        for (int i = 1; i <= nbVar; i++) {
            this.noeudList.add(new Noeud(i));
            this.noeudList.get(noeudList.size() - 1).setDomaine(nbVar);
        }
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

    public boolean coherentAssignBackjumping(int a, int b, int valueA, int valueB) {
        boolean coherent = false;
        for (Contraintes c : this.contraintesList) {
            if (c.noeudsPair.getLeft().id == a + 1 && c.noeudsPair.getRight().id == b + 1)
                if (c.coupleList.contains(new Pair<>(valueA, valueB)))
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
                if (n.getListDomaine().size() == n.initialDomaine.size())
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
                if (n.getListDomaine().size() == n.initialDomaine.size())
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
                if (n.getListDomaine().size() == n.initialDomaine.size())
                    listSoluce.add(n.getListDomaine().get(0));
                else
                    listSoluce.set(i, n.getListDomaine().get(0));
                n.getListDomaine().remove(0);
                boolean consistant = true;
                int k = 0;
                while (k < i && consistant) {
                    if (k > coupable) coupable = k;
                    //On cherche un conflit, donc il faut que l'assignation ne soit pas cohérente
                    if (!(coherentAssignBackjumping(k, i, listSoluce.get(k), listSoluce.get(i))) /*&& !coherentAssignNQueen(listSoluce)*/)
                        consistant = false;
                    else
                        k++;
                }
                if (consistant) ok = true;
            }
            if (!ok) {
                if (n.getListDomaine().isEmpty() && coupable == -1)
                    i--;
                else
                    i = coupable;
                int tempListSize = listSoluce.size();
                for (int temp = i + 1; temp < tempListSize; temp++) {
                    this.noeudList.get(temp).reinitListDomaine();
                    listSoluce.remove(i + 1);
                }
            } else {
                i++;
            }
            coupable = -1;
        }
        if (i < 0) System.out.println("UNSAT");
        else System.out.println(listSoluce);
    }

    public void forwardchecking() {
        int i = 0;
        List<Integer> listSoluce = new ArrayList<>();

        //initialisation de la map qui va gérer les contraintes pour le forwardchecking
        Map<Integer, Map<Integer, List<Integer>>> mapContrainte = new HashMap<>();
        for (int temp = 1; temp < noeudList.size(); temp++) {
            mapContrainte.put(temp, new HashMap<>());
            for (int temp2 = 0; temp2 < temp; temp2++)
                mapContrainte.get(temp).put(temp2, new ArrayList<>());
        }

        while (i > -1 && i < this.noeudList.size()) {
            Noeud n = this.noeudList.get(i);
            boolean ok = false;
            while (!ok && !n.getListDomaine().isEmpty()) {
                if (n.getListDomaine().size() == n.initialDomaine.size())
                    listSoluce.add(n.getListDomaine().get(0));
                else
                    listSoluce.set(i, n.getListDomaine().get(0));
                n.getListDomaine().remove(0);
                boolean domaineVide = false;
                int coef = 1;
                for (int k = i + 1; k < this.noeudList.size(); k++) {
                    mapContrainte.get(k).get(i).add(listSoluce.get(i) - coef);
                    mapContrainte.get(k).get(i).add(listSoluce.get(i));
                    mapContrainte.get(k).get(i).add(listSoluce.get(i) + coef);
                    coef++;
                    Set<Integer> completeList = new HashSet<>();
                    for(int temp = 0;temp<k;k++)
                        completeList.addAll(mapContrainte.get(k).get(temp));
                    if(completeList.containsAll(noeudList.get(i).initialDomaine))
                        domaineVide = true;
                }
                if (domaineVide)
                    for (int k = i + 1; k < this.noeudList.size(); k++)
                        mapContrainte.get(k).get(i).clear();
                else
                    ok = true;
            }
            if (!ok) {
                for (int k = i + 1; k < this.noeudList.size(); k++)
                    mapContrainte.get(k).get(i).clear();
                i--;
            } else
                i++;
        }
        if (i < 0)
            System.out.println("UNSAT");
        else
            System.out.println(listSoluce);
    }

    private void ReviseNqueen(Map<Integer, Map<Integer, List<Integer>>> mapContrainte, int k, int i) {

    }


    public static void main(String[] args) {
        CSP csp = new CSP(19, true);
        csp.backjumping();
        CSP csp2 = new CSP(6, true);
        csp2.backTrackingNQueen();

    }
}


