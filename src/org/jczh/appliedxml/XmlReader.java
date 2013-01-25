package org.jczh.appliedxml;

import java.io.IOException;

abstract class XmlReader {

	/**
	 * This is used to take the next node from the document. This will scan
	 * through the document, ignoring any comments to find the next relevant XML
	 * event to acquire. Typically events will be the start and end of an
	 * element, as well as any text nodes.
	 * 
	 * @return this returns the next event taken from the source XML
	 */
	abstract EventNode next() throws IOException;

	/**
	 * This is used to peek at the node from the document. This will scan
	 * through the document, ignoring any comments to find the next relevant XML
	 * event to acquire. Typically events will be the start and end of an
	 * element, as well as any text nodes.
	 * 
	 * @return this returns the next event taken from the source XML
	 */
	abstract EventNode peek() throws IOException;

	abstract EventNode last();

	abstract boolean hasNext();

}