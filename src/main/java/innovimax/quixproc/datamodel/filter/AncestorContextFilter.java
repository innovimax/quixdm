/*
QuiXProc: efficient evaluation of XProc Pipelines.
Copyright (C) 2011-2015 Innovimax
All rights reserved.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package innovimax.quixproc.datamodel.filter;

import java.util.Iterator;
import java.util.Stack;

import innovimax.quixproc.datamodel.IQuiXStream;
import innovimax.quixproc.datamodel.IQuiXToken;
import innovimax.quixproc.datamodel.QuiXQName;
import innovimax.quixproc.datamodel.event.AQuiXEvent;
import innovimax.quixproc.datamodel.event.IQuiXEvent;

public class AncestorContextFilter extends AQuiXEventStreamFilter {

	private Stack<QuiXQName> ancestors;

	public AncestorContextFilter(IQuiXStream<IQuiXToken> stream) {
		super(stream);
		this.ancestors = new Stack<QuiXQName>();
	}

	@Override
	public IQuiXToken process(IQuiXToken item) {
		switch (item.getType()) {
		case START_ELEMENT:
			//this.ancestors.push(qevent.asNamedEvent().getQName());
			break;
		case END_ELEMENT:
			//this.ancestors.pop();
			break;
		default:
			break;
		}
		return item;
	}

	public Iterator<QuiXQName> ancestors() {
		return this.ancestors.iterator();
	}
}
