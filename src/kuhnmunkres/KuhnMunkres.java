/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kuhnmunkres;

/**
 *
 * @author Adrian Gonzalez
 * @author juancarlosroot
 */

import java.util.Stack;
import java.util.ArrayList;
public class KuhnMunkres {

    GlobalVariables mGlobalVariables;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        KuhnMunkres km = new KuhnMunkres();
        km.init();
    }

    public void init() {

        mGlobalVariables = new GlobalVariables();
        int[][] m_matrix = {
            {1, 6, 4, 1, 5},
            {3, 2, 6, 0, 0},
            {8, 0, 7, 4, 4},
            {1, 1, 1, 1, 1},
            {9, 8, 5, 1, 1}
        };

        mGlobalVariables.setOriginalG(new Matrix(m_matrix));
        mGlobalVariables.setLabelingG(new Matrix(m_matrix));
        mGlobalVariables.setmLabeling(new Labeling());
        mGlobalVariables.setmAumenting(new int[mGlobalVariables.getOriginalG().get_m_WeightMatrix().length]);

        mGlobalVariables.setLabelingG(
                mGlobalVariables.getmLabeling().InitialLabeling(
                        mGlobalVariables.getOriginalG(),
                        mGlobalVariables.getOriginalG(),
                        mGlobalVariables.labelingG
                )
        );

        Is_X_Saturated_Feo(); //Added so it creates a first try of a perfect matching.
        mainAlgorithm1();
    }

    private void mainAlgorithm1() {
        while (mGlobalVariables.getGlobalPosition() != 4) {
            switch (mGlobalVariables.getGlobalPosition()) {
                case 1:
                    System.out.println("*********** PASO 1 ******");
                    mainAlgorithm1_1();
                    break;
                case 2:
                    System.out.println("*********** PASO 2 ******");
                    mainAlgorithm1_2();
                    break;
                case 3:
                    System.out.println("*********** PASO 3 ******");
                    mainAlgorithm1_3();
                    break;
            }
        }
    }

    private void mainAlgorithm1_1() {
        if (!Is_X_Saturated_Simple()) {
            //Is_X_Saturated_Feo(); //Added so it creates a first try of a perfect matching.
            mGlobalVariables.getM_i_S().clear();
            mGlobalVariables.getM_i_T().clear();
            mGlobalVariables.getM_i_S_Neighbors().clear();
            mGlobalVariables.getM_i_S().add(mGlobalVariables.getmActualMatching().indexOf(-1));
            //get_Neighbors();
            mGlobalVariables.setGlobalPosition(2);
        } else {
            System.out.println("Matching perfecto encontrado");
            for (int i = 0; i < mGlobalVariables.getmActualMatching().size(); i++) {
                System.out.println("Vertex X= " + i + "Matches Y= " + mGlobalVariables.getmActualMatching().get(i));
            }
            mGlobalVariables.setGlobalPosition(4);
        }
    }

    private void mainAlgorithm1_2() {
        if (!Is_T_ASubsetOfNeighbors()) {
            mGlobalVariables.getmLabeling().UpdateLabeling(
                    mGlobalVariables.getM_i_S(),
                    mGlobalVariables.getM_i_T(),
                    mGlobalVariables.getOriginalG(),
                    mGlobalVariables.getLabelingG()
            );
            //Aquí nos había faltado llamar de nuevo a los vecinos con el nuevo labeling que se hizo.
            get_Neighbors();

            mGlobalVariables.setGlobalPosition(3);
        } else {
            mGlobalVariables.setGlobalPosition(3);
            return;
        }
    }

    private void mainAlgorithm1_3() {
        for (int y : mGlobalVariables.getM_i_S_Neighbors()) {
            if (mGlobalVariables.getM_i_T().contains(y)) {
                continue;
            } else {
                if (mGlobalVariables.getmActualMatching().contains(y))//Saturated
                {
                    System.out.println("mArrayList.indexOf(y)" + mGlobalVariables.getmActualMatching().indexOf(y));
                    mGlobalVariables.getM_i_S().add(mGlobalVariables.getmActualMatching().indexOf(y));
                    mGlobalVariables.getM_i_T().add(y);
                    //Then, you should go to step 2 of the algorithm again.
                    mGlobalVariables.setGlobalPosition(2);
                    return;
                } else {
                    if (SearchAugmentingPath(y))//if successful; simmetric difference
                    {
                        System.out.println("camino mAumented");
                        SimmetricDifference();
                        mGlobalVariables.setGlobalPosition(1);
                        return;
                    } else //otherwise, no solution
                    {
                        System.out.println("no");
                        mGlobalVariables.setGlobalPosition(4);
                        return;
                    }
                }
            }

        }
        
        System.out.println("no neighbors, so not possible, ending this X");
        for(int s : mGlobalVariables.getM_i_S())
        {
            if(mGlobalVariables.getmActualMatching().get(s) == -1)
                mGlobalVariables.getmActualMatching().set(s, -2);
                       
        }
         mGlobalVariables.setGlobalPosition(1);
       
         return;
    }

    public void Is_X_Saturated_Feo() {
        //I'm trying to make it start with just one saturated edge., not any more.
        mGlobalVariables.setmActualMatching(new ArrayList<Integer>());

        boolean bHasAssigned = false;
        int iHighestValue = -100000;
        int iHighestY = -1;
        int iHighestX = -1;

        for (int x = 0; x < mGlobalVariables.getLabelingG().get_m_WeightMatrix().length; x++) {
            for (int y = 0; y < mGlobalVariables.getLabelingG().get_m_WeightMatrix().length && bHasAssigned == false; y++) {
                if (mGlobalVariables.getLabelingG().get_m_WeightMatrix()[x][y] != 0 ) {
                    iHighestValue = mGlobalVariables.getLabelingG().get_m_WeightMatrix()[x][y];
                    iHighestY = y;
                    iHighestX = x;
                    x=1000;
                    break;
                }
            }
        }

        for (int i = 0; i < mGlobalVariables.getLabelingG().get_m_WeightMatrix().length; i++) {
            if (mGlobalVariables.getmActualMatching().size() == iHighestX) {
                mGlobalVariables.getmActualMatching().add(iHighestY);
                continue;
            }
            mGlobalVariables.getmActualMatching().add(-1);
        }

        System.out.println("Calling Is_X_Saturated_Feo");
    }

    private void SimmetricDifference() {
        ArrayList<Integer> mPrime = new ArrayList<Integer>();

        for (int i = 0; i < mGlobalVariables.getmAumenting().length; i++) {
            if (mGlobalVariables.getmAumenting()[i] > -1) {
                int var
                        = mGlobalVariables.
                                getmActualMatching().
                                indexOf(mGlobalVariables.
                                        getmAumenting()[i]);
                if (var != -1) {
                    mGlobalVariables.getmActualMatching().set(var, mGlobalVariables.getmAumenting()[var]);
                } else {
                    mGlobalVariables.getmActualMatching().set(i, mGlobalVariables.getmAumenting()[i]);
                }
            }

        }
    }

    private int SearchColumns(int y, int start_x) {
        System.out.println(" Searching Columns from Y = " + y);
       // start_x += (start_x != 0 ? 1 : 0);
        System.out.println(" Starting from X = " + start_x);
        for (int x = 0 + start_x; x < mGlobalVariables.getLabelingG().get_m_WeightMatrix().length; x++) {
            if (mGlobalVariables.getLabelingG().get_m_WeightMatrix()[x][y /*This 'y' should be the one being searched now.*/] != 0) {
                //If that vertex is not already on the path we are making
                if (!(mGlobalVariables.getM_iAugmentingPathY().search(x) > 0)) {
                    //If this X vertex is not saturated, we win, this is an augmenting path.
                    if (mGlobalVariables.getmActualMatching().get(x) == -1) {
                        //If we get here, it means we have our augmenting path.
                        mGlobalVariables.getM_iAugmentingPathY().push(x);
                        return 2;
                    } else {
                        //Else, this X vertex is already saturated and we can go further in the tree to
                        //see if some deeper route could get to an Augmenting path.
                        System.out.println("A non saturated X= " + x + " was found, going deeper.");
                        mGlobalVariables.getM_iAugmentingPathY().push(x);
                        return 1;
                        //Then we must now search based on the rows, not the columns
                    }
                }
            }
        }
        if ((!mGlobalVariables.getM_iAugmentingPathX().empty()) && !mGlobalVariables.getM_iAugmentingPathY().empty()) {
            System.out.println("This Y = " + y + " Didn't find a new X to go deeper, going 2 steps back. (1x and 1 y)");
            //If it entered here, there's still hope.
            return 3;
        } else {
            return 0;// no solution FAIL
        }

    }

    private boolean SearchRows(int x) {
        System.out.println("Search rows using X = " + x);
        //If that vertex is not already on the path we are making
        if (!(mGlobalVariables.getM_iAugmentingPathX().search(mGlobalVariables.getmActualMatching().get(x)) > 0)) {
            //If it is already contained in the matching in that position, then that (x,y)-edge is saturated.
            //Then, we add it to the path and try to go deeper into the tree.
            mGlobalVariables.getM_iAugmentingPathX().push(mGlobalVariables.getmActualMatching().get(x));
            System.out.println("Going through: X = " + x + " and Y = " + mGlobalVariables.getmActualMatching().get(x));
            return true;
        }
        System.out.println("This X = " + x + " Couldn't go further, it's Y was already on the tree.");
        return false;// no solution
    }

    private boolean SearchAugmentingPath(int in_UnsaturedY) {
        System.out.println("*********** Enter  SearchAugmentingPath ******");
        //This 'y' vertex is unsaturated. Search a way from here to the Unsaturated 'X' vertex.
        mGlobalVariables.getM_iAugmentingPathX().clear();
        mGlobalVariables.getM_iAugmentingPathY().clear();
        //Search, on the neighbors (on LabelingG) of this y vertex.
        mGlobalVariables.getM_iAugmentingPathX().push(in_UnsaturedY);
        boolean bPartingFromY = true;
        int x_pop = 0;
        while (true) {
            if (bPartingFromY == true) {
                //Then, use the Y_FOR
                int result = SearchColumns(mGlobalVariables.getM_iAugmentingPathX().peek(), x_pop);
                x_pop = 0;
                if (result == 2) {
                    System.out.println("Camino M aumentante encontrado");
                    mGlobalVariables.setmAumenting(new int[mGlobalVariables.getOriginalG().get_m_WeightMatrix().length]);
                    for (int i = 0; i < mGlobalVariables.getOriginalG().get_m_WeightMatrix().length; i++) {
                        mGlobalVariables.getmAumenting()[i] = -1;
                    }

                    while (!(mGlobalVariables.getM_iAugmentingPathX().empty())) {
                        int y_pop = mGlobalVariables.getM_iAugmentingPathY().pop();//Esta es la fila
                        x_pop = mGlobalVariables.getM_iAugmentingPathX().pop();//Esta es la columna
                        //mGlobalVariables.getmAumenting() = new int[mGlobalVariables.getOriginalG().get_m_WeightMatrix().length];
                        mGlobalVariables.getmAumenting()[y_pop] = x_pop;
                    }
                    return true;
                } else if (result == 1) {
                    bPartingFromY = false;
                    continue;
                } else if (result == 0) {
                    System.out.println("No hay solución, no hay camino M aumentante");
                    return false;
                } else //In case it is Result = 3
                {
                    mGlobalVariables.getM_iAugmentingPathX().pop(); //This is completely disposable.
                    x_pop = mGlobalVariables.getM_iAugmentingPathY().pop(); //This x value should be used to continue the iteration of the y below on the stack. 
                    x_pop++;
                    continue;
                }
            } else {
                if (!SearchRows(mGlobalVariables.getM_iAugmentingPathY().peek())) {
                    x_pop = mGlobalVariables.getM_iAugmentingPathY().pop();
                    x_pop++;
                }
                bPartingFromY = true;
            }
        }
    }

    public boolean Is_X_Saturated() {
        Stack<Integer> matching_stack = new Stack<Integer>();
        Stack<Integer> matching_stack_great = new Stack<Integer>();

        int x = 0;
        int y = 0;
        while (x < mGlobalVariables.getOriginalG().get_m_WeightMatrix().length && x >= 0) {
            int x_actual = x;

            for (; y < mGlobalVariables.getOriginalG().get_m_WeightMatrix().length; y++) {
                if (mGlobalVariables.getLabelingG().get_m_WeightMatrix()[x][y] != 0) {
                    if (!(matching_stack.search(y) > 0)) {
                        matching_stack.push(y);
                        y = 0;
                        x++;
                        System.out.println(x);
                        break;
                    }
                }
            }
            if (x_actual == x) {
                if (!matching_stack.empty()) {
                    y = matching_stack.pop();
                } else {
                    break;
                }
                y++;
                x--;
            } else {
                if (matching_stack.size() > matching_stack_great.size()) {
                    matching_stack_great = matching_stack;
                }
            }
        }

        if (matching_stack_great.size() == mGlobalVariables.getOriginalG().get_m_WeightMatrix().length) {
            for (int i = 0; i < mGlobalVariables.getmActualMatching().size(); i++) {
                System.out.println("Vertex X= " + i + "Matches Y= " + matching_stack_great.get(i));
            }
            return true;
        }

        return false;
    }

    public boolean Is_X_Saturated_Simple() {
        return !mGlobalVariables.getmActualMatching().contains(-1);
    }

    //Function to check if the algorithm should go to step 2 or step 3, depending of the Sets S and T.
    boolean Is_T_ASubsetOfNeighbors() {
        //Check to see if all the elements inside Set T is contained in the Neighbors of S.
        get_Neighbors();
        //If the element in set T is not a member of the set of Neighbors of S, then,
        //return false, and the algorithm should go to UPDATE THE LABELING
        //Then, Set T is a Subset of the Neighbors of set S
        //The algorithm should proceed to STEP 3!.
        return !mGlobalVariables.getM_i_T().containsAll(mGlobalVariables.getM_i_S_Neighbors());//1ra vez regresa falso porque [m_i_t] está vacía
    }

    private void get_Neighbors() {
        for (int s : mGlobalVariables.getM_i_S()) {
            for (int y = 0; y < mGlobalVariables.getOriginalG().get_m_WeightMatrix().length; y++) {
                if (mGlobalVariables.getLabelingG().get_m_WeightMatrix()[s][y] != 0) {// Se envían a los vecinos que no contengan 0 [x]
                    mGlobalVariables.getM_i_S_Neighbors().add(y);
                }
            }
        }
    }
}

