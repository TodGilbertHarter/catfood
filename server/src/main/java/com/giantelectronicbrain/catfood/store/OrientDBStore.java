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

import com.giantelectronicbrain.catfood.client.chunk.Chunk;
import com.giantelectronicbrain.catfood.client.chunk.Chunk.Language;
import com.giantelectronicbrain.catfood.client.chunk.ChunkId;
import com.giantelectronicbrain.catfood.initialization.IInitializer;
import com.giantelectronicbrain.catfood.initialization.InitializationException;
import com.giantelectronicbrain.catfood.initialization.InitializerFactory;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.ODatabasePool;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.db.OrientDBConfigBuilder;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.OElement;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Data access layer for CatFood data storage using OrientDB. This acts as both a 'pool' and 
 * performs the functions of a repository.
 * 
 * @author tharter
 *
 */
public class OrientDBStore implements ICatFoodDBStore {
	private static Logger LOGGER = LoggerFactory.getLogger(OrientDBStore.class);
	
	private IInitializer initializer; // = InitializerFactory.getInitializer();
	
	private OrientDB db;
	private ODatabasePool dbPool;
	private String dbUrl; // = (String) initializer.get(InitializerFactory.ORIENTDB_URL);
	private String dbUser; // = (String) initializer.get(InitializerFactory.ORIENTDB_USER);
	private String dbPassword; // = (String) initializer.get(InitializerFactory.ORIENTDB_PASSWORD);
	private String dbName;

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
		dbName = (String) initializer.get(InitializerFactory.ORIENTDB_DATABASE);
	}

	/**
	 * Open database connection.
	 */
	@Override
	public void start() {
		LOGGER.info("Initializing OrientDB connection to "+dbUrl);
		db = new OrientDB(dbUrl,dbUser,dbPassword,OrientDBConfig.defaultConfig());
		
		OrientDBConfigBuilder poolCfg = OrientDBConfig.builder();
		poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MIN, 5);
		poolCfg.addConfig(OGlobalConfiguration.DB_POOL_MAX, 10);
		
		dbPool = new ODatabasePool(db,dbName,dbUser,dbPassword, poolCfg.build());
	}

	/**
	 * Shut down database connection.
	 */
	@Override
	public void stop() {
		dbPool.close();
		db.close();
	}

	@Override
	public void putContent(Chunk chunk) throws StorageException {
		ChunkId chunkId = chunk.getChunkId();
		if(chunkId == null || chunkId.getChunkId() == null || chunkId.getChunkId().isBlank())
			throw new StorageException("Cannot update a non-existent chunk");
		try(ODatabaseSession sess = dbPool.acquire()) {
			OResultSet results = sess.command("UPDATE Topic SET name=?, content=?, lang=? WHERE @rid=?",
					chunk.getName(),
					chunk.getContent(),
					chunk.getLang(),
					chunkId.getChunkId());
			results.close();
			sess.commit();
		}
	}
	
	@Override
	public ChunkId postContent(Chunk chunk) {
		LOGGER.debug("Posting a chunk with name "+chunk.getName());

		try(ODatabaseSession sess = dbPool.acquire()) {
//			ODatabaseRecordThreadLocal.instance().set(sess);
//			OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("INSERT INTO Topic(name,content,lang) VALUES(?,?,?)");
			OResultSet oResults = sess.command("INSERT INTO Topic(name,content,lang) VALUES(?,?,?)", chunk.getName(),chunk.getContent(),chunk.getLang());
			String rid = null; 
			if(oResults.hasNext()) {
				OResult result = oResults.next();
				ORecordId recId = result.getProperty("@RID");
				rid = recId.getIdentity().toString();
				LOGGER.debug("RID IS NOW: {}",rid);
			}
			oResults.close();
			sess.commit();
			return rid == null ? null : new ChunkId(rid);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.giantelectronicbrain.catfood.store.ICatFoodDBStore#getJsonContent(com.giantelectronicbrain.catfood.model.ChunkId)
	 */
	@Override
	public String getJsonContent(ChunkId id) {
		LOGGER.debug("Getting Content for id "+id);
		
		try(ODatabaseSession sess = dbPool.acquire()) {
//			ODatabaseRecordThreadLocal.instance().set(db);
//			OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT FROM Topic WHERE @RID = ?");
//			List<ODocument> result = db.command(query).execute(id.getChunkId());
			OResultSet result = sess.command("SELECT FROM Topic WHERE @RID = ?",id.getChunkId());
			
			String json = result.hasNext() ? result.next().toJSON() : "";
			
			LOGGER.debug("IS THERE ANY MORE TO THIS RESULT SET {}",result.hasNext());
			
			LOGGER.debug("Got list of results of size {}",result.getExactSizeIfKnown());
			LOGGER.debug("Result 0 is {}",json);
			result.close();
	
			return json;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.giantelectronicbrain.catfood.store.ICatFoodDBStore#getContent(com.giantelectronicbrain.catfood.model.ChunkId)
	 */
	@Override
	public Chunk getContent(ChunkId id) {
		LOGGER.debug("Getting Content for id "+id);
		
		try(ODatabaseSession sess = dbPool.acquire()) {
			Chunk result = null;
//			ODatabaseRecordThreadLocal.instance().set(db);
//			OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT FROM Topic WHERE @RID = ?");
//			List<ODocument> dbResults = db.command(query).execute(id);
			OResultSet oResults = sess.command("SELECT FROM Topic WHERE @RID = ?", id);
			
			if(oResults.hasNext()) {
				OResult oResult = oResults.next();
				result = new Chunk(oResult.getProperty("content"),new ChunkId(oResult.getIdentity().toString()),oResult.getProperty("name"),Language.valueOf(oResult.getProperty("lang")));
				LOGGER.debug("Got list of results of size "+oResults.getExactSizeIfKnown());
				LOGGER.debug("Result 0 is "+result.toString());
			}
			
			oResults.close();
			return result;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.giantelectronicbrain.catfood.store.ICatFoodDBStore#getJsonTopic(java.lang.String)
	 */
	@Override
	public String getJsonChunk(String name) {
		LOGGER.debug("Getting Topic for name "+name);
		
		try(ODatabaseSession sess = dbPool.acquire()) {
//			ODatabaseRecordThreadLocal.instance().set(db);
//			OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>("SELECT FROM Topic WHERE name = ?");
//			List<ODocument> result = db.command(query).execute(name);

			OResultSet oResults = sess.query("SELECT FROM Topic WHERE name = ?", name);
			
			LOGGER.debug("Got results, we have at least one result {}",oResults.hasNext());
	
			String json = null;
			if(oResults.hasNext()) {
				OResult res = oResults.next();
				LOGGER.debug("Got a result object of {}",res);
				OElement element = res.toElement();
				LOGGER.debug("Got element object of {}",element);
				json = element.toJSON();
				LOGGER.debug("JSON is {}",json);
			}
			oResults.close();
			return json;
		}
	}

}
