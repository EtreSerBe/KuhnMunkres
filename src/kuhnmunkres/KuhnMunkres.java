/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kuhnmunkres;

/**
 *
 * @author Adrian Gonzalez
 */
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;
import java.util.ArrayList;

class KuhnMunkres
{
    Matrix originalG;//todos los pesos
    Matrix labelingG;//solo los mayores al labeling actual

    Labeling mLabeling;
    Set<Integer> m_i_S = new HashSet<Integer>();
    Set<Integer> m_i_T = new HashSet<Integer>();
    Set<Integer> m_i_S_Neighbors = new HashSet<Integer>();

    Stack<Integer> m_iAugmentingPathX = new Stack<Integer>();
    Stack<Integer> m_iAugmentingPathY = new Stack<Integer>();

    ArrayList<Integer> mArrayList =  new ArrayList<Integer>();

    int globalPosition = 1;

    int []mAumenting;
    
    public static void main(String args[])
    {
        KuhnMunkres km = new KuhnMunkres();
        km.init();
    }

    public KuhnMunkres()
    {}

    public void init()
    {

		        int [][]m_matrix = {
        {250,400,350},
        {400,600,350},
        {200,400,250}
        };
        
        int [][]m_Auxiliarmatrix = {
       {250,400,350},
        {400,600,350},
        {200,400,250}
        };
	
		


        this.originalG = new Matrix(m_matrix);
        this.labelingG = new Matrix(m_Auxiliarmatrix);
        this.mLabeling = new Labeling();
        this.mAumenting = new int[originalG.get_m_WeightMatrix().length];

        this.mLabeling.InitialLabeling(this.originalG);

        Is_X_Saturated_Feo(); //Added so it creates a first try of a perfect matching.

        
        
        mainAlgorithm1();
    }

