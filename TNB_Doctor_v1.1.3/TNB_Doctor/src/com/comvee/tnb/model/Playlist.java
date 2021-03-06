/*
 * Copyright (C) 2009 Teleca Poland Sp. z o.o. <android@teleca.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.comvee.tnb.model;
import java.io.Serializable;
import java.util.ArrayList;


/**
 * @author Lukasz Wisniewski
 */
public class Playlist implements Serializable{
	
	/**
	 * 
	 */
	

	/**
	 * Keeps playlist's entries
	 */
	private ArrayList<StoryItemInfo> playlist;
	
	/**
	 * Keeps record of currently selected track
	 */
	private int selected = -1;
	
	public Playlist(){
		this(new ArrayList<StoryItemInfo>());
	}
	
	public Playlist(ArrayList<StoryItemInfo> list){
		playlist = list ;
	}
	
	/**
	 * Add single track to the playlist
	 * 
	 * @param track <code>Track</code> instance
	 * @param album <code>Album</code> instance
	 */
	public void addTrack(StoryItemInfo track) {
		playlist.add(track);
	}
	
	
	/**
	 * Checks if the playlist is empty 
	 * 
	 * @return boolean value
	 */
	public boolean isEmpty(){
		return playlist.size() == 0;
	}
	
	/**
	 * Selects next song from the playlist
	 */
	public void selectNext(){
		if(!isEmpty()){
			selected++;
			selected %= playlist.size();
		}
	}
	
	/**
	 * Selects previous song from the playlist
	 */
	public void selectPrev(){
		if(!isEmpty()){
			selected--;
			if(selected < 0)
				selected = playlist.size() - 1;
		}
	}
	
	/**
	 * Select song with a given index
	 * 
	 * @param index
	 */
	public void select(int index){
		if(!isEmpty()){
			if(index >= 0 && index < playlist.size())
				selected = index;
		}
	}
	
	public void selectOrAdd(StoryItemInfo track){
		
		// first search thru available tracks
		for(int i=0; i<playlist.size(); i++){
			if(playlist.get(i).getId() == track.getId()){
				select(i);
				return;
			}
		}
		
		// add track if necessary
		addTrack(track);
		select(playlist.size()-1);
	}
	
	/**
	 * Return index of the currently selected song
	 * 
	 * @return int value (-1 if the playlist is empty)
	 */
	public int getSelectedIndex(){
		if(isEmpty()){
			selected = -1;
		}
		if(selected == -1 && !isEmpty()){
			selected = 0;
		}
		return selected;
	}
	
	/**
	 * Return currently selected song
	 * 
	 * @return <code>PlaylistEntry</code> instance
	 */
	public StoryItemInfo getSelectedTrack(){
		StoryItemInfo playlistEntry = null;
		
		if(!isEmpty()){
			playlistEntry = playlist.get(getSelectedIndex());
		}
		
		return playlistEntry;
		
	}
	
	/**
	 * Adds PlaylistEntry object to the playlist
	 * 
	 * @param playlistEntry
	 */
	public void addPlaylistEntry(StoryItemInfo playlistEntry){
		playlist.add(playlistEntry);
	}
	
	/**
	 * Count of playlist entries
	 * 
	 * @return
	 */
	public int size(){
		return playlist == null ? 0 : playlist.size();
	}
	
	/**
	 * Given track index getter
	 * 
	 * @param index
	 * @return
	 */
	public StoryItemInfo getTrack(int index){
		return playlist.get(index);
	}

	/**
	 * Remove a track with a given index from the playlist
	 * 
	 * @param position
	 */
	public void remove(int position) {
		if(playlist != null && position < playlist.size() && position >= 0){
			
			if(selected >= position){
				selected--;
			}
			
			playlist.remove(position);
		}
		
	}
}
