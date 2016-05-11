import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

public class FlashCardUI {
	private JFrame mainframe;
	private JPanel headerPanel;
	private JPanel borderPanel;
	private JPanel cardPanel = new JPanel(new CardLayout());

	private int pageCounter = 1;	
	private JPanel buttonPanel;
	private JButton next;
	private JButton previous;
	private ArrayList<FlashCardPanel> flashcards = new ArrayList<FlashCardPanel>();

	public FlashCardUI()
	{
		prepareGUI();
	}

	public void addCard(FlashCard card)
	{
		if (card == null)
			return;
		FlashCardPanel newCard = new FlashCardPanel(card);
		flashcards.add(newCard);
		updateCard(newCard);
	}

	public void addCards(ArrayList<FlashCard> cardlist)
	{
		for (int i = 0; i < cardlist.size(); i++)
			addCard(cardlist.get(i));
	}

	private void prepareGUI()
	{		
		next = new JButton("Next");
		next.setActionCommand("NEXT");
		next.addActionListener(new ButtonClicker());
		previous = new JButton("Previous");
		previous.addActionListener(new ButtonClicker());
		previous.setActionCommand("PREVIOUS");
		JButton addButton = new JButton("Add");
		addButton.setActionCommand("ADD");
		addButton.addActionListener(new ButtonClicker());

		buttonPanel = new JPanel(new GridLayout(1,3));
		buttonPanel.add(previous);
		buttonPanel.add(addButton);
		buttonPanel.add(next);
		updateButtons();

		borderPanel = new JPanel(new BorderLayout(10, 10));
		borderPanel.setBorder(new TitledBorder("0/0"));
		borderPanel.add(cardPanel);

		headerPanel = new JPanel(new BorderLayout(1,1));
		JButton saveButton = new JButton("SAVE");
		saveButton.setActionCommand("SAVE");
		saveButton.addActionListener(new ButtonClicker());
		headerPanel.add(saveButton, BorderLayout.LINE_END);
		
		mainframe = new JFrame("My Flashcards");
		mainframe.add(headerPanel, BorderLayout.NORTH);
		mainframe.add(borderPanel);
		mainframe.add(buttonPanel, BorderLayout.SOUTH);
		mainframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		mainframe.pack();
		mainframe.setVisible(true);	
	}

	private void changeCard()
	{
		CardLayout cl = (CardLayout)(cardPanel.getLayout());
		cl.show(cardPanel, String.valueOf(pageCounter));
		TitledBorder tb = (TitledBorder)(borderPanel.getBorder());
		tb.setTitle(pageCounter + "/" + flashcards.size());
		borderPanel.repaint();
	}

	private void updateCard(FlashCardPanel newCard)
	{
		borderPanel.remove(cardPanel);
		cardPanel.add(newCard.panel, String.valueOf(flashcards.size()));
		borderPanel.add(cardPanel);
		TitledBorder tb = (TitledBorder)(borderPanel.getBorder());
		tb.setTitle(pageCounter + "/" + flashcards.size());
		borderPanel.revalidate();
		borderPanel.repaint();
		changeCard();
		updateButtons();
		mainframe.pack();
	}

	private void updateButtons()
	{
		next.setEnabled((pageCounter >= flashcards.size()) ? false : true);
		previous.setEnabled((pageCounter == 1)? false : true);
	}
	
	private void saveFlashCardsToFile(String fileName){
		ArrayList<FlashCard> cards = new ArrayList<FlashCard>();
		for(FlashCardPanel card : flashcards){
			cards.add(card.getFlashCard());
		}
		FlashCardFileReader.saveFlashCards(fileName, cards);
	}

	private void getFlashCardsFromFile(String fileName){
		addCards(FlashCardFileReader.getFlashCards(fileName));	
	}
	
	private String getFileName(){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		JPanel newPanel = new JPanel();
		int result = fileChooser.showSaveDialog(newPanel);
		if (result == JOptionPane.OK_OPTION) 
			return fileChooser.getSelectedFile().getAbsolutePath() + File.separatorChar + textPopup("Enter Name of File");
		else
			return null;	
	}
	
	private String textPopup(String dialog){
		JPanel popupPanel = new JPanel();
		JTextField textField = new JTextField(10);
		popupPanel.add(textField);
		int result = JOptionPane.showConfirmDialog(null, popupPanel, 
				dialog, JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) 
			return textField.getText();
		else
			return null;
	}
	
	private class ButtonClicker implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();  
			if(command.equals( "NEXT" ))  {
				pageCounter++;
				changeCard();
				updateButtons();
			}
			else if( command.equals( "PREVIOUS" ) )  {
				pageCounter--;
				changeCard();
				updateButtons();
			}
			else if (command.equals("ADD"))
			{
				new AddCardPopup().getNewCard();
			}
			else if (command.equals("SAVE"))
			{
				String fileName = getFileName();
				if (fileName != null)
					saveFlashCardsToFile(fileName);
			}
		}
	}

	private class FlashCardPanel{
		JPanel panel = new JPanel(new GridLayout(3,1));
		JLabel label1;
		JLabel label2;
		JButton button;
		public FlashCardPanel(FlashCard card)
		{
			label1 = new JLabel(card.getWord());
			label2 = new JLabel(card.getDef());
			label1.setHorizontalAlignment(JLabel.CENTER);
			label2.setHorizontalAlignment(JLabel.CENTER);
			label2.setVisible(false);
			button = new JButton("Show Def");
			button.addActionListener(new ShowLabel());			
			panel.add(label1);
			panel.add(label2);
			panel.add(button);
		}
		
		private class ShowLabel implements ActionListener
		{
			public void actionPerformed(ActionEvent e) {
				label2.setVisible((label2.isVisible())? false : true);
			}
		}
		
		private FlashCard getFlashCard(){
			return new FlashCard(label1.getText(),label2.getText(), "");
		}
	}
	
	private class AddCardPopup{
		JPanel panel = new JPanel(new GridLayout(3,2));
		JTextField wordText = new JTextField(10);
		JTextField defText = new JTextField(10);
		JButton fileButton = new JButton("Select Folder");

		private AddCardPopup()
		{
			panel.add(new JLabel("Word:"));
			panel.add(wordText);
			panel.add(new JLabel("Def:"));
			panel.add(defText);
			panel.add(new JLabel("Add from File:"));
			fileButton.addActionListener(new AddFromFile());
			panel.add(fileButton);			
		}

		private void getNewCard()
		{
			int result = JOptionPane.showConfirmDialog(null, panel, 
					"Please Enter Flash Card Values OR Add Flash Cards From File", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) 
			{
				if (!wordText.getText().isEmpty() && !defText.getText().isEmpty())
					addCard(new FlashCard(wordText.getText(), defText.getText(), ""));
			}
			else
				return;
		}
		
		
		private class AddFromFile implements ActionListener
		{
			public void actionPerformed(ActionEvent e) {
				String fileName = getFileName();
				if (fileName != null)
				{
					getFlashCardsFromFile(fileName);
					
				}
				else
				{
					JOptionPane.showConfirmDialog(null, new JPanel(), "No file selected", JOptionPane.OK_OPTION);
				}
			}		
		}
		
		private String getFileName()
		{
			JFileChooser fileChooser = new JFileChooser();
			JPanel newPanel = new JPanel();
			int result = fileChooser.showOpenDialog(newPanel);
			if (result == JFileChooser.APPROVE_OPTION) 
				return fileChooser.getSelectedFile().getAbsolutePath();
			else
				return null;		
		}
	}
}