    private void mainAlgorithm1()
    {
        while(globalPosition != 4)
        {
            switch(globalPosition)
            {
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

    private void mainAlgorithm1_1()
    {
        if(!Is_X_Saturated_Simple())
        {
            //Is_X_Saturated_Feo(); //Added so it creates a first try of a perfect matching.
            m_i_S.clear();
            m_i_T.clear();
            m_i_S_Neighbors.clear();
            m_i_S.add(mArrayList.indexOf(-1));
            //get_Neighbors();
            globalPosition = 2;
        }
        else
        {
            System.out.println("Matching perfecto encontrado");
            
            for( int i = 0; i < mArrayList.size(); i++ )
            {
                System.out.println("Vertex X= " + i + "Matches Y= " + mArrayList.get(i) );
            }
            globalPosition = 4;
        }    
    }

    private void mainAlgorithm1_2()
    {
        if(!Is_T_ASubsetOfNeighbors())
        {
            this.mLabeling.UpdateLabeling();
            //Aquí nos había faltado llamar de nuevo a los vecinos con el nuevo labeling que se hizo.
            get_Neighbors();
            
            globalPosition = 3;
        }    
        else
        {
            globalPosition = 3;
            return;
        }            
    }

    private void mainAlgorithm1_3()
    {
                for (int y : m_i_S_Neighbors)
                {
                        if(m_i_T.contains(y))
                            continue;
                        else
                        {
                            if(mArrayList.contains(y))//Saturated
                            {
                                System.out.println("mArrayList.indexOf(y)" + mArrayList.indexOf(y));
                                m_i_S.add(mArrayList.indexOf(y));
                                m_i_T.add(y);
								//Then, you should go to step 2 of the algorithm again.
                                globalPosition = 2;
                                return;
                            }
                            else
                            {
                                if(SearchAugmentingPath(y))//if successful; simmetric difference
                                {
                                    System.out.println("camino mAumented");
                                    SimmetricDifference();
                                    globalPosition = 1;
                                    return;
                                }   
                                else                     //otherwise, no solution
                                {    
                                    System.out.println("no");
                                    globalPosition = 4;
                                    return;
                                }
                            }
                        }
                        
                }     
    }

    private void SimmetricDifference()
    {
        ArrayList<Integer> mPrime = new ArrayList<Integer>();

        for(int i = 0 ; i < mAumenting.length ; i++)
        {
            if( mAumenting[i] > -1)
            {
                int var = mArrayList.indexOf(mAumenting[i]);
                if( var != -1)
                {
                    mArrayList.set(var, mAumenting[var]);
                }
                else
                {
                    mArrayList.set(i, mAumenting[i]);
                }
            }
                
        }
    }
	
    private int SearchColumns(int y, int start_x)
    {
        System.out.println(" Searching Columns from Y = " +  y );
        
        start_x += (start_x!=0?1:0);
        
        System.out.println( " Starting from X = " + start_x );

		for( int x = 0 + start_x; x < labelingG.get_m_WeightMatrix().length; x++ )
		{
                    if( labelingG.get_m_WeightMatrix()[x][y /*This 'y' should be the one being searched now.*/] != 0 )
                    {
			//If that vertex is not already on the path we are making
			if( !(m_iAugmentingPathY.search(x) > 0)  )
			{
                            //If this X vertex is not saturated, we win, this is an augmenting path.
                            if( mArrayList.get(x) == -1 )
                            {
                                //If we get here, it means we have our augmenting path.
                                m_iAugmentingPathY.push(x);
                                return 2;
                            }
                            else
                            {
				//Else, this X vertex is already saturated and we can go further in the tree to
				//see if some deeper route could get to an Augmenting path.
				System.out.println("A non saturated X= " + x + " was found, going deeper.");
				m_iAugmentingPathY.push(x);
                                return 1;
						//Then we must now search based on the rows, not the columns
                            }
			}
                    }
		}
        if((!m_iAugmentingPathX.empty())  && !m_iAugmentingPathY.empty() )
        {
           System.out.println( "This Y = " + y + " Didn't find a new X to go deeper, going 2 steps back. (1x and 1 y)" );
            //If it entered here, there's still hope.
            return 3;
        }
        else
        {
                return 0;// no solution FAIL
        }
            
    }

    private boolean SearchRows(int x)
    {
        System.out.println("Search rows using X = " + x );
        //If that vertex is not already on the path we are making
	if( !(m_iAugmentingPathX.search(mArrayList.get(x)) > 0)  )
	{
            
		    //If it is already contained in the matching in that position, then that (x,y)-edge is saturated.
						//Then, we add it to the path and try to go deeper into the tree.
            m_iAugmentingPathX.push(
                mArrayList.get(x));
            
            System.out.println("Going through: X = " + x + " and Y = " + mArrayList.get(x));
                 
            return true;
	}
        
        
        System.out.println("This X = " + x + " Couldn't go further, it's Y was already on the tree.");
        

        return false;// no solution
    }
	
	private boolean SearchAugmentingPath( int in_UnsaturedY )
	{
            
            System.out.println("*********** Enter  SearchAugmentingPath ******");
		//This 'y' vertex is unsaturated. Search a way from here to the Unsaturated 'X' vertex.
            m_iAugmentingPathX.clear();
            m_iAugmentingPathY.clear();
		
            //Search, on the neighbors (on LabelingG) of this y vertex.
            m_iAugmentingPathX.push(in_UnsaturedY);
            boolean bPartingFromY = true;
		
            int x_pop = 0;
		while( true )
		{   
                    if( bPartingFromY == true )
                    {
			//Then, use the Y_FOR
                       int result = SearchColumns(m_iAugmentingPathX.peek(), x_pop);
                       x_pop = 0;
                       if( result == 2)
                       {
                            System.out.println("Camino M aumentante encontrado");
                            mAumenting = new int[originalG.get_m_WeightMatrix().length];
                            for(int i = 0; i < originalG.get_m_WeightMatrix().length; i++)
                            {
                                mAumenting[i] = -1;
                            }
                            

                            while( !(m_iAugmentingPathX.empty()) )
                            {
                                int y_pop = m_iAugmentingPathY.pop();//Esta es la fila
                                x_pop = m_iAugmentingPathX.pop();//Esta es la columna
                                //mAumenting = new int[originalG.get_m_WeightMatrix().length];
                                mAumenting[y_pop] = x_pop;
                            }
                            return true;
                        }
                        else if(result == 1)
                        {
                            bPartingFromY = false;
                            continue;
                        }
                        else if(result == 0)
                        {
                            System.out.println("No hay solución, no hay camino M aumentante");
                            return false;
                        }
                        else //In case it is Result = 3
                        {
                             m_iAugmentingPathX.pop(); //This is completely disposable.
                             x_pop = m_iAugmentingPathY.pop(); //This x value should be used to continue the iteration of the y below on the stack. 
                            x_pop++;
                             continue;
                        }
                    }
                    else
                    {
                        if(!SearchRows(m_iAugmentingPathY.peek()))
                        {
                            x_pop = m_iAugmentingPathY.pop();
                            x_pop++;
                        }
                        bPartingFromY = true;
                    }
                }
            }

    public void Is_X_Saturated_Feo()
    {
        //I'm trying to make it start with just one saturated edge., not any more.
        mArrayList = 
            new ArrayList<Integer>();
        
        boolean bHasAssigned = false;
        int iHighestValue = -100000;
        int iHighestY = -1;
        int iHighestX = -1;
        for(int x = 0; x < labelingG.get_m_WeightMatrix().length; x++)
        {            
            for(int y = 0; y < labelingG.get_m_WeightMatrix().length && bHasAssigned == false; y++)
            {
                if(labelingG.get_m_WeightMatrix()[x][y] != 0  && labelingG.get_m_WeightMatrix()[x][y] > iHighestValue )
                {
                    iHighestValue = labelingG.get_m_WeightMatrix()[x][y];
                    iHighestY = y;
                    iHighestX = x;
                }
            }
        }			
        
        for(int i = 0; i < labelingG.get_m_WeightMatrix().length; i++ )
        {
            if(mArrayList.size() == iHighestX)
            {
                mArrayList.add(iHighestY);
                continue;
            }
            mArrayList.add(-1);
        }
        
		System.out.println("Calling Is_X_Saturated_Feo");	
    }

    public boolean Is_X_Saturated()
    {
        Stack<Integer> matching_stack = new Stack<Integer>();
        Stack<Integer> matching_stack_great = new Stack<Integer>();

        int x = 0;
        int y = 0;
        while(x < originalG.get_m_WeightMatrix().length && x >= 0)
        {
            int x_actual = x;
            
            for(; y < originalG.get_m_WeightMatrix().length ; y++)
            {
                if(labelingG.get_m_WeightMatrix()[x][y] != 0)
                {
                    if( !(matching_stack.search(y) > 0 ))
                    {
                        matching_stack.push(y);
                        y = 0;
                        x++;
                        System.out.println(x);
                        break;
                    }
                }
            }

            if(x_actual == x)
            {
                if(!matching_stack.empty())
                    y = matching_stack.pop();
                else
                    break;
                y++;
                x--;
            }
            else 
            {
                if(matching_stack.size() > matching_stack_great.size() )
                    matching_stack_great = matching_stack;
            }
        }
        
        if( matching_stack_great.size() == originalG.get_m_WeightMatrix().length )
        {
            for( int i = 0; i < mArrayList.size(); i++ )
            {
                System.out.println("Vertex X= " + i + "Matches Y= " + matching_stack_great.get(i) );
            }
            return true;
        }
        
        return false;     
    }

    
    
    public boolean Is_X_Saturated_Simple()
    {

        return !mArrayList.contains(-1);
    }
    
	//Function to check if the algorithm should go to step 2 or step 3, depending of the Sets S and T.
	boolean Is_T_ASubsetOfNeighbors()
	{
		//Check to see if all the elements inside Set T is contained in the Neighbors of S.
		
            get_Neighbors();
        //If the element in set T is not a member of the set of Neighbors of S, then,
        //return false, and the algorithm should go to UPDATE THE LABELING
        //Then, Set T is a Subset of the Neighbors of set S
        //The algorithm should proceed to STEP 3!.
            return !m_i_T.containsAll(m_i_S_Neighbors);
	}

    private void get_Neighbors()
    {
        for(int s : m_i_S)
            for(int y = 0; y < originalG.get_m_WeightMatrix().length; y++)
            {
                if(labelingG.get_m_WeightMatrix()[s][y] != 0)
                    m_i_S_Neighbors.add(y);
            }
    }

    int GetMax(int in_a, int in_b)
    {
        return in_a > in_b ? in_a : in_b;
    }

    int GetMin(int in_a, int in_b)
    {
        return in_a < in_b ? in_a : in_b;
    }

    class Labeling
    {
        public Labeling(){};

        int []m_RowsLabeling; //The label of the X vertices., The ones on the right of the rows.
        int []m_ColumnsLabeling; //The label of the Y vertices, the ones on the lower part of the columns.
        int m_alpha = 0;
        int CalculateAlpha( )  /*Set S of the algorithm*/ /*, Set T of the algorithm*/ 
        {
            System.out.println("1 Calculate alpha");
            m_alpha = 100000; //Set the lowest to alpha, so anything is lower than that.
            for (int x : m_i_S )//Changed to a Foreach
            {
                for (int y = 0 ; y < originalG.get_m_WeightMatrix().length; y++)
                    if(m_i_T.contains(y))
                        continue;
                    else
                    {
			int iTemp =  m_RowsLabeling[x] + m_ColumnsLabeling[y] - originalG.get_m_WeightMatrix()[x][y] ; //This was WRONG XD
						
			if(iTemp <= 0)
			{	
                            continue; 
			}
					
			System.out.println("iTemp, from the aplha, is: " + iTemp);
			m_alpha = GetMin( m_alpha,   iTemp );
                    }
                        
            }
            System.out.println("2 Calculate alpha, the final alpha is: " + m_alpha);
            return m_alpha; //PLACE HOLDER!
        }

        void UpdateLabeling()
        {
            CalculateAlpha();
            for (int s : m_i_S)
            {
                System.out.println("Update labeling S " + s);
                m_RowsLabeling[s] -= m_alpha;   
            }
                    
            for (int t : m_i_T)
                m_ColumnsLabeling[t] += m_alpha;     

            for(int x = 0; x < originalG.get_m_WeightMatrix().length; x++)
            {
                for(int y = 0; y < originalG.get_m_WeightMatrix().length; y++)
                    labelingG.get_m_WeightMatrix()[x][y] = 
                        (originalG.get_m_WeightMatrix()[x][y] >= m_RowsLabeling[x]? originalG.get_m_WeightMatrix()[x][y] : 0);
            } 
        }

        //Sets the initial labeling to this object based on the input matrix to be labeled.
        void InitialLabeling(Matrix in_MatrixToLabel)
        {
            m_RowsLabeling = new int[in_MatrixToLabel.get_m_WeightMatrix().length];
            m_ColumnsLabeling = new int[in_MatrixToLabel.get_m_WeightMatrix().length];
            for (int x = 0; x < in_MatrixToLabel.getSize(); x++)
            {
                int iActualMaxX = 0;
                for (int y = 0; y < in_MatrixToLabel.getSize(); y++)
                {
                    iActualMaxX = GetMax( iActualMaxX, in_MatrixToLabel.m_WeightMatrix[x][y] );
                }
                m_RowsLabeling[x] = iActualMaxX; //Stores it at the position of the X row.
                m_ColumnsLabeling[x] = 0;//Initially, all the labels for columns (Y) are at 0's.
            }
            for(int x = 0; x < originalG.get_m_WeightMatrix().length; x++)
            {
                for(int y = 0; y < originalG.get_m_WeightMatrix().length; y++)
                    labelingG.get_m_WeightMatrix()[x][y] = 
                        (originalG.get_m_WeightMatrix()[x][y] >= m_RowsLabeling[x]? originalG.get_m_WeightMatrix()[x][y] : 0);
            } 
        }

    }    
    class Matrix
    {
        int m_WeightMatrix[][];
        
        public Matrix(){}

        public Matrix(int in_pArrayOfWeights[][]){
            this.m_WeightMatrix = in_pArrayOfWeights;
        }

        private void SetWeights( int in_pArrayOfWeights[][]  )
        {
            for (int x = 0; x < in_pArrayOfWeights.length; x++)
            {
                for (int y = 0; y < in_pArrayOfWeights.length; y++)
                {
                    this.m_WeightMatrix[x][y] = in_pArrayOfWeights[x][y];
                }
            }
        }

        public int getSize()
        {
            return this.m_WeightMatrix.length;
        }

        public int[][] get_m_WeightMatrix()
        {
            return this.m_WeightMatrix;
        }

            
    }
}

