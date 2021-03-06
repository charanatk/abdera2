/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 */
package org.apache.abdera2.parser.axiom;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.apache.abdera2.common.Constants;
import org.apache.abdera2.common.mediatype.MimeTypeHelper;
import org.apache.abdera2.common.selector.Selector;
import org.apache.abdera2.common.iri.IRI;
import org.apache.abdera2.extra.InputStreamDataSource;
import org.apache.abdera2.model.Categories;
import org.apache.abdera2.model.Category;
import org.apache.abdera2.model.Content;
import org.apache.abdera2.model.Control;
import org.apache.abdera2.model.DateTime;
import org.apache.abdera2.model.Div;
import org.apache.abdera2.model.Document;
import org.apache.abdera2.model.Element;
import org.apache.abdera2.model.Entry;
import org.apache.abdera2.model.Feed;
import org.apache.abdera2.model.IRIElement;
import org.apache.abdera2.model.Link;
import org.apache.abdera2.model.Person;
import org.apache.abdera2.model.Source;
import org.apache.abdera2.model.Text;
import org.apache.abdera2.model.Content.Type;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;

import com.google.common.collect.Iterables;

import static com.google.common.base.Preconditions.*;

@SuppressWarnings({"deprecation","rawtypes"})
public class FOMEntry extends FOMExtensibleElement implements Entry {

    private static final long serialVersionUID = 1L;

    public FOMEntry() {
        super(Constants.ENTRY, new FOMDocument<Entry>(), new FOMFactory());
    }

    public FOMEntry(String name, OMNamespace namespace, OMContainer parent, OMFactory factory) throws OMException {
        super(name, namespace, parent, factory);
    }

    public FOMEntry(QName qname, OMContainer parent, OMFactory factory) {
        super(qname, parent, factory);
    }

    public FOMEntry(QName qname, OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) {
        super(qname, parent, factory, builder);
    }

    public FOMEntry(OMContainer parent, OMFactory factory) throws OMException {
        super(ENTRY, parent, factory);
    }

    public FOMEntry(OMContainer parent, OMFactory factory, OMXMLParserWrapper builder) throws OMException {
        super(ENTRY, parent, factory, builder);
    }

    public Person getAuthorInherited() {
      Person person = getAuthor();
      if (person == null) {
        Source source = getSource();
        if (source == null) {
          Element parent = getParentElement();
          if (parent != null && parent instanceof Source)
            source = (Source) parent;
        }
        if (source != null)
          person = source.getAuthor();
      }
      return person;
    }
    
    private boolean is_empty(List<?> list) {
      if (list == null) return true;
      return list.isEmpty();
    }
    
    public List<Person> getAuthorsInherited() {
      List<Person>list = getAuthors();
      if (is_empty(list)) {
        Source source = getSource();
        if (source == null) {
          if (this.getParent() instanceof Element) {
            Element parent = getParentElement();
            if (parent != null && parent instanceof Source)
              source = (Source) parent;
          }
        }
        if (source != null)
          list = source.getAuthors();
      }
      return list;
    }
    
    public List<Person> getAuthorsInherited(Selector selector) {
      List<Person>list = getAuthors(selector);
      if (is_empty(list)) {
        Source source = getSource();
        if (source == null) {
          Element parent = getParentElement();
          if (parent != null && parent instanceof Source)
            source = (Source) parent;
        }
        if (source != null)
          list = source.getAuthors(selector);
      }
      return list;
    }
    
    public Person getAuthor() {
        return (Person)getFirstChildWithName(AUTHOR);
    }

    public List<Person> getAuthors() {
        return _getChildrenAsSet(AUTHOR);
    }

    public Entry addAuthor(Person person) {
        complete();
        addChild((OMElement)person);
        return this;
    }

    public Person addAuthor(String name) {
        complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Person person = fomfactory.newAuthor(this);
        person.setName(name);
        return person;
    }

    public Person addAuthor(String name, String email, String uri) {
        complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Person person = fomfactory.newAuthor(this);
        person.setName(name);
        person.setEmail(email);
        person.setUri(uri);
        return person;
    }

    public List<Category> getCategories() {
        return _getChildrenAsSet(CATEGORY);
    }

