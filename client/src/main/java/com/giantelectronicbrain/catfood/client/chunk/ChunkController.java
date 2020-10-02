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
package com.giantelectronicbrain.catfood.client.chunk;

import com.giantelectronicbrain.catfood.client.Router;
import com.giantelectronicbrain.catfood.client.chunk.Chunk.Language;
import com.giantelectronicbrain.catfood.client.fluent.Fluent;
import com.giantelectronicbrain.catfood.client.fluent.ViewOn;

/**
 * The Controller for Chunks.
 * 
 * @author tharter
 *
 */
public class ChunkController {
	private Fluent root;			// The root object where our UI lives
	private ViewOn<Chunk> view;		// The ViewOn which is displaying the UI
	private Repository chunkRepo;	// Repo which supplies chunks
	private boolean editing = false;// If true then we are displaying editor mode
	private Router router;			// The router for this application
	
	/**
	 * Start up with no content, a default view will be displayed, either the editor or
	 * the chunk viewer depending on the value of editing. The topic with the given name
	 * will be loaded from the server. Once it finishes loading it will be displayed.
	 * 
	 * @param root the Fluent which holds the associated view.
	 * @param topic the topic to load by name.
	 * @param editing true if the editor should be displayed, false otherwise
	 */
	public ChunkController(Router router, Repository chunkRepo, Fluent root, String name, boolean editing) {
		this.root = root;
		this.editing = editing;
		this.chunkRepo = chunkRepo;
		this.router = router;
		showLoading(name);
		load(name);
	}

	
	/**
	 * Start up with the given content, either in editor mode or not as specified.
	 * 
	 * @param root the Fluent which holds the associated view.
	 * @param model the chunk to display
	 * @param editing true if the editor should be displayed, false otherwise
	 */
	public ChunkController(Router router, Repository chunkRepo, Fluent root, Chunk model, boolean editing) {
		this.root = root;
		this.editing = editing;
		this.chunkRepo = chunkRepo;
		this.router = router;
		if(editing)
			this.view = ChunkEditorViewFactory.createChunkView(model,root,this::saveHandler, this::cancelHandler);
		else
			this.view = ChunkViewFactory.createChunkView(model,root,this::editHandler);
	}

	/**
	 * Start up with the given content, either in editor mode or not as specified.
	 * 
	 * @param root the Fluent which holds the associated view.
	 * @param model the chunk to display
	 * @param editing true if the editor should be displayed, false otherwise
	 */
	public ChunkController(Router router, Repository chunkRepo, Fluent root, ChunkId chunkId, boolean editing) {
		this.root = root;
		this.editing = editing;
		this.chunkRepo = chunkRepo;
		this.router = router;
		if(editing)
			edit(chunkId);
//			this.view = ChunkEditorViewFactory.createChunkView(model,root,this::saveHandler);
		else
			view(chunkId);
//			this.view = ChunkViewFactory.createChunkView(model,root,this::editHandler);
	}
	
	/**
	 * Display an empty view. This will always come up in editor mode.
	 * 
	 * @param root the Fluent which holds the associated view.
	 */
	public ChunkController(Router router, Repository chunkRepo, Fluent root) {
		this.chunkRepo = chunkRepo;
		this.router = router;
		this.view = ChunkEditorViewFactory.createChunkView(root,this::saveHandler, this::cancelHandler);
	}

	private void cancelHandler(Chunk chunk) {
		if(!editing) { // this shouldn't really happen.
			Fluent.console.log("Cannot cancel, not editing");
			return;
		}
		cancel(chunk);
	}

	private void cancel(Chunk chunk) {
		router.pushState(null, null, "/view/id/"+chunk.getChunkId().getChunkId());
	}
	
	/**
	 * Handle save button press in editor mode.
	 * 
	 * @param fluent the top level element of the control
	 * @param event the UIEvent
	 */
	@SuppressWarnings("deprecation")
	private void saveHandler(Chunk newChunk) { 
		if(!editing) { // this shouldn't really happen.
			Fluent.console.log("Cannot save, not editing!");
			return;
		}
		save(newChunk);
		//TODO: go back to the regular view. We need a router for this...
	}

	private void save(Chunk chunk) {
//		Fluent.window.alert("We are saving :"+chunk);
		if(chunk.getChunkId() == null) {
			this.chunkRepo.saveChunk(chunk,(status,chunkId) -> {
				if(status != 200) {
					displayError("Failed to save chunk ","Chunk name was: "+chunk.getName());
				}
			});
		} else {
			this.chunkRepo.updateChunk(chunk,(status,chunkId) -> {
				if(status != 200) {
					displayError("Failed to update chunk ","Chunk id was: "+chunk.getChunkId().getChunkId());
				}
			});
		}
	}
	
