import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.File;

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
}

public class kanon 
{
	private ArrayList<Dataset> input;
	private int dpTable[][];
	
	public kanon(String fileName, int anonymity)
	{
		input = new ArrayList<Dataset>();
		File sourceFile = new File(fileName);
		inputDataFile(sourceFile);
		dpTable = new int[input.size()][anonymity];
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
			return (int)((d.get(half - 1).getQID() + d.get(half).getQID()) / 2);
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
		for (int i = 0; i < dpTable.length; i++)
		{
			for (int j = 0; j < dpTable[0].length; j++)
			{
				System.out.print(dpTable[i][j] + "   ");
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
					dpTable[j][i] = -1;
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
					}
					else // C(i, 2) or C(i, 3)
					{
						if (j + 1 < (anonymity * 2)) 			// no of element smaller than double of k-anonymity
							dpTable[j][i] = dpTable[j][i - 1];  // just equal to last calculation
						else
						{
							ArrayList<Integer> tempSet = new ArrayList<Integer>();
							// put all elements into all previous set
							tempSet.add(dpTable[j][i - 1]);
							// consider all possibilities that can split sets
							for (int k = anonymity; k <= j + 1 - anonymity; k++)
							{
								ArrayList<Dataset> temp = new ArrayList<Dataset>();
								for (int l = k; l < j + 1; l++)
									temp.add(d.get(l));
								// C(i - ?, 1) + A(i - ?)
								tempSet.add(dpTable[k - 1][i - 1] + changeOfDataset(temp, medianOfDataset(temp)));
							}
							// calculate minimum change of the set of dataset
							dpTable[j][i] = min(tempSet);
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		kanon dataset = new kanon(args[0], 4);
		ArrayList<Dataset> clonedSet = dataset.cloneAndSort();
		dataset.constructDPTable(clonedSet, 4);
		dataset.printDPTable();
	}
}