    public List<Category> getCategories(String scheme) {
        return FOMElement.getCategories(this, scheme);
    }

    public Entry addCategory(Category category) {
        complete();
        Element el = category.getParentElement();
        if (el != null && el instanceof Categories) {
            Categories cats = category.getParentElement();
            category = (Category)category.clone();
            try {
                if (category.getScheme() == null && cats.getScheme() != null)
                    category.setScheme(cats.getScheme().toString());
            } catch (Exception e) {
                // Do nothing, shouldn't happen
            }
        }
        addChild((OMElement)category);
        return this;
    }

    public Category addCategory(String term) {
        complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Category category = factory.newCategory(this);
        category.setTerm(term);
        return category;
    }

    public Category addCategory(String scheme, String term, String label) {
        complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Category category = factory.newCategory(this);
        category.setTerm(term);
        category.setScheme(scheme);
        category.setLabel(label);
        return category;
    }

    public Content getContentElement() {
        return (Content)getFirstChildWithName(CONTENT);
    }

    public Entry setContentElement(Content content) {
        complete();
        if (content != null) {
            _setChild(CONTENT, (OMElement)content);
        } else {
            _removeChildren(CONTENT, false);
        }
        return this;
    }

    /**
     * Sets the content for this entry as @type="text"
     */
    public Content setContent(String value) {
        complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Content content = factory.newContent();
        content.setValue(value);
        setContentElement(content);
        return content;
    }

    public Content setContentAsHtml(String value) {
        return setContent(value, Content.Type.HTML);
    }

    public Content setContentAsXhtml(String value) {
        return setContent(value, Content.Type.XHTML);
    }

    /**
     * Sets the content for this entry
     */
    public Content setContent(String value, Content.Type type) {
        FOMFactory factory = (FOMFactory)this.factory;
        Content content = factory.newContent(type);
        content.setValue(value);
        setContentElement(content);
        return content;
    }

    /**
     * Sets the content for this entry
     */
    public Content setContent(Element value) {
        FOMFactory factory = (FOMFactory)this.factory;
        Content content = factory.newContent();
        content.setValueElement(value);
        setContentElement(content);
        return content;
    }

    /**
     * Sets the content for this entry
     * 
     * @throws MimeTypeParseException
     */
    public Content setContent(Element element, String mediaType) {
        try {
            if (MimeTypeHelper.isText(mediaType))
                throw new IllegalArgumentException();
            FOMFactory factory = (FOMFactory)this.factory;
            Content content = factory.newContent(new MimeType(mediaType));
            content.setValueElement(element);
            setContentElement(content);
            return content;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera2.common.mediatype.MimeTypeParseException(e);
        }
    }

    /**
     * Sets the content for this entry
     * 
     * @throws MimeTypeParseException
     */
    public Content setContent(DataHandler dataHandler) {
        return setContent(dataHandler, dataHandler.getContentType());
    }

