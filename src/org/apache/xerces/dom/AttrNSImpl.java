/* $Id$ */
/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xerces.dom;

import org.w3c.dom.*;
import org.apache.xerces.dom.events.MutationEventImpl;


/**
 * AttrNSImpl inherits from AttrImpl and adds namespace support. 
 */
public class AttrNSImpl
    extends AttrImpl {

    //
    // Constants
    //

    /** Serialization version. */
    static final long serialVersionUID = -781906615369795414L;

    //
    // Data
    //

    /** DOM2: Namespace URI. */
	protected String namespaceURI;
  
    /** DOM2: Prefix */
	protected String prefix;

    /** DOM2: localName. */
	protected String localName;
    /**
     * DOM2: Constructor for Namespace implementation.
     */
    protected AttrNSImpl(DocumentImpl ownerDocument, 
			 String namespaceURI, 
			 String qualifiedName) {

	// treat an empty string as a null
	if (namespaceURI != null && !namespaceURI.equals("")) {
	    this.namespaceURI = namespaceURI;
	} else {
	    this.namespaceURI = null;
	}

    	if (!DocumentImpl.isXMLName(qualifiedName)) {
    	    throw new DOMExceptionImpl(DOMException.INVALID_CHARACTER_ERR, 
    	                               "INVALID_CHARACTER_ERR");
        }

        this.ownerDocument = ownerDocument;
        this.name = qualifiedName;
        int index = qualifiedName.indexOf(':');
        if (index < 0) {
            this.prefix = null;
            this.localName = qualifiedName;
        } 
        else {
            this.prefix = qualifiedName.substring(0, index); 
            this.localName = qualifiedName.substring(index+1);
        }
        
	if (this.namespaceURI == null) {
	    if (prefix != null) {
		if (prefix.equals("xml")) {
		    this.namespaceURI = "http://www.w3.org/XML/1998/namespace";
		} else if (prefix.equals("xmlns")) {
		    this.namespaceURI = "http://www.w3.org/2000/xmlns/";
		}
	    } else if (qualifiedName.equals("xmlns")) {
		this.namespaceURI = "http://www.w3.org/2000/xmlns/";
	    }
	} else {
	    if ((prefix != null
		 &&
		 ((prefix.equals("xml")
		   && !namespaceURI.equals("http://www.w3.org/XML/1998/namespace"))
		  ||
		  (prefix.equals("xmlns")
		   && !namespaceURI.equals("http://www.w3.org/2000/xmlns/"))))
		||
		(qualifiedName.equals("xmlns")
		 && !namespaceURI.equals("http://www.w3.org/2000/xmlns/"))) {
		throw new DOMExceptionImpl(DOMException.NAMESPACE_ERR, 
					   "NAMESPACE_ERR");
	    }
	}
    	
        syncData = true;
    } 

    // for DeferredAttrImpl
    protected AttrNSImpl(DocumentImpl ownerDocument, 
			 String value) {
	super(ownerDocument, value);
    }

    //
    // DOM2: Namespace methods
    //

    /** 
     * Introduced in DOM Level 2. <p>
     *
     * The namespace URI of this node, or null if it is unspecified.<p>
     *
     * This is not a computed value that is the result of a namespace lookup based on
     * an examination of the namespace declarations in scope. It is merely the
     * namespace URI given at creation time.<p>
     *
     * For nodes created with a DOM Level 1 method, such as createElement
     * from the Document interface, this is null.     
     * @since WD-DOM-Level-2-19990923
     */
    public String getNamespaceURI()
    {
        if (syncData) {
            synchronizeData();
        }
        // REVIST: This code could/should be done at a lower-level, such that the namespaceURI
        // is set properly upon creation. However, there still seems to be some DOM spec 
        // interpretation grey-area.
	return namespaceURI;
    }
    
    /** 
     * Introduced in DOM Level 2. <p>
     *
     * The namespace prefix of this node, or null if it is unspecified. <p>
     *
     * For nodes created with a DOM Level 1 method, such as createElement
     * from the Document interface, this is null. <p>
     *
     * @since WD-DOM-Level-2-19990923
     */
    public String getPrefix()
    {
        if (syncData) {
            synchronizeData();
        }
        return prefix;
    }
    
    /** 
     * Introduced in DOM Level 2. <p>
     *
     * Note that setting this attribute changes the nodeName attribute, which holds the
     * qualified name, as well as the tagName and name attributes of the Element
     * and Attr interfaces, when applicable.<p>
     *
     * @throws INVALID_CHARACTER_ERR Raised if the specified
     * prefix contains an invalid character.     
     *
     * @since WD-DOM-Level-2-19990923
     */
    public void setPrefix(String prefix)
        throws DOMException
    {
        if (syncData) {
            synchronizeData();
        }
	// treat an empty string as a null
	if (prefix != null && prefix.equals("")) {
	    prefix = null;
	}
	if (namespaceURI == null ||
	    (prefix != null
	     &&
	     ((prefix.equals("xmlns")
	       && !namespaceURI.equals("http://www.w3.org/2000/xmlns/"))
	      ||
	      (prefix.equals("xml")
	       && !namespaceURI.equals("http://www.w3.org/XML/1998/namespace"))))) {
    	    throw new DOMExceptionImpl(DOMException.NAMESPACE_ERR, 
				       "NAMESPACE_ERR");
    	}
	if (ownerDocument.errorChecking && !DocumentImpl.isXMLName(prefix)) {
    	    throw new DOMExceptionImpl(DOMException.INVALID_CHARACTER_ERR, 
    	                               "INVALID_CHARACTER_ERR");
        }
        this.prefix = prefix;
    }
                                        
    /** 
     * Introduced in DOM Level 2. <p>
     *
     * Returns the local part of the qualified name of this node.
     * @since WD-DOM-Level-2-19990923
     */
    public String getLocalName()
    {
        if (syncData) {
            synchronizeData();
        }
        return localName;
    }
}
