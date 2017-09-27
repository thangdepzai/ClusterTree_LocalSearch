/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clubfs_mfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thang
 */
public class Population {

    private ArrayList<Individual> population = new ArrayList<>();

    // hàm get population
    public ArrayList<Individual> getPopulation() {
        return population;
    }
// khoi tao quan the

    public void setPopulation(ArrayList<Individual> population) {
        this.population = population;
    }

    public void initPopulation() {
        for (int i = 0; i < CluBFS_MFO.sizePopulation; i++) {
            Individual indiv = new Individual();
            indiv.InitGene();

            indiv.setFitness();
            population.add(indiv);
        }

        calculateScalarFitness(population, CluBFS_MFO.sizePopulation);

    }

    public Individual LocalSearch(Individual indiv) {

        Individual ind = indiv;
        ArrayList<Integer> VertexConnect = new ArrayList<>();
        ArrayList<Edge> ListEdge = new ArrayList<Edge>();
        for (Edge e : indiv.getGene().get(CluBFS_MFO.NUMBER_OF_CLUSTERS).getListEdge()) {
            ArrayList<Integer> kq = Cluster_Edge_Connect(e);
            ArrayList<Integer> ArrVertexConnect = Edge_Min_2_Cluster(indiv.getGene().get(kq.get(0)), indiv.getGene().get(kq.get(1)));
            if (ind.getGene().get(kq.get(0)).getLevel() < ind.getGene().get(kq.get(1)).getLevel()) {
                ind.getTrace().put(kq.get(1), ArrVertexConnect.get(1));
            } else {
                ind.getTrace().put(kq.get(0), ArrVertexConnect.get(0));
            }
            for (int i : ArrVertexConnect) {
                if (!VertexConnect.contains(i)) {
                    VertexConnect.add(i);
                }
            }
            ListEdge.add(new Edge(ArrVertexConnect.get(0), ArrVertexConnect.get(1)));

        }

        Cluster clt = new Cluster(VertexConnect, ListEdge);
        ind.getGene().remove(CluBFS_MFO.NUMBER_OF_CLUSTERS);
        ind.getGene().add(clt);
        if (!(ind==indiv)) {
            System.out.println("\nfalse");

        }

        return ind;
    }

    public Individual LocalSearch2(Individual indiv) {

        int dem = 0;
        ArrayList<Integer> Cluster_Level_more_2 = new ArrayList<>();
        for (int i = 0; i < CluBFS_MFO.NUMBER_OF_CLUSTERS; i++) {
            if (indiv.getGene().get(i).getLevel() < 2) {
                dem++;
            } else {
                Cluster_Level_more_2.add(i);
            }
        }
        if (dem == CluBFS_MFO.NUMBER_OF_CLUSTERS) {
            return indiv;
        }

        Individual ind = indiv;
        for (int k : Cluster_Level_more_2) {
            Edge edge = null;
            for (Edge e : ind.getGene().get(CluBFS_MFO.NUMBER_OF_CLUSTERS).getListEdge()) {
                if (ind.getGene().get(k).getListCity().contains(e.getX()) || ind.getGene().get(k).getListCity().contains(e.getY())) {
                    ArrayList<Integer> kq = Cluster_Edge_Connect(e);
                    for (int i : kq) {
                        if (i != k && ind.getGene().get(i).getLevel() < ind.getGene().get(k).getLevel()) {

                            edge = e;
                        }
                    }
                }
            }
        
            ind.getGene().get(CluBFS_MFO.NUMBER_OF_CLUSTERS).getListEdge().remove(edge);
            ind.getGene().get(CluBFS_MFO.NUMBER_OF_CLUSTERS).getListEdge().add(new Edge(ind.getTrace().get(k), CluBFS_MFO.SOURCE_VERTEX));
            if (!ind.getGene().get(CluBFS_MFO.NUMBER_OF_CLUSTERS).getListCity().contains(CluBFS_MFO.SOURCE_VERTEX)) {
                ind.getGene().get(CluBFS_MFO.NUMBER_OF_CLUSTERS).getListCity().add(CluBFS_MFO.SOURCE_VERTEX);
            }

            ind.getGene().get(k).setLevel(1);
        }
      

        return ind;

    }

    public ArrayList<Integer> Cluster_Edge_Connect(Edge e) {
        ArrayList<Integer> kq = new ArrayList<Integer>();
        for (int i = 0; i < CluBFS_MFO.NUMBER_OF_CLUSTERS; i++) {
            if (CluBFS_MFO.ListClusterCity.get(i).contains(e.getX()) || CluBFS_MFO.ListClusterCity.get(i).contains(e.getY())) {
                kq.add(i);
            }

        }

        return kq;

    }

