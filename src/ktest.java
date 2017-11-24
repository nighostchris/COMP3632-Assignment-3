import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

class Linkage
{
	private int QID;
	private int sensitiveAttribute;
	
	public Linkage()
	{
		QID = 0;
		sensitiveAttribute = 0;
	}
	
	public Linkage(int QID, int sensitiveAttribute)
	{
		this.QID = QID;
		this.sensitiveAttribute = sensitiveAttribute;
	}
	
	@Override
	public boolean equals(Object o)
	{
		Linkage temp = ((Linkage) o);
		if (temp.QID == QID)
			return true;
		else
			return false;
	}
}

public class ktest 
{
	private ArrayList<Linkage> uniqueData;
	private ArrayList<Integer> occurrenceRecord;
	
	public ktest()
	{
		uniqueData = new ArrayList<Linkage>();
		occurrenceRecord = new ArrayList<Integer>();
	}
	
	private void countDataOccurrence(File sourceFile)
	{
		try
		(
			Scanner sc = new Scanner(sourceFile);	
		)
		{
			while (sc.hasNext())
			{
				String[] data = sc.nextLine().split(",");
				Linkage newData = new Linkage(Integer.parseInt(data[0]), Integer.parseInt(data[1]));
				int iteration = 0;
				if (uniqueData.size() == 0)
				{
					uniqueData.add(newData);
					occurrenceRecord.add(1);
				}
				else
				{
					for (Linkage check : uniqueData)
					{
						if (newData.equals(check))
						{
							occurrenceRecord.set(iteration, occurrenceRecord.get(iteration) + 1);
							break;
						}
						iteration++;
					}
					
					if (iteration == uniqueData.size())
					{
						uniqueData.add(newData);
						occurrenceRecord.add(1);
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private int calculateKAnonymity()
	{
		int min = occurrenceRecord.get(0);
		for (Integer value : occurrenceRecord)
		{
			if (value < min)
				min = value;
		}
		return min;
	}
	
	public static void main(String[] args)
	{
		ktest dataset = new ktest();
		File sourceFile = new File(args[0]);
		dataset.countDataOccurrence(sourceFile);
		
		int kAnonymity = dataset.calculateKAnonymity();
		System.out.println("K-anonymity of this dataset is : " + kAnonymity);
		System.exit(kAnonymity);
	}
}
