/**
 * This software is Copyright (C) 2020 Tod G. Harter. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.giantelectronicbrain.catfood.client.chunk.syntax.html;

import com.giantelectronicbrain.catfood.client.chunk.Chunk;
import com.giantelectronicbrain.catfood.client.chunk.Chunk.Language;
import com.giantelectronicbrain.catfood.client.chunk.syntax.Syntax;
import com.giantelectronicbrain.catfood.client.chunk.syntax.SyntaxRegistry;

/**
 * Markdown syntax handler. This one attempts to use markdown-it, but it doesn't really work...
 * 
 * @author tharter
 *
 */
public class Html implements Syntax {
	static {
		SyntaxRegistry.register(Language.HTML.name(), new Html());
	}

	public Html() {
	}
	
	@Override
	public String generateHTML(Chunk chunk) {
		StringBuilder sb = new StringBuilder();
		sb.append("<h1>");
		sb.append(chunk.getName());
		sb.append("</h1>");
		sb.append("<div>");
		sb.append(chunk.getContent());
		sb.append("</div>");
		return sb.toString();
	}
}
