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

import com.giantelectronicbrain.catfood.client.chunk.Chunk;
import com.giantelectronicbrain.catfood.client.chunk.ChunkId;
import com.giantelectronicbrain.catfood.client.chunk.Chunk.Language;
import com.giantelectronicbrain.catfood.initialization.IInitializer;
import com.giantelectronicbrain.catfood.initialization.InitializationException;
import com.giantelectronicbrain.catfood.initialization.InitializerFactory;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Data access layer for CatFood data storage using OrientDB.
 * 
 * @author tharter
 *
 */
public class OrientDBStore implements ICatFoodDBStore {
	private static Logger LOGGER = LoggerFactory.getLogger(OrientDBStore.class);
	
	private IInitializer initializer; // = InitializerFactory.getInitializer();
	
	private ODatabaseDocumentTx db;
	private String dbUrl; // = (String) initializer.get(InitializerFactory.ORIENTDB_URL);
	private String dbUser; // = (String) initializer.get(InitializerFactory.ORIENTDB_USER);
	private String dbPassword; // = (String) initializer.get(InitializerFactory.ORIENTDB_PASSWORD);

	/**
	 * Instantiate database connector. It is necessary to call start in order to initialize
	 * the connection.
	 * 
	 * @throws InitializationException 
	 */
	public OrientDBStore() throws InitializationException {
		initializer = InitializerFactory.getInitializer();
		
		dbUrl = (String) initializer.get(InitializerFactory.ORIENTDB_URL);
		dbUser = (String) initializer.get(InitializerFactory.ORIENTDB_USER);
		dbPassword = (String) initializer.get(InitializerFactory.ORIENTDB_PASSWORD);
	}

	/**
	 * Open database connection.
	 */
	@Override
	public void start() {
		LOGGER.info("Initializing OrientDB connection to "+dbUrl);
		db = new ODatabaseDocumentTx(dbUrl);
		db.open(dbUser,dbPassword);
	}

	/**
	 * Shut down database connection.
	 */
	@Override
	public void stop() {
		db.close();
	}

	/*
	 * (non-Javadoc)
	 * @see com.giantelectronicbrain.catfood.store.ICatFoodDBStore#getJsonContent(com.giantelectronicbrain.catfood.model.ChunkId)
	 */
	@Override
	public String getJsonContent(ChunkId id) {
		LOGGER.debug("Getting Content for id "+id);
		
		ODatabaseRecordThreadLocal.instance().set(db);
		OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT FROM Topic WHERE @RID = ?");
		List<ODocument> result = db.command(query).execute(id.getChunkId());
		
		LOGGER.debug("Got list of results of size "+result.size());
		LOGGER.debug("Result 0 is "+result.get(0).toString());

		return result.isEmpty() ? "" : result.get(0).toJSON();
	}

	/*
	 * (non-Javadoc)
	 * @see com.giantelectronicbrain.catfood.store.ICatFoodDBStore#getContent(com.giantelectronicbrain.catfood.model.ChunkId)
	 */
	@Override
	public Chunk getContent(ChunkId id) {
		LOGGER.debug("Getting Content for id "+id);
		
		Chunk result = null;
		ODatabaseRecordThreadLocal.instance().set(db);
		OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT FROM Topic WHERE @RID = ?");
		List<ODocument> dbResults = db.command(query).execute(id);
		if(dbResults.isEmpty()) {
			
		} else {
			ODocument dbResult = dbResults.get(0);
			result = new Chunk(dbResult.field("content"),new ChunkId(dbResult.getIdentity().toString()),dbResult.field("name"),Language.valueOf(dbResult.field("lang")));
			LOGGER.debug("Got list of results of size "+dbResults.size());
			LOGGER.debug("Result 0 is "+dbResult.toString());
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.giantelectronicbrain.catfood.store.ICatFoodDBStore#getJsonTopic(java.lang.String)
	 */
	@Override
	public String getJsonChunk(String name) {
		LOGGER.debug("Getting Topic for name "+name);
		
		ODatabaseRecordThreadLocal.instance().set(db);
		OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT FROM Topic WHERE name = ?");
		List<ODocument> result = db.command(query).execute(name);
		
		LOGGER.debug("Got list of results of size "+result.size());
		if(result.size() > 0)
			LOGGER.debug("Result 0 is "+result.get(0).toString());

		return result.isEmpty() ? null : result.get(0).toJSON();
	}

}
