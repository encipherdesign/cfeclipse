/*
 * Created on Mar 21, 2004
 *
 * The MIT License
 * Copyright (c) 2004 Oliver Tupman
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package com.rohanclan.cfml.parser.docitems;

import com.rohanclan.cfml.parser.ParseError;
import com.rohanclan.cfml.parser.docitems.DocItem;
import com.rohanclan.cfml.parser.exception.*;
import com.rohanclan.cfml.dictionary.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * @author Oliver Tupman
 *
 * A tag-based item in the document.
 *
 */
public class TagItem extends DocItem {
	
	/**
	 * Name / value attributes
	 * Name is always lower case.
	 */
	protected HashMap itemAttributes;
	/** Optional. 
	   *  
	   *  The matching item for this DocItem. For example CFML tags quite often have a matching closing tag, as do HTML tags. One example:
	   *  
	   *  <cfoutput>
	   *    #someVar#
	   *  </cfoutput>
	   *  
	   *  Therefore the DocItem representing the <cfoutput> would have a matchingItem that points (can you do this in Java???) to the closing DocItem, whereas the closing DocItem would have the opening one as the matchingItem.
	   */

	public DocItem matchingItem;

	/**
	 * Adds an attribute to the document
	 * If the attribute already exists for this tag item then it will cause a non-fatal
	 * parse error. Only the first attribute will be stored, identical attributes thereafter
	 * will not be added.
	 *
	 * @param newAttr The new attribute to add to this tag item. 
	 */
	public boolean addAttribute(AttributeItem newAttr) 
	{
		boolean addOkay = true;
		String attrName = newAttr.getName();
		/*
		 * Is the attribute already present in the tag's attribute list? 
		 */
		if(itemAttributes.containsKey(attrName.toLowerCase()))
		{
			parseMessages.addMessage(new ParseError(lineNumber, startPosition, endPosition, itemData, 
									"Attribute \'" + attrName + "\' has already been defined."));
		}
		
		itemAttributes.put(attrName.toLowerCase(), newAttr);
		
		return addOkay;
	}

	public boolean addAttributes(ArrayList newAttributes) 
	{
		boolean addOkay = true;
		Iterator attrIter = newAttributes.iterator();
		while(attrIter.hasNext()) {
			addOkay = this.addAttribute((AttributeItem)attrIter.next()) & addOkay;
		}

		return addOkay;
	}
	
	public boolean hasAttribute(String attrName) {
		return this.itemAttributes.containsKey(attrName.toLowerCase());
	}
	
	/**
	 * Gets the string-based value of an attribute
	 * @param attrName
	 * @return the string value of the attribute if it exists; if not, null. 
	 * @deprecated please use getAttributeValue()
	 */
	public String getAttribute(String attrName)
	{
		return this.getAttributeValue(attrName);
	}
	
	/**
	 * Gets the string-based value of an attribute
	 * @param attrName
	 * @return the string value of the attribute if it exists; if not, null. 
	 */
	public String getAttributeValue(String attrName) {
		AttributeItem attr = this.getAttributeObj(attrName);
		if(attr == null) {
			return null;
		}
		return attr.getValue();		
	}
	
	public AttributeItem getAttributeObj(String attrName) {
		return (AttributeItem)this.itemAttributes.get(attrName.toLowerCase());
	}
	
	public DocItem getMatchingItem() {
		return matchingItem;
	}
	
	public void setMatchingItem(DocItem closer) {
		matchingItem = closer;
	}
	
	public TagItem(int line, int startDocPos, int endDocPos, String name)
	{
		super(line, startDocPos, endDocPos, name);
		itemAttributes = new HashMap();
	}
	
	public boolean hasClosingTag() throws NullPointerException 
	{
		Tag tag = syntax.getTag(itemName);
		if(tag == null)
		{
			System.err.println("TagItem::hasClosingTag() - The tag I've retrieved is null! Tag name is \'" + itemName + "\' Line number: " + this.lineNumber);
			//
			// Should really raise an exception?
			parseMessages.addMessage(new ParseError(lineNumber, startPosition, endPosition, itemData, 
										"Unknown cf tag \'<cf" + itemName + ">\'."));
			
			return true;	// Let's say it's a single tag.
		}
		return !tag.isSingle();
	}

}