    /**
     * Sets the content for this entry
     * 
     * @throws MimeTypeParseException
     */
    public Content setContent(DataHandler dataHandler, String mediatype) {
        if (MimeTypeHelper.isText(mediatype)) {
            try {
                return setContent(dataHandler.getInputStream(), mediatype);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            FOMFactory factory = (FOMFactory)this.factory;
            Content content = factory.newContent(Content.Type.MEDIA);
            content.setDataHandler(dataHandler);
            if (mediatype != null)
                content.setMimeType(mediatype);
            setContentElement(content);
            return content;
        }
    }

    /**
     * Sets the content for this entry
     */
    public Content setContent(InputStream in) {
        InputStreamDataSource ds = new InputStreamDataSource(in);
        DataHandler dh = new DataHandler(ds);
        Content content = setContent(dh);
        return content;
    }

    /**
     * Sets the content for this entry
     */
    public Content setContent(InputStream in, String mediatype) {
        if (MimeTypeHelper.isText(mediatype)) {
            try {
                StringBuilder buf = new StringBuilder();
                String charset = MimeTypeHelper.getCharset(mediatype);
                Document<?> doc = this.getDocument();
                charset = charset != null ? charset : doc != null ? doc.getCharset() : null;
                charset = charset != null ? charset : "UTF-8";
                InputStreamReader isr = new InputStreamReader(in, charset);
                char[] data = new char[500];
                int r = -1;
                while ((r = isr.read(data)) != -1) {
                    buf.append(data, 0, r);
                }
                return setContent(buf.toString(), mediatype);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            InputStreamDataSource ds = new InputStreamDataSource(in, mediatype);
            DataHandler dh = new DataHandler(ds);
            return setContent(dh, mediatype);
        }
    }

    /**
     * Sets the content for this entry
     * 
     * @throws MimeTypeParseException
     */
    public Content setContent(String value, String mediatype) {
        try {
            FOMFactory factory = (FOMFactory)this.factory;
            Content content = factory.newContent(new MimeType(mediatype));
            content.setValue(value);
            content.setMimeType(mediatype);
            setContentElement(content);
            return content;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera2.common.mediatype.MimeTypeParseException(e);
        }
    }

    /**
     * Sets the content for this entry
     * 
     * @throws MimeTypeParseException
     * @throws IRISyntaxException
     */
    public Content setContent(IRI uri, String mediatype) {
        try {
            FOMFactory factory = (FOMFactory)this.factory;
            Content content = factory.newContent(new MimeType(mediatype));
            content.setSrc(uri.toString());
            setContentElement(content);
            return content;
        } catch (javax.activation.MimeTypeParseException e) {
            throw new org.apache.abdera2.common.mediatype.MimeTypeParseException(e);
        }
    }

    public List<Person> getContributors() {
        return _getChildrenAsSet(CONTRIBUTOR);
    }

    public Entry addContributor(Person person) {
        complete();
        addChild((OMElement)person);
        return this;
    }

    public Person addContributor(String name) {
        complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Person person = fomfactory.newContributor(this);
        person.setName(name);
        return person;
    }

    public Person addContributor(String name, String email, String uri) {
        complete();
        FOMFactory fomfactory = (FOMFactory)this.factory;
        Person person = fomfactory.newContributor(this);
        person.setName(name);
        person.setEmail(email);
        person.setUri(uri);
        return person;
    }

    public IRIElement getIdElement() {
        return (IRIElement)getFirstChildWithName(ID);
    }

    public Entry setIdElement(IRIElement id) {
        complete();
        if (id != null)
            _setChild(ID, (OMElement)id);
        else
            _removeChildren(ID, false);
        return this;
    }

    public IRI getId() {
        IRIElement id = getIdElement();
        return (id != null) ? id.getValue() : null;
    }

    public IRIElement setId(String value) {
        return setId(value, false);
    }

    public IRIElement newId() {
        return setId(this.getFactory().newUuidUri(), false);
    }

    public IRIElement setId(String value, boolean normalize) {
        complete();
        if (value == null) {
            _removeChildren(ID, false);
            return null;
        }
        IRIElement id = getIdElement();
        if (id != null) {
            if (normalize)
                id.setNormalizedValue(value);
            else
                id.setValue(value);
            return id;
        } else {
            FOMFactory fomfactory = (FOMFactory)factory;
            IRIElement iri = fomfactory.newID(this);
            iri.setValue((normalize) ? IRI.normalizeString(value) : value);
            return iri;
        }
    }

    public List<Link> getLinks() {
        return _getChildrenAsSet(LINK);
    }

    public List<Link> getLinks(String rel) {
        return getLinks(this, rel);
    }

    public List<Link> getLinks(String... rels) {
        return FOMElement.getLinks(this, rels);
    }

    public Entry addLink(Link link) {
        complete();
        addChild((OMElement)link);
        return this;
    }

    public Link addLink(String href) {
        complete();
        return addLink(href, null);
    }

    public Link addLink(String href, String rel) {
        complete();
        FOMFactory fomfactory = (FOMFactory)factory;
        Link link = fomfactory.newLink(this);
        link.setHref(href);
        if (rel != null)
            link.setRel(rel);
        return link;
    }

    public Link addLink(String href, String rel, String type, String title, String hreflang, long length) {
        complete();
        FOMFactory fomfactory = (FOMFactory)factory;
        Link link = fomfactory.newLink(this);
        link.setHref(href);
        link.setRel(rel);
        link.setMimeType(type);
        link.setTitle(title);
        link.setHrefLang(hreflang);
        link.setLength(length);
        return link;
    }

    public DateTime getPublishedElement() {
        return (DateTime)getFirstChildWithName(PUBLISHED);
    }

    public Entry setPublishedElement(DateTime dateTime) {
        complete();
        if (dateTime != null)
            _setChild(PUBLISHED, (OMElement)dateTime);
        else
            _removeChildren(PUBLISHED, false);
        return this;
    }

    public org.joda.time.DateTime getPublished() {
        DateTime dte = getPublishedElement();
        return (dte != null) ? dte.getValue() : null;
    }

    public DateTime setPublished(org.joda.time.DateTime value) {
        complete();
        if (value == null) {
            _removeChildren(PUBLISHED, false);
            return null;
        }
        DateTime dte = getPublishedElement();
        if (dte != null) {
            dte.setValue(value);
            return dte;
        } else {
            FOMFactory fomfactory = (FOMFactory)factory;
            DateTime dt = fomfactory.newPublished(this);
            dt.setValue(value);
            return dt;
        }
    }

    public DateTime setPublished(java.util.Date date) {
        return setPublished(new org.joda.time.DateTime(date));
    }

    public DateTime setPublished(String value) {
        return setPublished((value != null) ? new org.joda.time.DateTime(value) : null);
    }

    public Text getRightsElement() {
        return getTextElement(RIGHTS);
    }

    public Entry setRightsElement(Text text) {
        complete();
        setTextElement(RIGHTS, text, false);
        return this;
    }

    public Text setRights(String value) {
        complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newRights();
        text.setValue(value);
        setRightsElement(text);
        return text;
    }

    public Text setRightsAsHtml(String value) {
        return setRights(value, Text.Type.HTML);
    }

    public Text setRightsAsXhtml(String value) {
        return setRights(value, Text.Type.XHTML);
    }

    public Text setRights(String value, Text.Type type) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newRights(type);
        text.setValue(value);
        setRightsElement(text);
        return text;
    }

    public Text setRights(Div value) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newRights(Text.Type.XHTML);
        text.setValueElement(value);
        setRightsElement(text);
        return text;
    }

