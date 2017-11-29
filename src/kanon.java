import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;

class Dataset implements Comparable<Dataset>
{
	private int QID;
	private int sensitiveAttribute;
	
	public Dataset()
	{
		QID = 0;
		sensitiveAttribute = 0;
	}
	
	public Dataset(int QID, int sensitiveAttribute)
	{
		this.QID = QID;
		this.sensitiveAttribute = sensitiveAttribute;
	}
	
	public int getQID() { return QID; }
	
	public int getSensitiveAttribute() { return sensitiveAttribute; }
	
	public void setQID(int QID) { this.QID = QID; }
	
	@Override
	public int compareTo(Dataset o)
	{
		if (QID > o.QID)
			return 1;
		else if (QID == o.QID)
			return 0;
		else
			return -1;
	}
	
	@Override
	public String toString()
	{
		return QID + "," + sensitiveAttribute;
	}
}

public class kanon 
{
	private ArrayList<Dataset> input;
	private int dpTable[][];
	private ArrayList<ArrayList<Dataset>> setTable[][];
	
	public kanon(String fileName, int anonymity)
	{
		input = new ArrayList<Dataset>();
		File sourceFile = new File(fileName);
		inputDataFile(sourceFile);
		int width = (int)Math.floor(input.size() / anonymity); 
		dpTable = new int[input.size()][width];
		setTable = new ArrayList[input.size()][width];
	}
	
