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
package com.giantelectronicbrain.catfood.hairball;

import java.util.function.Consumer;

/**
 * A token which simply executes native Java code. Its behavior is a lambda which
 * implements the Consumer<Interpreter> functional interface. Any inputs or outputs
 * will be to the stack, or operations on the Context, etc.
 * 
 * @author tharter
 *
 */
public class NativeToken implements Token {
	private final Consumer<Interpreter> behavior;

	public NativeToken(Consumer<Interpreter> behavior) {
		this.behavior = behavior;
	}
	
	@Override
	public void execute(Interpreter interpreter) {
		behavior.accept(interpreter);
	}

}
