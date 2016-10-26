/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kuhnmunkres;

import java.util.Set;

/**
 *
 * @author juancarlosroot
 */
class Labeling {

    public Labeling() {
    }
    
    int[] m_RowsLabeling; //The label of the X vertices., The ones on the right of the rows.
    int[] m_ColumnsLabeling; //The label of the Y vertices, the ones on the lower part of the columns.
    int m_alpha = 0;

    int CalculateAlpha(Set<Integer> m_i_S, Set<Integer> m_i_T, Matrix originalG) /*Set S of the algorithm*/ /*, Set T of the algorithm*/ {
        System.out.println("1 Calculate alpha");
        m_alpha = 100000; //Set the lowest to alpha, so anything is lower than that.
        for (int x : m_i_S)//Changed to a Foreach
        {
            for (int y = 0; y < originalG.get_m_WeightMatrix().length; y++) {
                if (m_i_T.contains(y)) {
                    continue;
                } else {
                    int iTemp = m_RowsLabeling[x] + m_ColumnsLabeling[y] - originalG.get_m_WeightMatrix()[x][y]; //This was WRONG XD

                    if (iTemp <= 0) {
                        continue;
                    }

                    System.out.println("iTemp, from the aplha, is: " + iTemp);
                    m_alpha = GetMin(m_alpha, iTemp);
                }
            }

        }
        System.out.println("2 Calculate alpha, the final alpha is: " + m_alpha);
        return m_alpha; //PLACE HOLDER!
    }

    Matrix UpdateLabeling(Set<Integer> m_i_S, Set<Integer> m_i_T, Matrix originalG, Matrix labelingG) {
        CalculateAlpha(m_i_S, m_i_T, originalG);
        for (int s : m_i_S) {
            System.out.println("Update labeling S " + s);
            m_RowsLabeling[s] -= m_alpha;
        }

        for (int t : m_i_T) {
            m_ColumnsLabeling[t] += m_alpha;
        }

        for (int x = 0; x < originalG.get_m_WeightMatrix().length; x++) {
            for (int y = 0; y < originalG.get_m_WeightMatrix().length; y++) {
                labelingG.get_m_WeightMatrix()[x][y]
                        = (originalG.get_m_WeightMatrix()[x][y] >= m_RowsLabeling[x] + m_ColumnsLabeling[y] ? originalG.get_m_WeightMatrix()[x][y] : 0);
            }
        }

        return labelingG;
    }

    //Sets the initial labeling to this object based on the input matrix to be labeled.
    Matrix InitialLabeling(Matrix in_MatrixToLabel, Matrix originalG, Matrix labelingG) {
        m_RowsLabeling = new int[in_MatrixToLabel.get_m_WeightMatrix().length];
        m_ColumnsLabeling = new int[in_MatrixToLabel.get_m_WeightMatrix().length];
        for (int x = 0; x < in_MatrixToLabel.getSize(); x++) {
            int iActualMaxX = 0;
            for (int y = 0; y < in_MatrixToLabel.getSize(); y++) {
                iActualMaxX = GetMax(iActualMaxX, in_MatrixToLabel.m_WeightMatrix[x][y]);
            }
            m_RowsLabeling[x] = iActualMaxX; //Stores it at the position of the X row.
            m_ColumnsLabeling[x] = 0;//Initially, all the labels for columns (Y) are at 0's.
        }
        for (int x = 0; x < originalG.get_m_WeightMatrix().length; x++) {
            for (int y = 0; y < originalG.get_m_WeightMatrix().length; y++) {
                labelingG.get_m_WeightMatrix()[x][y]
                        = (originalG.get_m_WeightMatrix()[x][y] >= m_RowsLabeling[x]
                        ? originalG.get_m_WeightMatrix()[x][y] : 0);
            }
        }

        return labelingG;
    }

    int GetMax(int in_a, int in_b) {
        return in_a > in_b ? in_a : in_b;
    }

    int GetMin(int in_a, int in_b) {
        return in_a < in_b ? in_a : in_b;
    }
}
