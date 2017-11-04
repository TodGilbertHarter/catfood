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

package com.giantelectronicbrain.catfood.store;

import java.util.List;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Data access layer for CatFood data storage.
 * 
 * @author tharter
 *
 */
public class TestDB {
	private static Logger LOGGER = LoggerFactory.getLogger(TestDB.class);
	
	ODatabaseDocumentTx db;

	public void start() {
		db = new ODatabaseDocumentTx("plocal:orientdb/databases/CatFood");
		db.open("admin","admin");
	}

	public void stop() {
		db.close();
	}

	/**
	 * 
	 */
	public TestDB() {
	}

	public String getTest() {
		ODatabaseRecordThreadLocal.INSTANCE.set(db);
		StringBuffer sb = new StringBuffer("[");
		for(ODocument doc : db.browseClass("Test")) {
			sb.append(doc.toJSON());
		}
		sb.append("]");
		return sb.toString();
	}

	public String getContent(String id) {
		LOGGER.debug("Getting Content for id "+id);
		
		ODatabaseRecordThreadLocal.INSTANCE.set(db);
		OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT FROM Topic WHERE @RID = ?");
		List<ODocument> result = db.command(query).execute(id);
		
		LOGGER.debug("Got list of results of size "+result.size());
		LOGGER.debug("Result 0 is "+result.get(0).toString());

		return result.isEmpty() ? "" : result.get(0).toJSON();
	}
}
