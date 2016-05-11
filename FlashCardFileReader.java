import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FlashCardFileReader {
	private static char splitChar = ':';
	public static ArrayList<FlashCard> getFlashCards(String fileName)
	{
		ArrayList<FlashCard> cards = new ArrayList<FlashCard>();
		try 
		{
			FileReader read = new FileReader(fileName);
			BufferedReader fileIn = new BufferedReader(read);
			
			String line;
			while ((line = fileIn.readLine()) != null){
				String [] split = line.split(String.valueOf(splitChar));
				if (split.length != 3 && split.length != 2)
					throw new IOException("Invalid file input on line " + (cards.size()+1));
				cards.add(new FlashCard(split[0], split[1], (split.length==2)? "" : split[2]));
			}		
			fileIn.close();
		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("Unable to find file");
			System.exit(-1);
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
			System.exit(-1);
		}
		return cards;
	}
	
	public static void saveFlashCards(String fileName, ArrayList<FlashCard> cards)
	{
		try
		{
			FileWriter write = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(write);
			
			for (int i = 0; i < cards.size(); i++)
			{
				out.write(cards.get(i).getWord() + splitChar + cards.get(i).getDef() + "\n");
			}
			
			out.close();
		}
		catch(IOException ex){
			System.out.println(ex.getMessage());
		}
	}
}
