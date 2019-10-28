import java.util.*;

public class HTMLUtilities
{
    public static boolean isSimpleTag(HTMLTag tag)
    {
        return
        tag == HTMLTag.COMMENT ||
        tag == HTMLTag.DOCTYPE ||
        tag == HTMLTag.AREA    ||
        tag == HTMLTag.BASE    ||
        tag == HTMLTag.BR      ||
        tag == HTMLTag.COL     ||
        tag == HTMLTag.EMBED   ||
        tag == HTMLTag.HR      ||
        tag == HTMLTag.IMG     ||
        tag == HTMLTag.INPUT   ||
        tag == HTMLTag.KEYGEN  ||
        tag == HTMLTag.LINK    ||
        tag == HTMLTag.META    ||
        tag == HTMLTag.PARAM   ||
        tag == HTMLTag.SOURCE;
    }

    public static String getTagText(HTMLTag tag)
    {
        return
        tag == HTMLTag.A          ? "a"          :
        tag == HTMLTag.ABBR       ? "abbr"       :
        tag == HTMLTag.ACRONYM    ? "acronym"    :
        tag == HTMLTag.ADDRESS    ? "address"    :
        tag == HTMLTag.APPLET     ? "applet"     :
        tag == HTMLTag.AREA       ? "area"       :
        tag == HTMLTag.B          ? "b"          :
        tag == HTMLTag.BASE       ? "base"       :
        tag == HTMLTag.BASEFONT   ? "basefont"   :
        tag == HTMLTag.BDO        ? "bdo"        :
        tag == HTMLTag.BGSOUND    ? "bgsound"    :
        tag == HTMLTag.BIG        ? "big"        :
        tag == HTMLTag.BLINK      ? "blink"      :
        tag == HTMLTag.BLOCKQUOTE ? "blockquote" :
        tag == HTMLTag.BODY       ? "body"       :
        tag == HTMLTag.BR         ? "br"         :
        tag == HTMLTag.BUTTON     ? "button"     :
        tag == HTMLTag.CAPTION    ? "caption"    :
        tag == HTMLTag.CENTER     ? "center"     :
        tag == HTMLTag.CITE       ? "cite"       :
        tag == HTMLTag.CODE       ? "code"       :
        tag == HTMLTag.COL        ? "col"        :
        tag == HTMLTag.COLGROUP   ? "colgroup"   :
        tag == HTMLTag.COMMENT    ? "!--"        :
        tag == HTMLTag.COMMENT_IE ? "comment"    :
        tag == HTMLTag.DD         ? "dd"         :
        tag == HTMLTag.DEL        ? "del"        :
        tag == HTMLTag.DFN        ? "dfn"        :
        tag == HTMLTag.DIR        ? "dir"        :
        tag == HTMLTag.DIV        ? "div"        :
        tag == HTMLTag.DL         ? "dl"         :
        tag == HTMLTag.DOCTYPE    ? "DOCTYPE"    :
        tag == HTMLTag.DT         ? "dt"         :
        tag == HTMLTag.EM         ? "em"         :
        tag == HTMLTag.EMBED      ? "embed"      :
        tag == HTMLTag.FIELDSET   ? "fieldset"   :
        tag == HTMLTag.FONT       ? "font"       :
        tag == HTMLTag.FORM       ? "form"       :
        tag == HTMLTag.FRAME      ? "frame"      :
        tag == HTMLTag.FRAMESET   ? "frameset"   :
        tag == HTMLTag.H1         ? "h1"         :
        tag == HTMLTag.H2         ? "h2"         :
        tag == HTMLTag.H3         ? "h3"         :
        tag == HTMLTag.H4         ? "h4"         :
        tag == HTMLTag.H5         ? "h5"         :
        tag == HTMLTag.H6         ? "h6"         :
        tag == HTMLTag.HEAD       ? "head"       :
        tag == HTMLTag.HR         ? "hr"         :
        tag == HTMLTag.HTML       ? "html"       :
        tag == HTMLTag.I          ? "i"          :
        tag == HTMLTag.IFRAME     ? "iframe"     :
        tag == HTMLTag.IMG        ? "img"        :
        tag == HTMLTag.INPUT      ? "input"      :
        tag == HTMLTag.INS        ? "ins"        :
        tag == HTMLTag.ISINDEX    ? "isindex"    :
        tag == HTMLTag.KBD        ? "kbd"        :
        tag == HTMLTag.KEYGEN     ? "keygen"     :
        tag == HTMLTag.LABEL      ? "label"      :
        tag == HTMLTag.LEGEND     ? "legend"     :
        tag == HTMLTag.LI         ? "li"         :
        tag == HTMLTag.LINK       ? "link"       :
        tag == HTMLTag.LISTING    ? "listing"    :
        tag == HTMLTag.MAP        ? "map"        :
        tag == HTMLTag.MARQUEE    ? "marquee"    :
        tag == HTMLTag.MENU       ? "menu"       :
        tag == HTMLTag.META       ? "meta"       :
        tag == HTMLTag.NOBR       ? "nobr"       :
        tag == HTMLTag.NOEMBED    ? "noembed"    :
        tag == HTMLTag.NOFRAMES   ? "noframes"   :
        tag == HTMLTag.NOSCRIPT   ? "noscript"   :
        tag == HTMLTag.OBJECT     ? "object"     :
        tag == HTMLTag.OL         ? "ol"         :
        tag == HTMLTag.OPTGROUP   ? "optgroup"   :
        tag == HTMLTag.OPTION     ? "option"     :
        tag == HTMLTag.P          ? "p"          :
        tag == HTMLTag.PARAM      ? "param"      :
        tag == HTMLTag.PLAINTEXT  ? "plaintext"  :
        tag == HTMLTag.PRE        ? "pre"        :
        tag == HTMLTag.Q          ? "q"          :
        tag == HTMLTag.RB         ? "rb"         :
        tag == HTMLTag.RP         ? "rp"         :
        tag == HTMLTag.RT         ? "rt"         :
        tag == HTMLTag.RUBY       ? "ruby"       :
        tag == HTMLTag.S          ? "s"          :
        tag == HTMLTag.SAMP       ? "samp"       :
        tag == HTMLTag.SCRIPT     ? "script"     :
        tag == HTMLTag.SELECT     ? "select"     :
        tag == HTMLTag.SMALL      ? "small"      :
        tag == HTMLTag.SOURCE     ? "source"     :
        tag == HTMLTag.SPAN       ? "span"       :
        tag == HTMLTag.STRIKE     ? "strike"     :
        tag == HTMLTag.STRONG     ? "strong"     :
        tag == HTMLTag.STYLE      ? "style"      :
        tag == HTMLTag.SUB        ? "sub"        :
        tag == HTMLTag.SUP        ? "sup"        :
        tag == HTMLTag.TABLE      ? "table"      :
        tag == HTMLTag.TBODY      ? "tbody"      :
        tag == HTMLTag.TD         ? "td"         :
        tag == HTMLTag.TEXTAREA   ? "textarea"   :
        tag == HTMLTag.TFOOT      ? "tfoot"      :
        tag == HTMLTag.TH         ? "th"         :
        tag == HTMLTag.THEAD      ? "thead"      :
        tag == HTMLTag.TITLE      ? "title"      :
        tag == HTMLTag.TR         ? "tr"         :
        tag == HTMLTag.TT         ? "tt"         :
        tag == HTMLTag.U          ? "u"          :
        tag == HTMLTag.UL         ? "ur"         :
        tag == HTMLTag.VAR        ? "var"        :
        tag == HTMLTag.WBR        ? "wbr"        :
        tag == HTMLTag.XMP        ? "xmp"        : "";
    }

