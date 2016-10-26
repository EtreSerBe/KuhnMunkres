/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kuhnmunkres;

/**
 *
 * @author juancarlosroot
 */
class Matrix {

    int m_WeightMatrix[][];

    public Matrix() {
    }

    public Matrix(int in_pArrayOfWeights[][]) {
        this.m_WeightMatrix = new int[in_pArrayOfWeights.length][in_pArrayOfWeights.length];
        for (int x = 0; x < in_pArrayOfWeights.length; x++) {
            for (int y = 0; y < in_pArrayOfWeights.length; y++) {
               this.m_WeightMatrix[x][y] = in_pArrayOfWeights[x][y]; 
            }            
        }
        
    }

    private void SetWeights(int in_pArrayOfWeights[][]) {
        for (int x = 0; x < in_pArrayOfWeights.length; x++) {
            for (int y = 0; y < in_pArrayOfWeights.length; y++) {
                this.m_WeightMatrix[x][y] = in_pArrayOfWeights[x][y];
            }
        }
    }

    public int getSize() {
        return this.m_WeightMatrix.length;
    }

    public int[][] get_m_WeightMatrix() {
        return this.m_WeightMatrix;
    }

}
