/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kuhnmunkres;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 *
 * @author juancarlosroot
 */
public class GlobalVariables {
    Matrix originalG;//todos los pesos
    Matrix labelingG;//solo los mayores al labeling actual

    Labeling mLabeling;
    Set<Integer> m_i_S = new HashSet<Integer>();// x in clonfict
    Set<Integer> m_i_T = new HashSet<Integer>();// y in conflict
    Set<Integer> m_i_S_Neighbors = new HashSet<Integer>();

    Stack<Integer> m_iAugmentingPathX = new Stack<Integer>();
    Stack<Integer> m_iAugmentingPathY = new Stack<Integer>();

    ArrayList<Integer> mActualMatching = new ArrayList<Integer>(); // camino

    int globalPosition = 1;

    int[] mAumenting;   

    public Matrix getOriginalG() {
        return originalG;
    }

    public void setOriginalG(Matrix originalG) {
        this.originalG = originalG;
    }

    public Matrix getLabelingG() {
        return labelingG;
    }

    public void setLabelingG(Matrix labelingG) {
        this.labelingG = labelingG;
    }

    public Labeling getmLabeling() {
        return mLabeling;
    }

    public void setmLabeling(Labeling mLabeling) {
        this.mLabeling = mLabeling;
    }

    public Set<Integer> getM_i_S() {
        return m_i_S;
    }

    public void setM_i_S(Set<Integer> m_i_S) {
        this.m_i_S = m_i_S;
    }

    public Set<Integer> getM_i_T() {
        return m_i_T;
    }

    public void setM_i_T(Set<Integer> m_i_T) {
        this.m_i_T = m_i_T;
    }

    public Set<Integer> getM_i_S_Neighbors() {
        return m_i_S_Neighbors;
    }

    public void setM_i_S_Neighbors(Set<Integer> m_i_S_Neighbors) {
        this.m_i_S_Neighbors = m_i_S_Neighbors;
    }

    public Stack<Integer> getM_iAugmentingPathX() {
        return m_iAugmentingPathX;
    }

    public void setM_iAugmentingPathX(Stack<Integer> m_iAugmentingPathX) {
        this.m_iAugmentingPathX = m_iAugmentingPathX;
    }

    public Stack<Integer> getM_iAugmentingPathY() {
        return m_iAugmentingPathY;
    }

    public void setM_iAugmentingPathY(Stack<Integer> m_iAugmentingPathY) {
        this.m_iAugmentingPathY = m_iAugmentingPathY;
    }

    public ArrayList<Integer> getmActualMatching() {
        return mActualMatching;
    }

    public void setmActualMatching(ArrayList<Integer> mActualMatching) {
        this.mActualMatching = mActualMatching;
    }

    public int getGlobalPosition() {
        return globalPosition;
    }

    public void setGlobalPosition(int globalPosition) {
        this.globalPosition = globalPosition;
    }

    public int[] getmAumenting() {
        return mAumenting;
    }

    public void setmAumenting(int[] mAumenting) {
        this.mAumenting = mAumenting;
    }
    
    
}