    public static HTMLTag getTag(String tagText)
    {
        List<HTMLTag> list;
        list = getHTMLTagList();

        for(HTMLTag tag : list)
            if (getTagText(tag).toLowerCase().equals(tagText.toLowerCase()))
                return tag;
        return HTMLTag.UNKNOWN;
    }

    public static List<HTMLTag> getHTMLTagList()
    {
        List<HTMLTag> tags;
        tags = new ArrayList<HTMLTag>();
        HTMLTag[] arr =
        {
            HTMLTag.A,
            HTMLTag.ABBR,
            HTMLTag.ACRONYM,
            HTMLTag.ADDRESS,
            HTMLTag.APPLET,
            HTMLTag.AREA,
            HTMLTag.B,
            HTMLTag.BASE,
            HTMLTag.BASEFONT,
            HTMLTag.BDO,
            HTMLTag.BGSOUND,
            HTMLTag.BIG,
            HTMLTag.BLINK,
            HTMLTag.BLOCKQUOTE,
            HTMLTag.BODY,
            HTMLTag.BR,
            HTMLTag.BUTTON,
            HTMLTag.CAPTION,
            HTMLTag.CENTER,
            HTMLTag.CITE,
            HTMLTag.CODE,
            HTMLTag.COL,
            HTMLTag.COLGROUP,
            HTMLTag.COMMENT,
            HTMLTag.COMMENT_IE,
            HTMLTag.DD,
            HTMLTag.DEL,
            HTMLTag.DFN,
            HTMLTag.DIR,
            HTMLTag.DIV,
            HTMLTag.DL,
            HTMLTag.DOCTYPE,
            HTMLTag.DT,
            HTMLTag.EM,
            HTMLTag.EMBED,
            HTMLTag.FIELDSET,
            HTMLTag.FONT,
            HTMLTag.FORM,
            HTMLTag.FRAME,
            HTMLTag.FRAMESET,
            HTMLTag.H1,
            HTMLTag.H2,
            HTMLTag.H3,
            HTMLTag.H4,
            HTMLTag.H5,
            HTMLTag.H6,
            HTMLTag.HEAD,
            HTMLTag.HR,
            HTMLTag.HTML,
            HTMLTag.I,
            HTMLTag.IFRAME,
            HTMLTag.IMG,
            HTMLTag.INPUT,
            HTMLTag.INS,
            HTMLTag.ISINDEX,
            HTMLTag.KBD,
            HTMLTag.KEYGEN,
            HTMLTag.LABEL,
            HTMLTag.LEGEND,
            HTMLTag.LI,
            HTMLTag.LINK,
            HTMLTag.LISTING,
            HTMLTag.MAP,
            HTMLTag.MARQUEE,
            HTMLTag.MENU,
            HTMLTag.META,
            HTMLTag.NOBR,
            HTMLTag.NOEMBED,
            HTMLTag.NOFRAMES,
            HTMLTag.NOSCRIPT,
            HTMLTag.OBJECT,
            HTMLTag.OL,
            HTMLTag.OPTGROUP,
            HTMLTag.OPTION,
            HTMLTag.P,
            HTMLTag.PARAM,
            HTMLTag.PLAINTEXT,
            HTMLTag.PRE,
            HTMLTag.Q,
            HTMLTag.RB,
            HTMLTag.RP,
            HTMLTag.RT,
            HTMLTag.RUBY,
            HTMLTag.S,
            HTMLTag.SAMP,
            HTMLTag.SCRIPT,
            HTMLTag.SELECT,
            HTMLTag.SMALL,
            HTMLTag.SOURCE,
            HTMLTag.SPAN,
            HTMLTag.STRIKE,
            HTMLTag.STRONG,
            HTMLTag.STYLE,
            HTMLTag.SUB,
            HTMLTag.SUP,
            HTMLTag.TABLE,
            HTMLTag.TBODY,
            HTMLTag.TD,
            HTMLTag.TEXTAREA,
            HTMLTag.TFOOT,
            HTMLTag.TH,
            HTMLTag.THEAD,
            HTMLTag.TITLE,
            HTMLTag.TR,
            HTMLTag.TT,
            HTMLTag.U,
            HTMLTag.UL,
            HTMLTag.VAR,
            HTMLTag.WBR,
            HTMLTag.XMP
        };
        for(int i = 0; i < arr.length; i++)
            tags.add(arr[i]);

        return tags;
    }
}