    public ArrayList<Integer> Edge_Min_2_Cluster(Cluster clt1, Cluster clt2) {
        double weight = Double.MAX_VALUE;
        ArrayList<Integer> kq = new ArrayList<Integer>();
        int m1 = -1;
        int m2 = -1;
        for (int i : clt1.getListCity()) {
            for (int k : clt2.getListCity()) {
                if (CluBFS_MFO.distances[k - 1][i - 1] < weight) {
                    weight = CluBFS_MFO.distances[k - 1][i - 1];
                    m1 = i;
                    m2 = k;
                }

            }

        }
        kq.add(m1);
        kq.add(m2);
        return kq;
    }

// lai ghep va dot bien
    public ArrayList<Individual> crossOver(Individual indiv1, Individual indiv2) {
        ArrayList<Individual> child = new ArrayList<Individual>();
        if (indiv1.getSkillFactor() == indiv2.getSkillFactor() && CluBFS_MFO.rand.nextDouble() < 0.3) {
            if (CluBFS_MFO.cross == 1) {
                child = Cross1(indiv1, indiv2);
            } else if (CluBFS_MFO.cross == 2) {

                child = Cross2(indiv1, indiv2);
            }

        } else {

          child.add(mutation(indiv1));
         child.add(mutation(indiv2));
 

        }

        return child;
    }

    public Individual mutation(Individual indiv) {
        Individual ind = new Individual();
        // clone individual
        for (int i = 0; i < CluBFS_MFO.NUMBER_OF_CLUSTERS + 1; i++) {

            try {
                Cluster clt = (Cluster) indiv.getGene().get(i).clone();
                ind.getGene().add(clt);

            } catch (CloneNotSupportedException e) {
                e.printStackTrace();

            }
        }

        Map<Integer, Integer> trace_copy = new HashMap<Integer, Integer>();
        for (int i : indiv.getTrace().keySet()) {
            trace_copy.put(i, indiv.getTrace().get(i));
        }
        ind.setTrace(trace_copy);

        int m1, m2, k = 0;
        // chon cluster co it nhat 1 canh
        do {
            k = CluBFS_MFO.rand.nextInt(CluBFS_MFO.NUMBER_OF_CLUSTERS);
        } while (indiv.getGene().get(k).getListEdge().size() < 2);
        // chon ngau nhien 2 dinh trong cluster
        do {
            m1 = indiv.getGene().get(k).getListCity().get(CluBFS_MFO.rand.nextInt(indiv.getGene().get(k).getListCity().size()));
        } while (indiv.getGene().get(k).isVertexConnectFull(m1));
        int[] truoc = indiv.FindPathBFS(m1);

        do {
            m2 = indiv.getGene().get(k).getListCity().get(CluBFS_MFO.rand.nextInt(indiv.getGene().get(k).getListCity().size()));
        } while (m1 == m2 || indiv.getGene().get(k).checkHasEdgeInList(m1, m2) || !indiv.getGene().get(k).getListCity().contains(truoc[m2 - 1]));

        ind.getGene().get(k).getListEdge().add(new Edge(m1, m2));
        ind.getGene().get(k).removeEdge(m2, truoc[m2 - 1]);
        for (int i = 0; i < CluBFS_MFO.NUMBER_OF_CLUSTERS; i++) {
            ind.getGene().get(i).setLevel(indiv.getGene().get(i).getLevel());
        }

        return ind;

    }
//
    public ArrayList<Individual> Cross1(Individual indiv1, Individual indiv2) {
        Individual ind1 = new Individual();
        Individual ind2 = new Individual();
        Prim_Heap algori = new Prim_Heap();
        for (int i = 0; i < CluBFS_MFO.NUMBER_OF_CLUSTERS; i++) {
            ind1.getGene().add(algori.Al_Prim_Heap(indiv1.getGene().get(i), indiv2.getGene().get(i), indiv1.getTrace().get(i)));
            ind2.getGene().add(algori.Al_Prim_Heap(indiv1.getGene().get(i), indiv2.getGene().get(i), indiv2.getTrace().get(i)));
        }
        // clone cluster index CluBFS_MFO.NUMBER_OF_CLUSTERS ind1
        try {
            ind1.getGene().add((Cluster) indiv1.getGene().get(CluBFS_MFO.NUMBER_OF_CLUSTERS).clone());
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
        }

        Map<Integer, Integer> trace_copy1 = new HashMap<Integer, Integer>();
        for (int i : indiv1.getTrace().keySet()) {
            trace_copy1.put(i, indiv1.getTrace().get(i));
        }
        ind1.setTrace(trace_copy1);
        
        
        // clone cluster index CluBFS_MFO.NUMBER_OF_CLUSTERS ind2
        try {
            ind2.getGene().add((Cluster) indiv2.getGene().get(CluBFS_MFO.NUMBER_OF_CLUSTERS).clone());
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
        }
        Map<Integer, Integer> trace_copy = new HashMap<Integer, Integer>();
        for (int i : indiv2.getTrace().keySet()) {
            trace_copy.put(i, indiv2.getTrace().get(i));
        }
        ind2.setTrace(trace_copy);
        
        
        
        
        // copy level
        for (int i = 0; i < CluBFS_MFO.NUMBER_OF_CLUSTERS; i++) {
            ind1.getGene().get(i).setLevel(indiv1.getGene().get(i).getLevel());
            ind2.getGene().get(i).setLevel(indiv2.getGene().get(i).getLevel());
        }
        
        
        
        ArrayList<Individual> Child = new ArrayList<>();
        Child.add(ind1);
        Child.add(ind2);
        return Child;

    }
     public ArrayList<Individual> Cross2(Individual indiv1, Individual indiv2) {
        Individual ind1 = new Individual();
        Individual ind2 = new Individual();
        Dijkstra_Heap algori = new Dijkstra_Heap();
        for (int i = 0; i < CluBFS_MFO.NUMBER_OF_CLUSTERS; i++) {
            ind1.getGene().add(algori.Al_Dijkstra_Heap(indiv1.getGene().get(i), indiv2.getGene().get(i), indiv1.getTrace().get(i)));
            ind2.getGene().add(algori.Al_Dijkstra_Heap(indiv1.getGene().get(i), indiv2.getGene().get(i), indiv2.getTrace().get(i)));
        }
        // clone cluster end for ind1
        try {
            ind1.getGene().add((Cluster) indiv1.getGene().get(CluBFS_MFO.NUMBER_OF_CLUSTERS).clone());
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
        }

        Map<Integer, Integer> trace_copy1 = new HashMap<Integer, Integer>();
        for (int i : indiv1.getTrace().keySet()) {
            trace_copy1.put(i, indiv1.getTrace().get(i));
        }
        ind1.setTrace(trace_copy1);
        
        
        // clone cluster end for ind2
        try {
            ind2.getGene().add((Cluster) indiv2.getGene().get(CluBFS_MFO.NUMBER_OF_CLUSTERS).clone());
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Population.class.getName()).log(Level.SEVERE, null, ex);
        }
        Map<Integer, Integer> trace_copy = new HashMap<Integer, Integer>();
        for (int i : indiv2.getTrace().keySet()) {
            trace_copy.put(i, indiv2.getTrace().get(i));
        }
        ind2.setTrace(trace_copy);
        
