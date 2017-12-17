/**
 * This software is Copyright (C) 2016 Tod G. Harter. All rights reserved.
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

package com.giantelectronicbrain.catfood.templ.impl;

/**
 * @author tharter
 *
 */
public class ProcessedJSX {

    public final String name;
    public final byte[] source;
    public final long lastModified;

    ProcessedJSX(String name, byte[] source, long lastModified) {
      if(name == null) {
        throw new IllegalArgumentException("name == null");
      }
      if(source == null) {
        throw new IllegalArgumentException("source == null");
      }
      if(lastModified < -1L) {
        throw new IllegalArgumentException("lastModified < -1L");
      }
      this.name = name;
      this.source = source;
      this.lastModified = lastModified;
    }

    @Override
	public boolean equals(Object obj) {
      return obj instanceof ProcessedJSX && name.equals(((ProcessedJSX) obj).name);
    }

    @Override
	public int hashCode() {
      return name.hashCode();
    }

}
