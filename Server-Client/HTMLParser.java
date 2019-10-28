import java.util.*;

public class HTMLParser
{
	private static final int[][] table =
	{
		{1, 0, 0, 0, 0, 0, 0, 0, 0 },
		{1, 2, 0, 1, 5, 6, 1, 1, 1 },
		{2, 2, 4, 3, 2, 2, 2, 2, 2 },
		{3, 2, 4, 3, 3, 3, 3, 11, 3 },
		{1, 4, 4, 4, 4, 4, 4, 4, 4 },
		{5, 5, 0, 5, 5, 5, 5, 5, 5 },
		{2, 2, 2, 2, 2, 2, 7, 2, 2 },
		{2, 2, 2, 2, 2, 2, 8, 2, 2 },
		{8, 8, 8, 8, 8, 8, 9, 8, 8 },
		{8, 8, 8, 8, 8, 8, 10, 8, 8 },
		{8, 8, 0, 8, 8, 8, 8, 8, 8 },
		{11, 11, 11, 11, 11, 11, 11, 3, 11 }
	};

	private static int getColumn(char c)
	{
		return
		    c == '<' ? 0 :
			c == ' ' ? 1 :
			c == '>' ? 2 :
			c == '=' ? 3 :
			c == '/' ? 4 :
			c == '!' ? 5 :
            c == '-' ? 6 :
            c == '"' ? 7 : 8;
	}

	public static HTMLEntity parse(String htmlText)
	{
		char[]              cs;
		int                 state;
		List<HTMLComponent> comps;
		HTMLComponent       comp;
		HTMLAttributeSet    attr;
		String              tag;
		String              endTag;
		String              attrKey;
		String              attrVal;
		String              text;
		String              comment;
		HTMLTraceableComponent currComp;

		HTMLEntity          entity;

		cs      = htmlText.toCharArray();
		state   = 0;
		comps   = new ArrayList<HTMLComponent>();
		comp    = null;
		attr    = null;
		tag     = "";
		endTag  = "";
		attrKey = "";
		attrVal = "";
		text    = "";
		comment = "";
		currComp = null;
		entity = null;

		for(int i = 0; i < cs.length; i++)
		{
			char c;
			c = cs[i];

            if(state == 0)
            {
				if(c == '<')
				    if(currComp != null && HTMLUtilities.isSimpleTag(currComp.getTag()))
				        currComp = currComp.getParent();
			}
			else if(state == 1)
			{
				if(c == ' ' || c == '>')
				{
					HTMLTraceableComponent tmp;
					tmp = new HTMLTraceableComponent(currComp);
					tmp.setTag(HTMLUtilities.getTag(tag));
					if(currComp != null)
					    currComp.addChild(tmp);
					else
					    comps.add(tmp);
					currComp = tmp;
					tag = "";
				}
				else if(c != '/' && c != '!')
				    tag += c;
			}
			else if(state == 2)
			{
				if(c == '=')
				{
					attr = new HTMLAttributeSet();
					attr.setAttribute(attrKey);
					attrKey = "";
				}
				else if(c == '>')
				{
					attr = null;
					attrKey = "";
				}
				else
				    attrKey += c;
			}
			else if(state == 3)
			{
				if(c == ' ' || c == '>')
				{
					if(currComp != null && attr != null)
					{
						attr.setValue(attrVal);
						currComp.addAttributeSet(attr);
					}
					attrVal = "";
				}
				else
				    attrVal += c;
			}
			else if(state == 4)
			{
				if(c == '<')
				{
					if(currComp != null)
					    currComp.setText(text);
					text = "";
				}
				else
				    text += c;
			}
			else if(state == 5)
			{
				if(c == '>')
					currComp = currComp.getParent();
			}
			else if(state == 6)
			{
			}
			else if(state == 7)
			{
				if(c == '-')
				{
					HTMLTraceableComponent tmp;
					tmp = new HTMLTraceableComponent(currComp);
					tmp.setTag(HTMLTag.COMMENT);
					if(currComp != null)
						currComp.addChild(tmp);
					else
						comps.add(tmp);
					currComp = tmp;
				}
			}
			else if(state == 8)
			{
				comment += c;
			}
			else if(state == 9)
			{
				comment += c;
			}
			else if(state == 10)
			{
				if(c == '>')
				{
					if(currComp != null)
					    currComp.setText(comment);
					comment = "";
				}
				else
				    comment += c;
			}
			else if(state == 11)
			{
				attrVal += c;
			}

			state = table[state][getColumn(c)];
		}

        entity = new HTMLEntity();
		for(int i = 0; i < comps.size(); i++)
		    entity.addHTMLComponent(comps.get(i));
		return entity;
	}
}