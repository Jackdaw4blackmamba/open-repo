import java.util.*;

public class TextConverter
{
	private static final char SPLITTER = '|';
	private static final char FILLER   = '_';

	public static String merge(String[] splitTexts)
	{
		String margedText;
		margedText = "";

		for(int i = 0; i < splitTexts.length; i++)
		{
			char[] cs;
			cs = splitTexts[i].toCharArray();

			for(int j = 0; j < cs.length; j++)
			{
				margedText += cs[j];
				if(cs[j] == SPLITTER)
				    margedText += SPLITTER;
			}
			if(i != splitTexts.length - 1)
			    margedText += new String(new char[]{FILLER, SPLITTER, FILLER});
		}

		return margedText;
	}

	public static String[] split(String margedText)
	{
		String[] splitTexts;
		splitTexts = splitText(margedText);
		for(int i = 0; i < splitTexts.length; i++)
		    splitTexts[i] = splitTexts[i].replace(new String(new char[]{SPLITTER, SPLITTER}), new String(new char[]{SPLITTER}));
		return splitTexts;
	}

	private static String[] splitText(String text)
	{
		List<String> list;
		char[]       cs;
		String       tmpText;
		int          state;

		list = new ArrayList<String>();
		cs = text.toCharArray();
		tmpText = "";
		state = 0;

		for(int i = 0; i < cs.length; i++)
		{
			tmpText += cs[i];
			if(state == 0)
			{
				if(cs[i] == FILLER)
				    state = 1;
			}
			else if(state == 1)
			{
				if(cs[i] == SPLITTER)
				    state = 2;
				else if(cs[i] != FILLER)
				    state = 0;
			}
			else if(state == 2)
			{
				if(cs[i] == FILLER)
				{
					//tmpText = tmpText.replace(new String(new char[]{FILLER, SPLITTER, FILLER}), "");
					list.add(tmpText.substring(0, tmpText.length() - 3));
					tmpText = "";
				}
				state = 0;
			}

			if(i == cs.length - 1)
			    list.add(tmpText);
		}

		return list.toArray(new String[]{});
	}
}