	private void inputDataFile(File sourceFile)
	{
		try
		(
			Scanner sc = new Scanner(sourceFile);
		)
		{
			while (sc.hasNext())
			{
				String[] data = sc.nextLine().split(",");
				Dataset newData = new Dataset(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
				input.add(newData);
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private ArrayList<Dataset> cloneAndSort()
	{
		ArrayList<Dataset> clonedSet = new ArrayList<Dataset>();
		for (Dataset data : input)
			clonedSet.add(data);
		Collections.sort(clonedSet);
		return clonedSet;
	}
	
	private int medianOfDataset(ArrayList<Dataset> d) 
	{	
		// perform sorting on d first
		Collections.sort(d);
		// find out the median
		if (d.size() % 2 == 0)
		{
			int half = d.size() / 2;
			return (int)((d.get(half - 1).getQID() + d.get(half).getQID()) / 2) + 1;
		}
		else
		{
			int half = (d.size() + 1) / 2;
			return d.get(half - 1).getQID();
		}
	}
	
	private int changeOfDataset(ArrayList<Dataset> d, int median)
	{
		int change = 0;
		for (Dataset data : d)
			change += Math.abs(data.getQID() - median);
		return change;
	}
	
	private int min(ArrayList<Integer> d)
	{
		int min = d.get(0);
		for (Integer integer : d)
		{
			if (integer < min)
				min = integer;
		}
		return min;
	}
	
	private void printDPTable()
	{
		System.out.println("Dynamic Programming Table of the input file : ");
		for (int i = 0; i < dpTable.length; i++)
		{
			for (int j = 0; j < dpTable[0].length; j++)
			{
				System.out.print(dpTable[i][j] + "   ");
			}
			System.out.println();
		}
	}
	
	private void printSetTable()
	{
		System.out.println("Set Table of the input file : " + setTable.length + " " + setTable[0].length);
		for (int j = 0; j < setTable[0].length; j++)
		{
			for (int i = 0; i < setTable.length; i++)
			{
				System.out.print("i: " + (i + 1) + " j: " + (j + 1) + " [");
				for (ArrayList<Dataset> print : setTable[i][j])
				{
					System.out.print("[");
					for (int l = 0; l < print.size(); l++)
					{
						if (l == 0)
							System.out.print(print.get(l).getQID());
						else
							System.out.print(", " + print.get(l).getQID());
					}
					if (print == setTable[i][j].get(setTable[i][j].size() - 1))
						System.out.print("]");
					else
						System.out.print("], ");
				}
				System.out.println("]");
			}
			System.out.println();
		}
	}
	
	private void constructDPTable(ArrayList<Dataset> d, int anonymity)
	{
		// x-coor, width of dpTable
		for (int i = 0; i < dpTable[0].length; i++)
		{
			// y-coor, height of dpTable
			for (int j = 0; j < dpTable.length; j++)
			{
				// No need to compute DP for those lower than value of kanonymity
				if (j < anonymity - 1)
				{
					dpTable[j][i] = -1;
					setTable[j][i] = new ArrayList<ArrayList<Dataset>>();
				}
				else
				{
					if (i == 0) // C(i, 1)
					{
						// Get the data to process
						ArrayList<Dataset> tempSet = new ArrayList<Dataset>();
						for (int k = 0; k <= j; k++)
							tempSet.add(d.get(k));
						// Get the median of the dataset
						int median = medianOfDataset(tempSet);
						// Get the minimal change of the dataset
						dpTable[j][i] = changeOfDataset(tempSet, median);
						// Store the set into set table
						setTable[j][i] = new ArrayList<ArrayList<Dataset>>();
						setTable[j][i].add(tempSet);
					}
					else // C(i, 2) or C(i, 3)
					{
						// No of element smaller than double of k-anonymity
						if (j + 1 < (anonymity * 2))
						{
							// Just equal to last calculation
							dpTable[j][i] = dpTable[j][i - 1];  
							setTable[j][i] = setTable[j][i - 1];
						}
						else
						{
							ArrayList<Integer> tempSet = new ArrayList<Integer>();
							// 3D arraylist to store all dynamic programming done
							ArrayList<ArrayList<ArrayList<Dataset>>> tempSetTable = new ArrayList<ArrayList<ArrayList<Dataset>>>();
							
							// Put all elements into all previous set
							tempSet.add(dpTable[j][i - 1]);
							tempSetTable.add(setTable[j][i - 1]);
							
							// Consider all possibilities that can split sets
							for (int k = anonymity; k <= j + 1 - anonymity; k++)
							{
								ArrayList<Dataset> temp = new ArrayList<Dataset>();
								for (int l = k; l <= j; l++)
									temp.add(d.get(l));
								// C(i - ?, 1) + A(i - ?)
								tempSet.add(dpTable[k - 1][i - 1] + changeOfDataset(temp, medianOfDataset(temp)));
								
								// Deep copy for the current dynamic programming set
								ArrayList<ArrayList<Dataset>> t = new ArrayList<ArrayList<Dataset>>();
								for (int m = 0; m < setTable[k - 1][i - 1].size(); m++)
								{
									ArrayList<Dataset> ds = setTable[k - 1][i - 1].get(m);
									ArrayList<Dataset> nds = new ArrayList<Dataset>();
									for (Dataset copy : ds)
										nds.add(copy);
									t.add(nds);
								}
								t.add(temp);
								tempSetTable.add(t);
							}
						
							// Get the minimum change of the set of datasets
							dpTable[j][i] = min(tempSet);
							
							// Find out the minimum datasets
							for (int m = 0; m < tempSet.size(); m++)
								if (tempSet.get(m) == dpTable[j][i])
									setTable[j][i] = tempSetTable.get(m);
						}
					}
				}
			}
		}
	}
	
	private void printInput()
	{
		for (Dataset print : input)
			System.out.println(print);
	}
	
	private void outputToFile(String filename)
	{
		ArrayList<ArrayList<Dataset>> finalSet = setTable[setTable.length - 1][setTable[0].length - 1];
		// Changing subset by subset
		for (ArrayList<Dataset> subset : finalSet)
		{
			int noOfElementToChange = subset.size();
			int median = medianOfDataset(subset);
			// search through input to swap with median
			for (Dataset change : input)
			{
				if (noOfElementToChange == 0)
					break;
				else
				{
					for (Dataset elements : subset)
					{
						if (elements.getQID() == change.getQID())
						{
							change.setQID(median);
							noOfElementToChange--;
							break;
						}
					}
				}
			}
		}
		// overwrite the whole file
		File file = new File(filename);
		try
		(
			PrintWriter writer = new PrintWriter(file);
		)
		{
			for (Dataset write : input)
			{
				writer.print(write);
				if (write != input.get(input.size() - 1))
				{
					writer.print("\n");
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static void main(String[] args)
	{
		kanon dataset = new kanon(args[0], 4);
		dataset.printInput();
		ArrayList<Dataset> clonedSet = dataset.cloneAndSort();
		dataset.constructDPTable(clonedSet, 4);
		dataset.printDPTable();
		dataset.printSetTable();
		dataset.outputToFile(args[0]);
		System.out.println("Data After: ");
		dataset.printInput();
	}
}
