/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clubfs_mfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author thang
 */
public class Prim_Heap {
  
    private double[] d = new double[CluBFS_MFO.NUM_CITY];
    private int[] p = new int[CluBFS_MFO.NUM_CITY];
     private int[] K = new int[CluBFS_MFO.NUM_CITY];
    public Prim_Heap() {
        Arrays.fill(d, 0);
        Arrays.fill(K, 0);
        Arrays.fill(p, 0);
    }
    //----------------------------heap---------------------
    public int parent(int i) {
        return (i + 1) / 2 - 1;
    }

    

    public int lChild(int i) {
        int x = i + 1;
        int y = x * 2;
        return y - 1;
    }

    public int rChild(int i) {
        int x = i + 1;
        int y = x * 2 + 1;
        return y - 1;
    }

    public Cluster Al_Prim_Heap(Cluster clt1, Cluster clt2, int start) {

        for (int u : clt1.getListCity()) {
            d[u - 1] = Double.MAX_VALUE;
            p[u - 1] = 0;
            K[u - 1] = 0;
        }
        d[start - 1] = 0;
        ArrayList<Double> Q = new ArrayList<>();
        for (int i = 0; i < clt1.getListCity().size(); i++) {
            Q.add(0.0);
        }

        int j = 0;
        Map<Integer, Integer> pos = new HashMap<Integer, Integer>();
        for (int i : clt1.getListCity()) {
            Q.set(j, d[i - 1]);
            pos.put(j, i);
            j++;
        }
        Build_Min_Heap(Q, pos);
        while (Q.size() != 0) {
            int u = Extract_Min(Q, pos);
            K[u - 1] = 1;
            ArrayList<Integer> Keof_u = DsKe(u, clt1, clt2);
            if (!Keof_u.isEmpty()) {
                for (int v : Keof_u) {
                    if (K[v - 1] == 0 && d[v - 1] > CluBFS_MFO.distances[u - 1][v - 1]) {
                        d[v - 1] = CluBFS_MFO.distances[u - 1][v - 1];
                        p[v - 1] = u;
                        int m = 0;
                        for (int i : pos.keySet()) {
                            if (pos.get(i) == v) {
                                m = i;
                                break;
                            }
                        }
                        Decrease_Key(Q, m, d[v - 1], pos);
                    }
                }
            }
        }

        ArrayList<Edge> NewListEdge = new ArrayList<Edge>();
        ArrayList<Integer> T = (ArrayList<Integer>) clt1.getListCity().clone();
        T.remove(T.indexOf(start));
        for (int i : T) {
            NewListEdge.add(new Edge(p[i - 1], i));
        }

        return new Cluster(clt1.getListCity(), NewListEdge);
    }
  private void Build_Min_Heap(ArrayList<Double> Q, Map<Integer, Integer> pos) {
        int n = Q.size();
        for (int i = n / 2 - 1; i > -1; i--) {
            Min_Heapify(Q, i, n, pos);
        }
    }

    private void Min_Heapify(ArrayList<Double> A, int i, int n, Map<Integer, Integer> pos) {
        int l = lChild(i);
        int r = rChild(i);
        int minimum;
        if (l < n && A.get(l) < A.get(i)) {
            minimum = l;
        } else {
            minimum = i;
        }

        if (r < n && A.get(r) < A.get(minimum)) {
            minimum = r;
        }

        if (minimum != i) {

            double tmp = A.get(minimum);
            A.set(minimum, A.get(i));
            A.set(i, tmp);
            int tmp1 = pos.get(i);
            pos.put(i, pos.get(minimum));
            pos.put(minimum, tmp1);
            Min_Heapify(A, minimum, n, pos);
        }

    }

    private int Extract_Min(ArrayList<Double> Q, Map<Integer, Integer> pos) {
        int n = Q.size();

        int k = pos.get(0);
        Q.set(0, Q.get(n - 1));
        int tmp = pos.get(n - 1);
        pos.put(n - 1, pos.get(0));
        pos.put(0, tmp);
        Q.remove(n - 1);
        Min_Heapify(Q, 0, n - 1, pos);
        return k;
    }
     private void Decrease_Key(ArrayList<Double> Q, int m, double d, Map<Integer, Integer> pos) {
        Q.set(m, d);
        while (m > 0) {
            if (Q.get(parent(m)) > Q.get(m)) {
                int c = parent(m);
                double tmp = Q.get(c);
                Q.set(c, Q.get(m));
                Q.set(m, tmp);
                int tmp1 = pos.get(m);
                pos.put(m, pos.get(c));
                pos.put(c, tmp1);
                m = c;

            } else {
                break;
            }
        }

    }
       public ArrayList<Integer> DsKe(int v, Cluster clt1, Cluster clt2) {
        ArrayList<Integer> L1 = new ArrayList<>();
        for (Edge e : clt1.getListEdge()) {
            if (e.getX() == v && !L1.contains(e.getY())) {
                L1.add(e.getY());
            } else if (e.getY() == v && !L1.contains(e.getX())) {
                L1.add(e.getX());
            }
        }
        for (Edge e : clt2.getListEdge()) {
            if (e.getX() == v && !L1.contains(e.getY())) {
                L1.add(e.getY());
            } else if (e.getY() == v && !L1.contains(e.getX())) {
                L1.add(e.getX());
            }
        }
        return L1;
    }

    
}

    
}
