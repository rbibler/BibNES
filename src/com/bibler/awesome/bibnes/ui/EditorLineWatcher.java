package com.bibler.awesome.bibnes.ui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyleConstants;

public class EditorLineWatcher extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7262672915611800201L;
	
	protected JTextComponent component;
	private HashMap<String, FontMetrics> fonts;
	
	public EditorLineWatcher(JTextComponent component) {
		super();
		this.component = component;
	}
	
	
	protected int getLineNumber(int rowStartOffset) {
		Element root = component.getDocument().getDefaultRootElement();
		int index = root.getElementIndex(rowStartOffset);
		return index;
	}
	
	/*
	 *	Get the line number to be drawn. The empty string will be returned
	 *  when a line of text has wrapped.
	 */
	protected String getTextLineNumber(int rowStartOffset)
	{
		int index = getLineNumber(rowStartOffset);
		Element root = component.getDocument().getDefaultRootElement();
		Element line = root.getElement( index );
		if (line.getStartOffset() == rowStartOffset)
			return String.valueOf(index + 1);
		else
			return "";
	}
	
	
	protected int getLineYOffset(int lineNumber, FontMetrics fontMetrics) {
		int offset = -1;
		int lineHeight = fontMetrics.getHeight();
		offset = (lineNumber * lineHeight) + (lineHeight / 2);
		return offset;
	}
	
	/*
	 *  Determine the Y offset for the current row
	 */
	protected int getOffsetY(int rowStartOffset, FontMetrics fontMetrics)
		throws BadLocationException
	{
		//  Get the bounding rectangle of the row

		Rectangle r = component.modelToView( rowStartOffset );
		int lineHeight = fontMetrics.getHeight();
		int y = r.y + r.height;
		int descent = 0;

		//  The text needs to be positioned above the bottom of the bounding
		//  rectangle based on the descent of the font(s) contained on the row.

		if (r.height == lineHeight)  // default font is being used
		{
			descent = fontMetrics.getDescent();
		}
		else  // We need to check all the attributes for font changes
		{
			if (fonts == null)
				fonts = new HashMap<String, FontMetrics>();

			Element root = component.getDocument().getDefaultRootElement();
			int index = root.getElementIndex( rowStartOffset );
			Element line = root.getElement( index );

			for (int i = 0; i < line.getElementCount(); i++)
			{
				Element child = line.getElement(i);
				AttributeSet as = child.getAttributes();
				String fontFamily = (String)as.getAttribute(StyleConstants.FontFamily);
				Integer fontSize = (Integer)as.getAttribute(StyleConstants.FontSize);
				String key = fontFamily + fontSize;

				FontMetrics fm = fonts.get( key );

				if (fm == null)
				{
					Font font = new Font(fontFamily, Font.PLAIN, fontSize);
					fm = component.getFontMetrics( font );
					fonts.put(key, fm);
				}

				descent = Math.max(descent, fm.getDescent());
			}
		}

		return y - descent;
	}
	
	/*
	 *  We need to know if the caret is currently positioned on the line we
	 *  are about to paint so the line number can be highlighted.
	 */
	protected boolean isCurrentLine(int rowStartOffset)
	{
		int caretPosition = component.getCaretPosition();
		Element root = component.getDocument().getDefaultRootElement();

		if (root.getElementIndex( rowStartOffset ) == root.getElementIndex(caretPosition))
			return true;
		else
			return false;
	}

}