    public String getRights() {
        return getText(RIGHTS);
    }

    public Source getSource() {
        return (Source)getFirstChildWithName(SOURCE);
    }

    public Entry setSource(Source source) {
        complete();
        if (source != null) {
            if (source instanceof Feed)
                source = ((Feed)source).getAsSource();
            _setChild(SOURCE, (OMElement)source);
        } else {
            _removeChildren(SOURCE, false);
        }
        return this;
    }

    public Text getSummaryElement() {
        return getTextElement(SUMMARY);
    }

    public Entry setSummaryElement(Text text) {
        complete();
        setTextElement(SUMMARY, text, false);
        return this;
    }

    public Text setSummary(String value) {
        complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newSummary();
        text.setValue(value);
        setSummaryElement(text);
        return text;
    }

    public Text setSummaryAsHtml(String value) {
        return setSummary(value, Text.Type.HTML);
    }

    public Text setSummaryAsXhtml(String value) {
        return setSummary(value, Text.Type.XHTML);
    }

    public Text setSummary(String value, Text.Type type) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newSummary(type);
        text.setValue(value);
        setSummaryElement(text);
        return text;
    }

    public Text setSummary(Div value) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newSummary(Text.Type.XHTML);
        text.setValueElement(value);
        setSummaryElement(text);
        return text;
    }

    public String getSummary() {
        return getText(SUMMARY);
    }

    public Text getTitleElement() {
        return getTextElement(TITLE);
    }

    public Entry setTitleElement(Text title) {
        complete();
        setTextElement(TITLE, title, false);
        return this;
    }

    public Text setTitle(String value) {
        complete();
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newTitle();
        text.setValue(value);
        setTitleElement(text);
        return text;
    }

    public Text setTitleAsHtml(String value) {
        return setTitle(value, Text.Type.HTML);
    }

    public Text setTitleAsXhtml(String value) {
        return setTitle(value, Text.Type.XHTML);
    }

    public Text setTitle(String value, Text.Type type) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newTitle(type);
        text.setValue(value);
        setTitleElement(text);
        return text;
    }

    public Text setTitle(Div value) {
        FOMFactory factory = (FOMFactory)this.factory;
        Text text = factory.newTitle(Text.Type.XHTML);
        text.setValueElement(value);
        setTitleElement(text);
        return text;
    }

    public String getTitle() {
        return getText(TITLE);
    }

    public DateTime getUpdatedElement() {
        return (DateTime)getFirstChildWithName(UPDATED);
    }

    public Entry setUpdatedElement(DateTime updated) {
        complete();
        if (updated != null)
            _setChild(UPDATED, (OMElement)updated);
        else
            _removeChildren(UPDATED, false);
        return this;
    }

    public org.joda.time.DateTime getUpdated() {
        DateTime dte = getUpdatedElement();
        return (dte != null) ? dte.getValue() : null;
    }

    public DateTime setUpdated(org.joda.time.DateTime value) {
        complete();
        if (value == null) {
            _removeChildren(UPDATED, false);
            return null;
        }
        DateTime dte = getUpdatedElement();
        if (dte != null) {
            dte.setValue(value);
            return dte;
        } else {
            FOMFactory fomfactory = (FOMFactory)factory;
            DateTime dt = fomfactory.newUpdated(this);
            dt.setValue(value);
            return dt;
        }
    }

    public DateTime setUpdated(Date value) {
        return setUpdated(new org.joda.time.DateTime(value));
    }

    public DateTime setUpdated(String value) {
        return setUpdated((value != null) ? new org.joda.time.DateTime(value) : null);
    }

    public DateTime getEditedElement() {
        DateTime dt = (DateTime)getFirstChildWithName(EDITED);
        if (dt == null)
            dt = (DateTime)getFirstChildWithName(PRE_RFC_EDITED);
        return dt;
    }

    public void setEditedElement(DateTime updated) {
        complete();
        declareNamespace(APP_NS, "app");
        _removeChildren(PRE_RFC_EDITED, false);
        if (updated != null)
            _setChild(EDITED, (OMElement)updated);
        else
            _removeChildren(EDITED, false);
    }

    public org.joda.time.DateTime getEdited() {
        DateTime dte = getEditedElement();
        return (dte != null) ? dte.getValue() : null;
    }

    public DateTime setEdited(org.joda.time.DateTime value) {
        complete();
        declareNamespace(APP_NS, "app");
        if (value == null) {
            _removeChildren(PRE_RFC_EDITED, false);
            _removeChildren(EDITED, false);
            return null;
        }
        DateTime dte = getEditedElement();
        if (dte != null) {
            dte.setValue(value);
            return dte;
        } else {
            FOMFactory fomfactory = (FOMFactory)factory;
            DateTime dt = fomfactory.newEdited(this);
            dt.setValue(value);
            return dt;
        }
    }

    public DateTime setEdited(Date value) {
        return setEdited(new org.joda.time.DateTime(value));
    }

    public DateTime setEdited(String value) {
        return setUpdated((value != null) ? new org.joda.time.DateTime(value) : null);
    }

    public Control getControl(boolean create) {
        Control control = getControl();
        if (control == null && create) {
            control = getFactory().newControl();
            setControl(control);
        }
        return control;
    }

    public Control getControl() {
        Control control = (Control)getFirstChildWithName(CONTROL);
        if (control == null)
            control = (Control)getFirstChildWithName(PRE_RFC_CONTROL);
        return control;
    }

    public Entry setControl(Control control) {
        complete();
        _removeChildren(PRE_RFC_CONTROL, true);
        if (control != null)
            _setChild(CONTROL, (OMElement)control);
        else
            _removeChildren(CONTROL, false);
        return this;
    }

    public Link getLink(String rel) {
        List<Link> links = getLinks(rel);
        Link link = null;
        if (links.size() > 0)
            link = links.get(0);
        return link;
    }

    public Link getAlternateLink() {
        return getLink(Link.REL_ALTERNATE);
    }

    public Link getEnclosureLink() {
        return getLink(Link.REL_ENCLOSURE);
    }

    public Link getEditLink() {
        return getLink(Link.REL_EDIT);
    }

    public Link getSelfLink() {
        return getLink(Link.REL_SELF);
    }

    public Link getEditMediaLink() {
        return getLink(Link.REL_EDIT_MEDIA);
    }

    public IRI getLinkResolvedHref(String rel) {
        Link link = getLink(rel);
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getAlternateLinkResolvedHref() {
        Link link = getAlternateLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getEnclosureLinkResolvedHref() {
        Link link = getEnclosureLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getEditLinkResolvedHref() {
        Link link = getEditLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getEditMediaLinkResolvedHref() {
        Link link = getEditMediaLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public IRI getSelfLinkResolvedHref() {
        Link link = getSelfLink();
        return (link != null) ? link.getResolvedHref() : null;
    }

    public String getContent() {
        Content content = getContentElement();
        return (content != null) ? content.getValue() : null;
    }

    public InputStream getContentStream() throws IOException {
        Content content = getContentElement();
        DataHandler dh = content.getDataHandler();
        return dh.getInputStream();
    }

    public IRI getContentSrc() {
        Content content = getContentElement();
        return (content != null) ? content.getResolvedSrc() : null;
    }

    public Type getContentType() {
        Content content = getContentElement();
        return (content != null) ? content.getContentType() : null;
    }

    public Text.Type getRightsType() {
        Text text = getRightsElement();
        return (text != null) ? text.getTextType() : null;
    }

    public Text.Type getSummaryType() {
        Text text = getSummaryElement();
        return (text != null) ? text.getTextType() : null;
    }

    public Text.Type getTitleType() {
        Text text = getTitleElement();
        return (text != null) ? text.getTextType() : null;
    }

    public MimeType getContentMimeType() {
        Content content = getContentElement();
        return (content != null) ? content.getMimeType() : null;
    }

    public Link getAlternateLink(String type, String hreflang) {
        return selectLink(getLinks(Link.REL_ALTERNATE), type, hreflang);
    }

    public IRI getAlternateLinkResolvedHref(String type, String hreflang) {
        Link link = getAlternateLink(type, hreflang);
        return (link != null) ? link.getResolvedHref() : null;
    }

    public Link getEditMediaLink(String type, String hreflang) {
        return selectLink(getLinks(Link.REL_EDIT_MEDIA), type, hreflang);
    }

    public IRI getEditMediaLinkResolvedHref(String type, String hreflang) {
        Link link = getEditMediaLink(type, hreflang);
        return (link != null) ? link.getResolvedHref() : null;
    }

    public Entry setDraft(boolean draft) {
        complete();
        Control control = getControl();
        if (control == null && draft) {
            control = ((FOMFactory)factory).newControl(this);
        }
        if (control != null)
            control.setDraft(draft);
        return this;
    }

    /**
     * Returns true if this entry is a draft
     */
    public boolean isDraft() {
        Control control = getControl();
        return (control != null) ? control.isDraft() : false;
    }

    public Control addControl() {
        complete();
        Control control = getControl();
        if (control == null) {
            control = ((FOMFactory)factory).newControl(this);
        }
        return control;
    }

    public List<Person> getAuthors(Selector selector) {
      return _getChildrenAsSet(AUTHOR,selector);
    }

    public List<Category> getCategories(Selector selector) {
      return _getChildrenAsSet(CATEGORY,selector);
    }

    public List<Person> getContributors(Selector selector) {
      return _getChildrenAsSet(CONTRIBUTOR,selector);
    }

    public List<Link> getLinks(Selector selector) {
      return _getChildrenAsSet(LINK,selector);
    }

    public DateTime setPublishedNow() {
      return setPublished(org.joda.time.DateTime.now());
    }

    public DateTime setUpdatedNow() {
      return setUpdated(org.joda.time.DateTime.now());
    }

    public DateTime setEditedNow() {
      return setEdited(org.joda.time.DateTime.now());
    }

    public Link addLink(IRI href) {
      checkNotNull(href);
      return addLink(href.toString());
    }

    public Link addLink(IRI href, String rel) {
      checkNotNull(href);
      return addLink(href.toString(),rel);
    }

    public Link addLink(IRI href, String rel, String type, String title,
        String hreflang, long length) {
      checkNotNull(href);
      return addLink(href.toString(),rel,type,title,hreflang,length);
    }
}
