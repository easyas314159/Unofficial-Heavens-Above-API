package com.uhaapi.server.util;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.nodes.TextNode;

public class WhitespaceFilter implements NodeFilter {
	public boolean accept(Node node) {
		if(node instanceof TextNode) {
			return ((TextNode)node).isWhiteSpace();
		}
		return false;
	}
}
