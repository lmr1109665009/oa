//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.03.13 at 11:13:53 ���� CST 
//


package com.suneee.core.bpmn20.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for tItemDefinition complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tItemDefinition">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/spec/BPMN/20100524/MODEL}tRootElement">
 *       &lt;attribute name="structureRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="isCollection" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="itemKind" type="{http://www.omg.org/spec/BPMN/20100524/MODEL}tItemKind" default="Information" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tItemDefinition")
public class ItemDefinition
    extends RootElement
{

    @XmlAttribute
    protected QName structureRef;
    @XmlAttribute
    protected Boolean isCollection;
    @XmlAttribute
    protected ItemKind itemKind;

    /**
     * Gets the value of the structureRef property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getStructureRef() {
        return structureRef;
    }

    /**
     * Sets the value of the structureRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setStructureRef(QName value) {
        this.structureRef = value;
    }

    /**
     * Gets the value of the isCollection property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsCollection() {
        if (isCollection == null) {
            return false;
        } else {
            return isCollection;
        }
    }

    /**
     * Sets the value of the isCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsCollection(Boolean value) {
        this.isCollection = value;
    }

    /**
     * Gets the value of the itemKind property.
     * 
     * @return
     *     possible object is
     *     {@link ItemKind }
     *     
     */
    public ItemKind getItemKind() {
        if (itemKind == null) {
            return ItemKind.INFORMATION;
        } else {
            return itemKind;
        }
    }

    /**
     * Sets the value of the itemKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemKind }
     *     
     */
    public void setItemKind(ItemKind value) {
        this.itemKind = value;
    }

}