	/**
	 * Switch to editing mode. If a chunk is supplied, then edit it, otherwise
	 * edit the current chunk.
	 * 
	 * @param chunk a new chunk, or null for current chunk
	 */
	public void edit(Chunk chunk) {
		Chunk state = getState();
		if((chunk == null || chunk.equals(state)) && this.editing) return; // we are already editing this chunk
		if(chunk == null)
			chunk = state;
		ViewOn<Chunk> oldView = this.view;
		this.editing = true;
		if(oldView != null) oldView.delete();
		this.view = ChunkEditorViewFactory.createChunkView(chunk,root,this::saveHandler, this::cancelHandler);
		Fluent.console.log("GOT HERE "+chunk);
	}

	public void edit(String name) {
		if(name.equals(getStateName()) && !this.editing) return; // already viewing this chunk
		ViewOn<Chunk> oldView = this.view;
		if(oldView != null) oldView.delete();
		this.editing = true;
		showLoading(name);
		load(name);
	}

	public void edit(ChunkId chunkId) {
		if(chunkId.equals(getStateId()) && !this.editing) return; // already viewing this chunk
		ViewOn<Chunk> oldView = this.view;
		if(oldView != null) oldView.delete();
		this.editing = true;
		showLoading(chunkId.getChunkId());
		load(chunkId);
	}
	
	private Chunk getState() {
		if(this.view == null) return null;
		return this.view.state();
	}
	
	private String getStateName() {
		Chunk chunk = getState();
		return chunk == null ? null : chunk.getName();
	}
	
	private ChunkId getStateId() {
		Chunk chunk = this.getState();
		return chunk == null ? null : chunk.getChunkId();
	}
	
	/**
	 * Switch to viewing mode. If a chunk is supplied, then view it, otherwise
	 * view the current chunk.
	 * 
	 * @param chunk a new chunk, or null for current chunk
	 */
	public void view(Chunk chunk) {
		Chunk state = getState();
		if((chunk == null || chunk.equals(state)) && !this.editing) return; // we are already viewing this chunk
		if(chunk == null)
			chunk = state;
		ViewOn<Chunk> oldView = this.view;
		this.editing = false;
		if(oldView != null) oldView.delete();
		this.view = ChunkViewFactory.createChunkView(chunk, root, this::edit);
	}
	
	public void view(String name) {
		if(name.equals(getStateName()) && !this.editing) return; // already viewing this chunk
		ViewOn<Chunk> oldView = this.view;
		this.editing = false;
		if(oldView != null) oldView.delete();
		showLoading(name);
		load(name);
	}

	public void view(ChunkId chunkId) {
Fluent.console.log("GOT TO VIEW BY CHUNK ID WITH ID OF:",chunkId);
		if(chunkId.equals(getStateId()) && !this.editing) return; // already viewing this chunk
		ViewOn<Chunk> oldView = this.view;
Fluent.console.log("OLD VIEW IS: ",oldView);
		this.editing = false;
		if(oldView != null) oldView.delete();
		showLoading(chunkId.getChunkId());
		load(chunkId);
Fluent.console.log("LOADED CHUNK WITH ID ",chunkId);
	}
	
	private void showLoading(String name) {
		if(name == null || name.isEmpty()) name = "Loading";
		Chunk chunk = new Chunk("loading...",null,name,Language.MARKDOWN);
		if(editing) {
			this.view = ChunkEditorViewFactory.createChunkView(chunk,root,this::saveHandler, this::cancelHandler);
			this.view.getView().disabled(true);
		} else {
			this.view = ChunkViewFactory.createChunkView(chunk,root,this::editHandler);
		}
	}

	/**
	 * Handle the pressing of the edit button by telling the router to go to the 
	 * editing route.
	 * 
	 * @param chunk the chunk to edit
	 */
	private void editHandler(Chunk chunk) {
		this.editing = true;
		Fluent.console.log("GOING TO EDIT CHUNK "+chunk);
		router.pushState(chunk, "editing", "/edit/id/"+chunk.getChunkId().getChunkId());
	}
	
	/**
	 * Load data for a chunk by name from the chunk repo, with the updated handler being called
	 * when the task finishes.
	 * 
	 * @param name name of the chunk to load
	 */
	public void load(String name) {
		chunkRepo.findChunkByName(name, this::updated);
	}

	/**
	 * Load data for a chunk by id from the chunk repo, with the updated handler being called
	 * when the task finishes
	 * 
	 * @param chunkId if of the chunk to load
	 */
	private void load(ChunkId chunkId) {
		chunkRepo.getChunk(chunkId, this::updated);		
	}

	/**
	 * Signal the view that the model updated.
	 * 
	 * @param newModel
	 */
	private void updated(Chunk newModel) {
		view.state(newModel);
	}
	
	private void displayError(String title, String description) {
		Fluent.window.alert(title+"\n\n"+description);
	}
	/*
	 * handler for responses from the repository.
	 */
	private void updated(Integer status, Chunk newModel) {
		if(newModel != null)
			Fluent.console.log("we got an updated thingy "+newModel.toString()+", status was "+status);
		else
			Fluent.console.log("load failed, chunk is null, status is "+status);
Fluent.console.log("WTF IS STATUS ",status);
		if(status == 200) {
			this.view.getView().disabled(false); // re-enable the view if it was disabled
			updated(newModel);
		} else {
			displayError("Failed to Load","Got an error from server with status: "+status);
		}
	}

}