        // copy level
        for (int i = 0; i < CluBFS_MFO.NUMBER_OF_CLUSTERS; i++) {
            ind1.getGene().get(i).setLevel(indiv1.getGene().get(i).getLevel());
            ind2.getGene().get(i).setLevel(indiv2.getGene().get(i).getLevel());
        }
        ArrayList<Individual> Child = new ArrayList<>();
        Child.add(ind1);
        Child.add(ind2);
        return Child;

    }


    public void calculateScalarFitness(ArrayList<Individual> pop, int popLength) {

        double[] scalarFitness = new double[popLength];
        int[] skillFactor = new int[popLength];

        ArrayList<Individual> temp_pop = (ArrayList<Individual>) pop.clone();

        for (int i = 0; i < popLength; i++) {
            scalarFitness[i] = 0;
            temp_pop.get(i).setScalarFitness(0);

        }

        for (int i = 0; i < 2; i++) {
            int j = i;
            Collections.sort(temp_pop, new Comparator<Individual>() {
                @Override

                public int compare(Individual o1, Individual o2) {
                    if (o1.getFitness()[j] > o2.getFitness()[j]) {
                        return 1;
                    } else if (o1.getFitness()[j] < o2.getFitness()[j]) {
                        return -1;
                    } else {
                        return 0;
                    }

                }
            });
            for (int k = 0; k < popLength; k++) {
                Individual ind = pop.get(k);
                if (1.0 / (temp_pop.indexOf(ind) + 1) > scalarFitness[k]) {
                    scalarFitness[k] = (1.0 / (temp_pop.indexOf(ind) + 1));
                    skillFactor[k] = i + 1;
                }
            }
        }
        for (int k = 0; k < popLength; k++) {

            pop.get(k).setScalarFitness(scalarFitness[k]);
            pop.get(k).setSkillFactor(skillFactor[k]);
        }
        skillFactor = null;
        scalarFitness = null;

    }

}
