package com.uhaapi.server.util;

import org.apache.commons.lang3.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.nodes.TextNode;

public class WhitespaceFilter implements NodeFilter {
	@Override
	public boolean accept(Node node) {
		if(node instanceof TextNode) {
			return !StringUtils.isWhitespace(((TextNode)node).getText());
		}
		return true;
	}
}
