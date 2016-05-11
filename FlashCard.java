
public class FlashCard {
	private String word;
	private String def;
	private String pinyin;
	
	public FlashCard()
	{}
	public FlashCard(String w0, String d0, String p0)
	{
		this.word = w0;
		this.def = d0;
		this.pinyin = p0;
	}
	public String getWord()
	{
		return word;
	}
	public String getDef()
	{
		return def;
	}
	public String getPinyin()
	{
		return pinyin;
	}
}
