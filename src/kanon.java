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
	
	public kanon(String fileName)
	{
		input = new ArrayList<Dataset>();
		File sourceFile = new File(fileName);
		inputDataFile(sourceFile);
		dpTable = new int[input.size()][4];
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
			return (int)((d.get(half).getQID() + d.get(half + 1).getQID()) / 2);
		}
		else
		{
			int half = (d.size() + 1) / 2;
			return d.get(half).getQID();
		}
	}
	
	private int changeOfDataset(ArrayList<Dataset> d, int median)
	{
		int change = 0;
		for (Dataset data : d)
			change += Math.abs(data.getQID() - median);
		return change;
	}
	
	private void constructDPTable(ArrayList<Dataset> d)
	{
		// x-coor, height of dpTable
		for (int i = 0; i < dpTable[0].length; i++)
		{
			// y-coor, width of dpTable
			for (int j = 0; j < dpTable.length; j++)
			{
				// No need to compute DP for those lower than value of kanonymity
				if (j < 4)
				{
					dpTable[i][j] = -1;
				}
				else
				{
					// Get the data to process
					ArrayList<Dataset> tempSet = new ArrayList<Dataset>();
					for (int k = 0; k < j; k++)
						tempSet.add(d.get(k));
					// Get the median of the dataset
					int median = medianOfDataset(d);
					// Get the minimal change of the dataset
					dpTable[i][j] = changeOfDataset(tempSet, median);
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		kanon dataset = new kanon(args[0]);
		ArrayList<Dataset> clonedSet = dataset.cloneAndSort();
		dataset.constructDPTable(clonedSet);
	}
}
